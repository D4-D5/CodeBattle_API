package com.example.CodeWar.model;

import com.example.CodeWar.app.DifficultyLevel;
import com.example.CodeWar.dto.ProblemPayload;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Problem {

    @Id
    @GeneratedValue
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
    private String fileInputTestCase;
    private String fileOutputTestCase;
    private String fileIdealSolution;
    @OneToOne
    private User authorId;
    private String ioExplanation;
    private int maxCodeSize;
    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    public Problem(ProblemPayload problemPayload) {
        this.problemStatement = problemPayload.getProblemStatement();
        this.problemTitle = problemPayload.getProblemTitle();
        this.inputSpecification = problemPayload.getInputSpecification();
        this.outputSpecification = problemPayload.getOutputSpecification();
        this.timeLimit = problemPayload.getTimeLimit();
        this.memoryLimit = problemPayload.getMemoryLimit();
        this.difficultyLevel = problemPayload.getDifficultyLevel();
        this.ioExplanation = problemPayload.getIoExplanation();
        this.maxCodeSize = problemPayload.getMaxCodeSize();
    }

    public Problem() {

    }
}