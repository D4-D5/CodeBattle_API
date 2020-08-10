package com.example.CodeWar.services;

import com.example.CodeWar.dto.ContestantPayload;
import com.example.CodeWar.dto.StartContestPayload;
import com.example.CodeWar.dto.SubmissionPayload;
import kong.unirest.UnirestException;
//import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.Map;

public interface ContestService {
    Map<String, Object> startContest(StartContestPayload startContestPayload);

    Map<String, Object> getLeaderboard(String roomId);

    Map<String, Object> getContestQuestions(ContestantPayload contestantPayload) throws IOException;

    Map<String, Object> submitProblem(SubmissionPayload submissionPayload) throws UnirestException;
}
