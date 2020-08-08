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
    private int timeLimit;
    private int memoryLimit;
    private int acceptedSubmissions = 0;
    private int totalSubmissions = 0;
    private DifficultyLevel difficultyLevel;
    private String fileSampleInput;
    private String fileSampleOutput;
    private String authorId;
    private String ioExplanation;
    private int maxCodeSize;

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
        this.maxCodeSize = problem.getMaxCodeSize();
        this.fileSampleInput = Files.readString(Path.of(problem.getFileInputTestCase()));
        this.fileSampleOutput = Files.readString(Path.of(problem.getFileOutputTestCase()));
    }
}