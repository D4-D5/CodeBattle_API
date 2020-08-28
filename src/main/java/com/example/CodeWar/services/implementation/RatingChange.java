package com.example.CodeWar.services.implementation;

//import com.example.CodeWar.CodeforcesRatingCalculator2;
import com.example.CodeWar.app.ContestStatus;
import com.example.CodeWar.app.ContestantComparator;
import com.example.CodeWar.dto.Contestant;
import com.example.CodeWar.model.Lobby;
import com.example.CodeWar.model.User;
import com.example.CodeWar.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RatingChange {

    private static final Logger logger = LoggerFactory.getLogger(RatingChange.class);
    @Autowired
    private UserRepository  userRepository;

    @Autowired
    private MongoOperations mongoOperations;

    void updateRating(Set<Contestant> contestants, String roomId) {

        List<Contestant> contestantList = new ArrayList<>(contestants);
        sortByNumSolvedAndPenalty(contestantList);
        assignRanks(contestantList);

        List<UserContestant> userContestantList = new ArrayList<>(contestants.size());

        for (Contestant contestant : contestantList) {
            userContestantList.add(new UserContestant(contestant));
        }

        process(userContestantList);

        Set<Contestant> contestantSet = new HashSet<>();

        for (UserContestant contestant : userContestantList) {
            contestant.contestant.setRating(contestant.contestant.getRating()+contestant.contestant.getDelta());
            contestantSet.add(contestant.contestant);
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(roomId));
        Update update = new Update();
        update.set("contestants", contestantSet);
        mongoOperations.updateFirst(query,update, Lobby.class);

        for(Contestant contestant:contestantSet){
            User user = userRepository.findByCodeBattleId(contestant.getCodeBattleId());
            user.setRating(contestant.getRating());
            user.setMaxRating(Math.max(user.getRating(), user.getMaxRating()));
            userRepository.save(user);
        }

        logger.info("The Rating change {}",contestantSet);
    }

    private void assignRanks(List<Contestant> contestantList) {
        int rank = 1;
        for (Contestant contestant : contestantList) {
            contestant.setRank(rank);
            rank += 1;
        }
    }

    private void sortByNumSolvedAndPenalty(List<Contestant> contestantList) {
        Comparator<Contestant> comparator = Collections.reverseOrder(new ContestantComparator());
        contestantList.sort(comparator);
    }

    private void process(List<UserContestant> contestants) {
        if (contestants.isEmpty()) {
            return;
        }

        for (UserContestant a : contestants) {
            a.seed = 1;
            for (UserContestant b : contestants) {
                if (a != b) {
                    a.seed += getEloWinProbability(b, a);
                }
            }
        }

        for (UserContestant userContestant : contestants) {
            double midRank = Math.sqrt(userContestant.contestant.getRank() * userContestant.seed);
            userContestant.needRating = getRatingToRank(contestants, midRank);
            userContestant.contestant.setDelta((userContestant.needRating - userContestant.contestant.getRating()) / 2);
        }

        sortByRatingDesc(contestants);

        // Total sum should not be more than zero.
        {
            int sum = 0;
            for (UserContestant c : contestants) {
                sum += c.contestant.getDelta();
            }
            int inc = -sum / contestants.size() - 1;
            for (UserContestant userContestant : contestants) {
                userContestant.contestant.setDelta(userContestant.contestant.getDelta()+inc);
            }
        }

        // Sum of top-4*sqrt should be adjusted to zero.
        {
            int sum = 0;
            int zeroSumCount = Math.min((int) (4 * Math.round(Math.sqrt(contestants.size()))), contestants.size());
            for (int i = 0; i < zeroSumCount; i++) {
                sum += contestants.get(i).contestant.getDelta();
            }
            int inc = Math.min(Math.max(-sum / zeroSumCount, -10), 0);
            for (UserContestant userContestant : contestants) {
                userContestant.contestant.setDelta(userContestant.contestant.getDelta()+inc);
            }
        }
        List<Contestant> contestantList = new ArrayList<>();
        for(UserContestant userContestant:contestants){
            contestantList.add(userContestant.contestant);
        }
        sortByNumSolvedAndPenalty(contestantList);
        validateDeltas(contestantList);
    }

    private void validateDeltas(List<Contestant> contestants) {

        for (int i = 0; i < contestants.size(); i++) {
            for (int j = i + 1; j < contestants.size(); j++) {
                if (contestants.get(i).getRating() > contestants.get(j).getRating()) {
                    ensure(contestants.get(i).getRating() + contestants.get(i).getDelta() >= contestants.get(j).getRating() + contestants.get(j).getDelta(),
                            "First rating invariant failed: " + contestants.get(i).getCodeBattleId() + " vs. " + contestants.get(j).getCodeBattleId() + ".");
                }
                if (contestants.get(i).getRating() < contestants.get(j).getRating()) {
                    if (contestants.get(i).getDelta() < contestants.get(j).getDelta()) {
                        System.out.println(1);
                    }
                    ensure(contestants.get(i).getDelta() >= contestants.get(j).getDelta(),
                            "Second rating invariant failed: " + contestants.get(i).getCodeBattleId() + " vs. " + contestants.get(j).getCodeBattleId() + ".");
                }
            }
        }
    }

    private void ensure(boolean b, String message) {
        if (!b) {
            throw new RuntimeException(message);
        }
    }

    private int getRatingToRank(List<UserContestant> contestants, double rank) {
        int left = 0;
        int right = 2400;

        while (right - left > 1) {
            int mid = (left + right) / 2;

            if (getSeed(contestants, mid) < rank) {
                right = mid;
            } else {
                left = mid;
            }
        }

        return left;
    }

    private double getSeed(List<UserContestant> contestants, int rating) {
        UserContestant extraContestant = new UserContestant(new Contestant(rating));

        double result = 1;
        for (UserContestant other : contestants) {
            result += getEloWinProbability(other, extraContestant);
        }

        return result;
    }

    private double getEloWinProbability(UserContestant a, UserContestant b) {
        return getEloWinProbability(a.contestant.getRating(), b.contestant.getRating());
    }

    private static double getEloWinProbability(int ra, int rb) {
        return 1.0 / (1 + Math.pow(10, (rb - ra) / 400.0));
    }

    private void sortByRatingDesc(List<UserContestant> contestants) {
        contestants.sort(new Comparator<UserContestant>() {
            @Override
            public int compare(UserContestant o1, UserContestant o2) {
                return -Integer.compare(o1.contestant.getRating(), o2.contestant.getRating());
            }
        });
    }


    private static final class UserContestant {
        private Contestant contestant;
        private int needRating;
        private double seed;

        private UserContestant(Contestant contestant) {
            this.contestant = contestant;
        }
    }
}
