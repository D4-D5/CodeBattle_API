package com.example.CodeWar.dto;

import lombok.Data;

@Data
public class Judge0CreateSubmission {
    private int language_id;
    private String source_code;
    private String stdin = "";
    private String expected_output = "";
}
