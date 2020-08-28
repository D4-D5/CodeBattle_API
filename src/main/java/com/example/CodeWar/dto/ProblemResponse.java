package com.example.CodeWar.dto;

import com.example.CodeWar.app.DifficultyLevel;
import com.example.CodeWar.model.Problem;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@Data
public class ProblemResponse {
    private long id;
    private String problemStatement;
    private String problemTitle;
    private String inputSpecification;
    private String outputSpecification;
    private String constraints;
    private int timeLimit;
    private int memoryLimit;
    private DifficultyLevel difficultyLevel;
    private String sampleInput;
    private String sampleOutput;
    private String authorId;
    private String ioExplanation;
    private long maxCodeSize;

    public ProblemResponse(Problem problem) throws IOException {
        this.id = problem.getId();
        this.problemStatement = problem.getProblemStatement();
        this.problemTitle = problem.getProblemTitle();
        this.inputSpecification = problem.getInputSpecification();
        this.outputSpecification = problem.getOutputSpecification();
        this.timeLimit = problem.getTimeLimit();
        this.memoryLimit = problem.getMemoryLimit();
        this.difficultyLevel = problem.getDifficultyLevel();
        this.authorId = problem.getAuthorId().getCodeBattleId();
        this.ioExplanation = problem.getIoExplanation();
        this.sampleInput = problem.getSampleInput();
        this.sampleOutput = problem.getSampleOutput();
        this.constraints = problem.getConstraints();
    }
}