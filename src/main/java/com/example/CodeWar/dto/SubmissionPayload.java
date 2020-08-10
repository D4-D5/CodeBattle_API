package com.example.CodeWar.dto;

import lombok.Data;

@Data
public class SubmissionPayload {
    private long problemId;
    private String codeBattleId;
    private String sourceCode;
    private String roomId;
    private int languageId;
}
