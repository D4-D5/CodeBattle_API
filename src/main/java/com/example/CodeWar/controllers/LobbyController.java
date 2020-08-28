package com.example.CodeWar.controllers;

import com.example.CodeWar.app.Constants;
import com.example.CodeWar.dto.ContestantPayload;
import com.example.CodeWar.dto.CreateLobbyPayload;
import com.example.CodeWar.dto.LoginPayload;
import com.example.CodeWar.dto.StartContestPayload;
import com.example.CodeWar.services.LobbyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import static com.example.CodeWar.app.Constants.STATUS_FAILURE;

@RestController
@RequestMapping("/api")
public class LobbyController {
    private static final Logger logger = LoggerFactory.getLogger(LobbyController.class);

    @Autowired
    private LobbyService lobbyService;

    @PostMapping(path = "/createLobby")
    public ResponseEntity<Map<String, Object>> createLobby(Principal principal, @RequestBody CreateLobbyPayload createLobbyPayload) {
        logger.info("create lobby Request with request object:{}", createLobbyPayload);
        logger.info("{}",principal);
        logger.info(principal.getName());
        createLobbyPayload.setOwner(principal.getName());
        Map<String, Object> response = lobbyService.createLobby(createLobbyPayload);
        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = "/addContestant")
    public ResponseEntity<Map<String, Object>> addContestant(Principal principal,@RequestBody ContestantPayload contestantPayload) {
        logger.info("add contestant Request with request object:{}", contestantPayload);
        contestantPayload.setCodeBattleId(principal.getName());
        Map<String, Object> response = lobbyService.addContestant(contestantPayload);
        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = "/getContestants")
    public ResponseEntity<Map<String, Object>> getContestants(@RequestParam String roomId) {
//        logger.info("get contestants Request with request object:{}", contestantPayload);
        Map<String, Object> response = lobbyService.getContestants(roomId);
        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
