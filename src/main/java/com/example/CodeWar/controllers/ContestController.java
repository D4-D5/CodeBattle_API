package com.example.CodeWar.controllers;


import com.example.CodeWar.app.Constants;
import com.example.CodeWar.dto.ContestantPayload;
import com.example.CodeWar.dto.StartContestPayload;
import com.example.CodeWar.dto.SubmissionPayload;
import com.example.CodeWar.services.ContestService;
import com.example.CodeWar.services.LobbyService;
//import com.mashape.unirest.http.exceptions.UnirestException;
import kong.unirest.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

import static com.example.CodeWar.app.Constants.STATUS_FAILURE;

@RestController
@RequestMapping("/api")
public class ContestController {
    private static final Logger logger = LoggerFactory.getLogger(ContestController.class);

    @Autowired
    private ContestService contestService;

    @PostMapping(path = "/startContest")
    public ResponseEntity<Map<String, Object>> startContest(@RequestBody StartContestPayload startContestPayload) {
        logger.info("start contest with request object:{}", startContestPayload);
        Map<String, Object> response = contestService.startContest(startContestPayload);
        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = "/getLeaderboard")
    public ResponseEntity<Map<String, Object>> getLeaderboard(@RequestParam String roomId) {
//        logger.info("get Leaderboard Request with request object:{}", contestantPayload);
        Map<String, Object> response = contestService.getLeaderboard(roomId);
        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = "/getContestQuestions")
    public ResponseEntity<Map<String, Object>> getContestQuestions(@RequestBody ContestantPayload contestantPayload) throws IOException {
//        logger.info("get Leaderboard Request with request object:{}", contestantPayload);
        Map<String, Object> response = contestService.getContestQuestions(contestantPayload);
        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = "/submitProblem")
    public ResponseEntity<Map<String, Object>> submitProblem(@RequestBody SubmissionPayload submissionPayload) throws UnirestException {
//        logger.info("get Leaderboard Request with request object:{}", contestantPayload);
        Map<String, Object> response = contestService.submitProblem(submissionPayload);
        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
