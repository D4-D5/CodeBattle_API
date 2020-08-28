package com.example.CodeWar.controllers;


import com.example.CodeWar.app.Constants;
import com.example.CodeWar.dto.ContestantPayload;
import com.example.CodeWar.dto.StartContestPayload;
import com.example.CodeWar.dto.SubmissionPayload;
import com.example.CodeWar.services.ContestService;
import com.example.CodeWar.services.LobbyService;
//import com.mashape.unirest.http.exceptions.UnirestException;
import it.zielke.moji.MossException;
import it.zielke.moji.SocketClient;
import kong.unirest.UnirestException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.example.CodeWar.app.Constants.*;

@RestController
@RequestMapping("/api")
public class ContestController {
    private static final Logger logger = LoggerFactory.getLogger(ContestController.class);

    @Autowired
    private ContestService contestService;

//    @Autowired
//    private SocketClient socketClient;

    @PostMapping(path = "/startContest")
    public ResponseEntity<Map<String, Object>> startContest(Principal principal, @RequestBody StartContestPayload startContestPayload) {
        logger.info("start contest with request object:{}", startContestPayload);
        startContestPayload.setOwner(principal.getName());
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
    public ResponseEntity<Map<String, Object>> getContestQuestions(Principal principal,@RequestBody ContestantPayload contestantPayload) throws IOException {
        logger.info("get contest questions with request object:{}", contestantPayload);
        contestantPayload.setCodeBattleId(principal.getName());
        logger.info(principal.getName());
        Map<String, Object> response = contestService.getContestQuestions(contestantPayload);
        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = "/submitProblem")
    public ResponseEntity<Map<String, Object>> submitProblem(Principal principal,@RequestBody SubmissionPayload submissionPayload) throws UnirestException {
//        logger.info("get Leaderboard Request with request object:{}", contestantPayload);
        submissionPayload.setCodeBattleId(principal.getName());
        Map<String, Object> response = contestService.submitProblem(submissionPayload);
        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = "/endContestTemp")
    public ResponseEntity<Map<String, Object>> endContestTemp(@RequestParam String roomId) {
//        logger.info("get Leaderboard Request with request object:{}", contestantPayload);
        Map<String, Object> response = new HashMap<>();
        response.put(STATUS,STATUS_SUCCESS);
                contestService.endContest(roomId);
        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = "/endContestForUser")
    public ResponseEntity<Map<String, Object>> endContestForUser(Principal principal,@RequestBody ContestantPayload contestantPayload) {
        logger.info("end contest Request with request object:{}", contestantPayload);
        contestantPayload.setCodeBattleId(principal.getName());
        Map<String, Object> response = contestService.endContestForUser(contestantPayload);
        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = "/checkP")
    public ResponseEntity<Map<String, Object>> checkP() {
        Map<String, Object> response = new HashMap<>();
        getCheckP();
        response.put(STATUS,STATUS_SUCCESS);
        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private void getCheckP(){
        // a list of students' source code files located in the prepared
        // directory.
        Collection<File> files = FileUtils.listFiles(new File(
                "/media/mohit/1AB6DA39B6DA155B/Coding/CP_Practice/CSES/solution_directory"), new String[] { "cpp" }, true);

        // a list of base files that was given to the students for this
        // assignment.
        Collection<File> baseFiles = FileUtils.listFiles(new File(
                "/media/mohit/1AB6DA39B6DA155B/Coding/CP_Practice/CSES/base_directory"), new String[] { "cpp" }, true);
        try {
            SocketClient socketClient = new SocketClient();
            //set your Moss user ID
            socketClient.setUserID("243096311");
            //socketClient.setOpt...

            //set the programming language of all student source codes
            socketClient.setLanguage("cc");

            //initialize connection and send parameters
            socketClient.run();

            // upload all base files
            for (File f : baseFiles) {
                socketClient.uploadBaseFile(f);
            }

            //upload all source files of students
            for (File f : files) {
                socketClient.uploadFile(f);
            }

            //finished uploading, tell server to check files
            socketClient.sendQuery();

            //get URL with Moss results and do something with it
            URL results = socketClient.getResultURL();
            System.out.println("Results available at " + results.toString());
            logger.info(socketClient.getServer());
        }
        catch (MossException e){
            logger.info("{}",e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
