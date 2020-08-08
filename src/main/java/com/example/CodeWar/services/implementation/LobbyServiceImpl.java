package com.example.CodeWar.services.implementation;

import com.example.CodeWar.app.ContestStatus;
import com.example.CodeWar.app.ContestType;
import com.example.CodeWar.app.DifficultyLevel;
import com.example.CodeWar.app.ProblemStatus;
import com.example.CodeWar.dto.*;
import com.example.CodeWar.model.Lobby;
import com.example.CodeWar.model.Problem;
import com.example.CodeWar.model.User;
import com.example.CodeWar.repositories.LobbyRepository;
import com.example.CodeWar.repositories.ProblemRepository;
import com.example.CodeWar.repositories.UserRepository;
import com.example.CodeWar.services.LobbyService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
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
import java.util.*;

import static com.example.CodeWar.app.Constants.*;

@Service
public class LobbyServiceImpl implements LobbyService {

    private static final Logger logger = LoggerFactory.getLogger(LobbyServiceImpl.class);
    private static final String ROOM_LISTENER = "/topic/";

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ProblemRepository problemRepository;

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
        lobbyRepository.insert(lobby);

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
            if(lobbyRepository.existsByRoomId(createLobbyPayload.getRoomId())){
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
            simpMessagingTemplate.convertAndSend(ROOM_LISTENER+contestantPayload.getRoomId(),contestantPayload.getCodeBattleId());

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
}
