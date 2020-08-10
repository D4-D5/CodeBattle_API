package com.example.CodeWar.services.implementation;

import com.example.CodeWar.app.ContestStatus;
import com.example.CodeWar.app.DifficultyLevel;
import com.example.CodeWar.app.ProblemStatus;
import com.example.CodeWar.dto.*;
import com.example.CodeWar.model.Lobby;
import com.example.CodeWar.model.Problem;
import com.example.CodeWar.model.User;
import com.example.CodeWar.repositories.LobbyRepository;
import com.example.CodeWar.repositories.ProblemRepository;
import com.example.CodeWar.repositories.UserRepository;
import com.example.CodeWar.services.ContestService;
//import com.mashape.unirest.http.HttpResponse;
//import com.mashape.unirest.http.Unirest;
//import com.mashape.unirest.http.exceptions.UnirestException;
//import com.mashape.unirest.request.HttpRequest;
//import com.mashape.unirest.request.body.RequestBodyEntity;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.example.CodeWar.app.Constants.*;
import static com.example.CodeWar.app.Constants.STATUS_FAILURE;

@Service
public class ContestServiceImpl implements ContestService {

    private static final Logger logger = LoggerFactory.getLogger(ContestServiceImpl.class);
    private static final String ROOM_LISTENER = "/topic/";


    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ProblemRepository problemRepository;

    @Override
    public Map<String, Object> startContest(StartContestPayload startContestPayload) {
        Map<String,Object> response = new HashMap<>();

        if(!verifyStartContestPayload(startContestPayload,response)){
            response.put(STATUS,STATUS_FAILURE);
            return response;
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(startContestPayload.getRoomId()));

        Lobby lobby = mongoOperations.findOne(query,Lobby.class);

        if(!Objects.isNull(lobby)){
            if(!lobby.getOwner().equals(startContestPayload.getOwner())){
                response.put(REASON,NOT_OWNER);
                response.put(STATUS,STATUS_FAILURE);
                return response;
            }
            if(lobby.getContestStatus().equals(ContestStatus.LIVE)){
                response.put(REASON,CONTEST_IS_ALREADY_LIVE);
                response.put(STATUS,STATUS_FAILURE);
                return response;
            }

            if(lobby.getContestStatus().equals(ContestStatus.ENDED)){
                response.put(REASON,CONTEST_ENDED);
                response.put(STATUS,STATUS_FAILURE);
                return response;
            }

            List<User> contestantList = new ArrayList<>();
            for (Contestant contestant:lobby.getContestants()) {
                User user = userRepository.findByCodeBattleId(contestant.getCodeBattleId());
                contestantList.add(user);
            }

            int contestRating = ComputeContestRating(contestantList);

            Update update = new Update();
            update.set("contestStatus", ContestStatus.LIVE);
            update.set("rating",contestRating);
            update.set("questions",getQuestionsForContest(contestRating,contestantList));
            update.set("startTime",System.currentTimeMillis());
            mongoOperations.updateFirst(query,update,Lobby.class);
            logger.info("This is lobby {}",lobbyRepository.findByRoomId(startContestPayload.getRoomId()));
            response.put(MESSAGE,lobbyRepository.findByRoomId(startContestPayload.getRoomId()));
            response.put(STATUS,STATUS_SUCCESS);
            simpMessagingTemplate.convertAndSend(ROOM_LISTENER+startContestPayload.getRoomId(),CONTEST_STARTED);
        }
        else{
            response.put(REASON,ROOM_ID_NOT_FOUND);
            response.put(STATUS,STATUS_FAILURE);
        }
        return response;
    }

    private boolean verifyStartContestPayload(StartContestPayload startContestPayload, Map<String, Object> response) {
        if(Objects.isNull(startContestPayload)){
            response.put(REASON,"will give");
        }
        if(Objects.isNull(startContestPayload.getRoomId()) || StringUtils.isEmpty(startContestPayload.getRoomId())){
            response.put(REASON,ROOM_ID_NULL);
        }
        if(Objects.isNull(startContestPayload.getOwner()) || StringUtils.isEmpty(startContestPayload.getOwner())){
            response.put(REASON,OWNER_IS_NULL);
        }
        else{
            if(!userRepository.existsByCodeBattleId(startContestPayload.getOwner())){
                response.put(REASON,CODEBATTLE_ID_NOT_FOUND);
            }
        }
        return !response.containsKey(REASON);
    }

    private Set<Long> getQuestionsForContest(int contestRating, List<User> contestantList) {
        logger.info(String.valueOf(contestRating));
        logger.info("list is {}",contestantList);

        int questionSetIndex = contestRating/300;
        String questionSetValue = CONTEST_QUESTION_SET.get(questionSetIndex);

        logger.info(String.valueOf(questionSetIndex));
        logger.info(questionSetValue);


        List<Long> problemsIdNotToTake = getProblemToIgnore(contestantList);
        List<Problem> problemList = getFilteredProblems(questionSetValue,problemsIdNotToTake);

        Set<Long> problemSet = new HashSet<>();
        for (Problem problem:problemList){
            problemSet.add(problem.getId());
            logger.info(problem.getDifficultyLevel()+"___"+problem.getProblemTitle());
        }
        return problemSet;
    }

    private List<Problem> getFilteredProblems(String questionSetValue, List<Long> problemsIdNotToTake) {
        List<Problem> problemList = new ArrayList<>();
        int numOfEasyQuestions = questionSetValue.charAt(0) - '0';
        if(numOfEasyQuestions!=0) {
            problemList.addAll(findProblemsFromRepo(numOfEasyQuestions, DifficultyLevel.EASY, problemsIdNotToTake));
        }
        int numOfMediumQuestions = questionSetValue.charAt(1) - '0';
        if(numOfMediumQuestions!=0) {
            problemList.addAll(findProblemsFromRepo(numOfMediumQuestions, DifficultyLevel.MEDIUM, problemsIdNotToTake));
        }
        int numOfHardQuestions = questionSetValue.charAt(2) - '0';
        if(numOfHardQuestions!=0) {
            problemList.addAll(findProblemsFromRepo(numOfHardQuestions, DifficultyLevel.HARD, problemsIdNotToTake));
        }
        return problemList;
    }

    private List<Problem> findProblemsFromRepo(int numOfQuestions, DifficultyLevel difficultyLevel, List<Long> problemsIdNotToTake) {
        switch (numOfQuestions){
            case 3:
                return problemRepository.findFirst3ByDifficultyLevelAndProblemStatusAndIdNotIn(difficultyLevel, ProblemStatus.PUBLISHED,problemsIdNotToTake);
            case 2:
                return problemRepository.findFirst2ByDifficultyLevelAndProblemStatusAndIdNotIn(difficultyLevel, ProblemStatus.PUBLISHED,problemsIdNotToTake);
            case 1:
                return problemRepository.findFirst1ByDifficultyLevelAndProblemStatusAndIdNotIn(difficultyLevel, ProblemStatus.PUBLISHED,problemsIdNotToTake);
            default:
                return new ArrayList<>();
        }
    }

    private List<Long> getProblemToIgnore(List<User> contestantList) {
        List<Long> problemsIdNotToTake = new ArrayList<>();
        for(User user:contestantList){
            for(Problem problem:user.getSolvedProblems()){
                logger.info("___________"+problem.getId()+"______"+problem.getProblemTitle()+"___________");
                problemsIdNotToTake.add(problem.getId());
            }
            for(Problem problem:problemRepository.getListOfProblemsByUserName(user.getCodeBattleId())){
                logger.info("___________"+problem.getId()+"______"+problem.getProblemTitle()+"___________");
                problemsIdNotToTake.add(problem.getId());
            }
        }
        return problemsIdNotToTake;
    }

    private int ComputeContestRating(List<User> contestantList) {
        int totalRating = 0;
        for(User user:contestantList){
            totalRating = totalRating + user.getRating();
        }
        return totalRating/contestantList.size();
    }


    @Override
    public Map<String, Object> getLeaderboard(String roomId) {
        return null;
    }

    @Override
    public Map<String, Object> getContestQuestions(ContestantPayload contestantPayload) throws IOException {
        Map<String,Object> response = new HashMap<>();

        if(!verifyContestantPayload(contestantPayload,response)){
            response.put(STATUS,STATUS_FAILURE);
            return response;
        }
        Lobby lobby = lobbyRepository.findByRoomId(contestantPayload.getRoomId());
        if(Objects.isNull(lobby)){
            response.put(REASON,ROOM_ID_NOT_FOUND);
            response.put(STATUS,STATUS_SUCCESS);
            return response;
        }
        if(!lobby.getContestStatus().equals(ContestStatus.LIVE)){
            response.put(REASON,CONTEST_NOT_LIVE);
            response.put(STATUS,STATUS_FAILURE);
            return response;
        }
        boolean checkContestant = false;
        for(Contestant contestant:lobby.getContestants()){
            if(contestant.getCodeBattleId().equals(contestantPayload.getCodeBattleId())){
                checkContestant = true;
                break;
            }
        }
        if(!checkContestant){
            response.put(REASON,CONTESTANT_NOT_ALLOWED);
            response.put(STATUS,STATUS_FAILURE);
            return response;
        }
        Set<Long> problemIdsList = lobby.getQuestions();
        List<ProblemResponse> problemResponseList = new ArrayList<>();
        try {
            for (Long id : problemIdsList) {
                Optional<Problem> problem = problemRepository.findById(id);
                if(problem.isPresent()) {
                    ProblemResponse problemResponse = new ProblemResponse(problem.get());
                    problemResponseList.add(problemResponse);
                }
                else{
                    response.put(STATUS,STATUS_FAILURE);
                    return response;
                }
            }
            logger.info("{}", problemResponseList);
            response.put(MESSAGE, problemResponseList);
            response.put(STATUS, STATUS_SUCCESS);
        }
        catch (IOException e){
            logger.info("{}",e);
            response.put(REASON,FILE_ERROR);
            response.put(STATUS,STATUS_FAILURE);
        }
        return response;
    }

    @Override
    public Map<String, Object> submitProblem(SubmissionPayload submissionPayload) throws UnirestException {
        Map<String,Object> response = new HashMap<>();
        if(!verifySubmissionPayload(submissionPayload,response)){
            response.put(STATUS,STATUS_FAILURE);
            return response;
        }
        Judge0CreateSubmission body = new Judge0CreateSubmission();
        try {
            body.setLanguage_id(submissionPayload.getLanguageId());
            body.setSource_code(submissionPayload.getSourceCode());
            Optional<Problem> problem = problemRepository.findById(submissionPayload.getProblemId());
            if (problem.isPresent()) {
                body.setStdin(Files.readString(Path.of(problem.get().getFileInputTestCase())));
                body.setExpected_output(Files.readString(Path.of(problem.get().getFileOutputTestCase())));
            } else {
                response.put(REASON,PROBLEM_TEST_INPUT_IS_NULL);
                response.put(STATUS,STATUS_FAILURE);
                return response;
            }
        }
        catch (IOException e){
            response.put(REASON,FILE_ERROR);
            response.put(STATUS,STATUS_FAILURE);
            return response;
        }
        logger.info("{}",body.toString());
        try {
            HttpResponse<JsonNode> httpResponse = Unirest.post("https://judge0.p.rapidapi.com/submissions")
                    .header("x-rapidapi-host", "judge0.p.rapidapi.com")
                    .header("x-rapidapi-key", "9d099c4f7fmsh89afa8cdfb965f3p123e5fjsn10b6969a328e")
                    .header("content-type", "application/json")
                    .header("accept", "application/json")
                    .body(body)
                    .asJson();
            logger.info("{}",httpResponse.getBody());
            String token = httpResponse.getBody().getObject().getString("token");
            logger.info(token);
            TimeUnit.MILLISECONDS.sleep(SLEEP_TIME);
            getSubmission(token,submissionPayload,response);
            if(response.containsKey(REASON)){
                response.put(STATUS,STATUS_FAILURE);
            }
            else {
                response.put(STATUS, STATUS_SUCCESS);
            }
        }
        catch (UnirestException | InterruptedException e){
            response.put(REASON,e);
            response.put(STATUS,STATUS_FAILURE);
        }
        return response;
    }

    private boolean verifySubmissionPayload(SubmissionPayload submissionPayload, Map<String, Object> response) {
        if(Objects.isNull(submissionPayload)){
            response.put(REASON,INVALID_ARGUMENT);
            return false;
        }
        if(Objects.isNull(submissionPayload.getCodeBattleId()) || submissionPayload.getCodeBattleId().isEmpty() || submissionPayload.getCodeBattleId().isBlank()){
            response.put(REASON,USERNAME_IS_NULL);
        }
        if(Objects.isNull(submissionPayload.getSourceCode()) || submissionPayload.getSourceCode().isEmpty() || submissionPayload.getSourceCode().isBlank()){
            response.put(REASON,SOURCECODE_IS_NULL);
        }
        if(Objects.isNull(submissionPayload.getRoomId()) || submissionPayload.getRoomId().isEmpty() || submissionPayload.getRoomId().isBlank()){
            response.put(REASON,ROOM_ID_NULL);
        }
        else{
            Lobby lobby = lobbyRepository.findByRoomId(submissionPayload.getRoomId());
            if(Objects.isNull(lobby)){
                response.put(REASON,ROOM_ID_NOT_FOUND);
            }
            else{
                if(!lobby.getQuestions().contains(submissionPayload.getProblemId())){
                    response.put(REASON,PROBLEM_NOT_FOUND);
                }
                if(lobby.getContestants().stream().noneMatch(contestant -> contestant.getCodeBattleId().equals(submissionPayload.getCodeBattleId()))){
                    response.put(REASON,CODEBATTLE_ID_NOT_FOUND);
                }
            }
        }
        return !response.containsKey(REASON);
    }

    private boolean getSubmission(String token, SubmissionPayload submissionPayload, Map<String, Object> response) {
        try {
            HttpResponse<JsonNode> httpResponse = Unirest.get("https://judge0.p.rapidapi.com/submissions/{token}?base64_encoded=true")
                    .header("x-rapidapi-host", "judge0.p.rapidapi.com")
                    .header("x-rapidapi-key", "9d099c4f7fmsh89afa8cdfb965f3p123e5fjsn10b6969a328e")
                    .routeParam("token", token)
                    .asJson();
            logger.info("()()===> {}", httpResponse.getBody());
            int statusId = httpResponse.getBody().getObject().getJSONObject("status").getInt("id");
            if (statusId <= 2) {
                TimeUnit.MILLISECONDS.sleep(SLEEP_TIME / 2);
                return getSubmission(token, submissionPayload, response);
            }
            if (statusId == 3) {
                updateScore(submissionPayload);
            } else if (statusId == 4) {
                addTimePenalty(submissionPayload);
            }

            String description = httpResponse.getBody().getObject().getJSONObject("status").getString("description");
            response.put(MESSAGE,description);
        }
        catch (UnirestException | InterruptedException e){
            response.put(REASON,e.getMessage());
        }
        return true;
    }

    private void addTimePenalty(SubmissionPayload submissionPayload) {
        Lobby lobby = lobbyRepository.findByRoomId(submissionPayload.getRoomId());
        Set<Contestant> oldContestantSet = lobby.getContestants();
        Set<Contestant> newContestantSet = new HashSet<>();
        Query query = new Query();
        query.addCriteria(Criteria.where("contestants").is(oldContestantSet));
        for(Contestant contestant:oldContestantSet){
            if(contestant.getCodeBattleId().equals(submissionPayload.getCodeBattleId())){
                Contestant newContestant = new Contestant(contestant);
                newContestant.setPenalty(newContestant.getPenalty()+PENALTY_TIME);
                newContestantSet.add(newContestant);
                logger.info("{}",contestant);
                logger.info("{}",newContestantSet);
            }
            else{
                newContestantSet.add(contestant);
            }
        }
        Update update = new Update();
        update.set("contestants",newContestantSet);
        mongoOperations.updateFirst(query,update,Lobby.class);
    }

    private void updateScore(SubmissionPayload submissionPayload) {
        Lobby lobby = lobbyRepository.findByRoomId(submissionPayload.getRoomId());
        boolean problemAlreadySolved = false;
        Set<Contestant> oldContestantSet = lobby.getContestants();
        Set<Contestant> newContestantSet = new HashSet<>();
        Query query = new Query();
        query.addCriteria(Criteria.where("contestants").is(oldContestantSet));
        for(Contestant contestant:oldContestantSet){
            if(contestant.getCodeBattleId().equals(submissionPayload.getCodeBattleId())){
                if(contestant.getSolvedQuestions().contains(submissionPayload.getProblemId())){
                    problemAlreadySolved = true;
                }
                else {
                    Contestant newContestant = new Contestant(contestant);
                    newContestant.getSolvedQuestions().add(submissionPayload.getProblemId());
                    newContestant.setNumSolved(newContestant.getSolvedQuestions().size());
                    newContestant.setPenalty(newContestant.getPenalty()+(System.currentTimeMillis()-lobby.getStartTime()));
                    newContestantSet.add(newContestant);
                }
                logger.info("{}",contestant);
                logger.info("{}",newContestantSet);
            }
            else{
                newContestantSet.add(contestant);
            }
        }
        if(!problemAlreadySolved) {
            Update update = new Update();
            update.set("contestants", newContestantSet);
            mongoOperations.updateFirst(query, update, Lobby.class);
            User user = userRepository.findByCodeBattleId(submissionPayload.getCodeBattleId());
            Optional<Problem> problem = problemRepository.findById(submissionPayload.getProblemId());
            if (!Objects.isNull(user) && problem.isPresent()) {
                user.getSolvedProblems().add(problem.get());
                userRepository.save(user);
            }
        }

    }

    private boolean verifyContestantPayload(ContestantPayload contestantPayload, Map<String, Object> response) {
        if(Objects.isNull(contestantPayload)){
            response.put(REASON,PAYLOAD_IS_NULL);
        }
        if(Objects.isNull(contestantPayload.getRoomId()) || StringUtils.isEmpty(contestantPayload.getRoomId())){
            response.put(REASON,ROOM_ID_NULL);
        }
        if(Objects.isNull(contestantPayload.getCodeBattleId()) || StringUtils.isEmpty(contestantPayload.getCodeBattleId())){
            response.put(REASON,OWNER_IS_NULL);
        }
        else{
            if(!userRepository.existsByCodeBattleId(contestantPayload.getCodeBattleId())){
                response.put(REASON,CODEBATTLE_ID_NOT_FOUND);
            }
        }
        return !response.containsKey(REASON);
    }

}
