package com.example.CodeWar.services;

import com.example.CodeWar.dto.ProblemPayload;

import java.util.Map;

public interface ProblemService {
    Map<String, Object> addProblem(ProblemPayload problemPayload);

    Map<String, Object> getProblems(String authorId);
}
