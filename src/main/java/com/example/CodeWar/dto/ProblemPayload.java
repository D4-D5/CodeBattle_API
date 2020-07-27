package com.example.CodeWar.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Set;

@Data
public class ProblemPayload {
    @Id
    @GeneratedValue
    private int id;
    private String problemStatement;
    private String problemTitle;
    private String inputSpecification;
    private String outputSpecification;
    private int timeLimit;
    private int memoryLimit;
    private int acceptedSubmissions = 0;
    private int totalSubmissions = 0;
    private String difficultyLevel;
    private MultipartFile fileSampleInput;
    private MultipartFile fileSampleOutput;
    private MultipartFile fileInputTestCase;
    private MultipartFile fileOutputTestCase;
    private MultipartFile fileIdealSolution;
    private String authorId;
    private String ioExplanation;
    private int maxCodeSize;
    private Set<String> tags;
}