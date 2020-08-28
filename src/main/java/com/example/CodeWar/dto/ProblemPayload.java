package com.example.CodeWar.dto;

import com.example.CodeWar.app.DifficultyLevel;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ProblemPayload {
    private long id;
    private String problemStatement;
    private String problemTitle;
    private String inputSpecification;
    private String outputSpecification;
    private String constraints;
    private int timeLimit;
    private int memoryLimit;
    private int acceptedSubmissions = 0;
    private int totalSubmissions = 0;
    private DifficultyLevel difficultyLevel;
    private String sampleInput;
    private String sampleOutput;
    private List<MultipartFile> fileInputTestCases;
    private List<MultipartFile> fileOutputTestCases;
    private String idealSolution;
    private int idealSolutionLanguageId;
    private String authorId;
    private String ioExplanation;
    private int maxCodeSize;
    private Set<String> tags = new HashSet<>();
}