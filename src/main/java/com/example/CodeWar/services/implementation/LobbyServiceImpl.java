package com.example.CodeWar.services.implementation;

import com.example.CodeWar.app.ContestStatus;
import com.example.CodeWar.app.ContestType;
import com.example.CodeWar.dto.*;
import com.example.CodeWar.model.Lobby;
import com.example.CodeWar.model.User;
import com.example.CodeWar.repositories.LobbyRepository;
import com.example.CodeWar.repositories.UserRepository;
import com.example.CodeWar.services.LobbyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.example.CodeWar.app.Constants.*;

@Service
public class LobbyServiceImpl implements LobbyService {

    private static final Logger logger = LoggerFactory.getLogger(LobbyServiceImpl.class);

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Map<String, Object> createLobby(CreateLobbyPayload createLobbyPayload) {
        Map<String,Object> response = new HashMap<>();

        //Null check for the payload
        if(!verifyCreateLobbyPayload(createLobbyPayload,response)){
            response.put(STATUS,STATUS_FAILURE);
            return response;
        }

        //create instance of lobby
        Lobby lobby = new Lobby(createLobbyPayload);

        //set status to IDEAL
        lobby.setContestStatus(ContestStatus.IDLE);

        //create instance of contestant i.e add owner
        Contestant contestant = new Contestant(createLobbyPayload.getOwner());
        lobby.getContestants().add(contestant);

        //save the lobby to lobby repository
        lobbyRepository.save(lobby);

        response.put(MESSAGE,lobby);
        response.put(STATUS,STATUS_SUCCESS);
        return response;
    }

    private boolean verifyCreateLobbyPayload(CreateLobbyPayload createLobbyPayload, Map<String, Object> response) {
        if(Objects.isNull(createLobbyPayload)){
            response.put(REASON,PAYLOAD_IS_NULL);
        }
        if(Objects.isNull(createLobbyPayload.getRoomId()) || StringUtils.isEmpty(createLobbyPayload.getRoomId())){
            response.put(REASON,ROOM_ID_NULL);
        }
        else{
            if(lobbyRepository.exitsByRoomId(createLobbyPayload.getRoomId())){
                response.put(REASON,ROOM_ID_IN_USE);
            }
        }
        if(Objects.isNull(createLobbyPayload.getOwner()) || StringUtils.isEmpty(createLobbyPayload.getOwner())){
            response.put(REASON,OWNER_IS_NULL);
        }
        else{
            if(!userRepository.existsByCodeBattleId(createLobbyPayload.getOwner())){
                response.put(REASON,CODEBATTLE_ID_NOT_FOUND);
            }
        }
        if(Objects.isNull(createLobbyPayload.getContestType())){
            response.put(REASON,CONTEST_TYPE_IS_NULL);
        }
        return !response.containsKey(REASON);
    }

    @Override
    public Map<String, Object> addContestant(ContestantPayload contestantPayload) {
        Map<String,Object> response = new HashMap<>();

        if(!verifyContestantPayload(contestantPayload,response)){
            response.put(STATUS,STATUS_FAILURE);
            return response;
        }

        Contestant contestant = new Contestant(contestantPayload.getCodeBattleId());

        Query query = new Query();
        query.addCriteria(Criteria.where("roomId").is(contestantPayload.getRoomId()));

        Lobby lobby = mongoOperations.findOne(query,Lobby.class);

        if(!Objects.isNull(lobby)){

            Update update = new Update();
            Set<Contestant> contestantSet = lobby.getContestants();
            contestantSet.add(contestant);
            update.set("contestants",contestantSet);
            update.set("startTime",System.currentTimeMillis());

            mongoOperations.updateFirst(query,update,Lobby.class);

            logger.info("This is contestant {}",contestant);
            logger.info("This is lobby {}",lobby);
            response.put(STATUS,STATUS_SUCCESS);
        }
        else{
            response.put(REASON,ROOM_ID_NOT_FOUND);
            response.put(STATUS,STATUS_FAILURE);
        }
        return response;
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

    @Override
    public Map<String, Object> getContestants(String roomId) {
        Map<String,Object> response = new HashMap<>();

        if(Objects.isNull(roomId) || StringUtils.isEmpty(roomId)){
            response.put(REASON,ROOM_ID_NULL);
            response.put(STATUS,STATUS_FAILURE);
            return response;
        }

        Lobby lobby = lobbyRepository.findByRoomId(roomId);

        if(!Objects.isNull(lobby)){
            logger.info("This is lobby {}",lobby);
            List<String> list  = new ArrayList<>();
            response.put(MESSAGE,lobby.getContestants());
            response.put(STATUS,STATUS_SUCCESS);
        }
        else{
            response.put(REASON,ROOM_ID_NOT_FOUND);
            response.put(STATUS,STATUS_FAILURE);
        }
        return response;
    }

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

            mongoOperations.updateFirst(query,update,Lobby.class);
            logger.info("This is lobby {}",lobby);
            response.put(STATUS,STATUS_SUCCESS);
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
        return null;
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
}
