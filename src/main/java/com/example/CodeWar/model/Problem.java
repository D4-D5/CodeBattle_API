package com.example.CodeWar.model;

import com.example.CodeWar.app.DifficultyLevel;
import com.example.CodeWar.app.ProblemStatus;
import com.example.CodeWar.dto.ProblemPayload;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
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
    private ProblemStatus problemStatus = ProblemStatus.IN_DRAFT;
    @OneToOne
    private User authorId;
    private String ioExplanation;
    private int maxCodeSize;
    @ManyToMany
    private Set<Tag> tags = new HashSet<>();
    @ManyToMany(mappedBy = "solvedProblems")
    private Set<User> solvedByUsers;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProblemStatement() {
        return problemStatement;
    }

    public void setProblemStatement(String problemStatement) {
        this.problemStatement = problemStatement;
    }

    public String getProblemTitle() {
        return problemTitle;
    }

    public void setProblemTitle(String problemTitle) {
        this.problemTitle = problemTitle;
    }

    public String getInputSpecification() {
        return inputSpecification;
    }

    public void setInputSpecification(String inputSpecification) {
        this.inputSpecification = inputSpecification;
    }

    public String getOutputSpecification() {
        return outputSpecification;
    }

    public void setOutputSpecification(String outputSpecification) {
        this.outputSpecification = outputSpecification;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(int memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public int getAcceptedSubmissions() {
        return acceptedSubmissions;
    }

    public void setAcceptedSubmissions(int acceptedSubmissions) {
        this.acceptedSubmissions = acceptedSubmissions;
    }

    public int getTotalSubmissions() {
        return totalSubmissions;
    }

    public void setTotalSubmissions(int totalSubmissions) {
        this.totalSubmissions = totalSubmissions;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getFileSampleInput() {
        return fileSampleInput;
    }

    public void setFileSampleInput(String fileSampleInput) {
        this.fileSampleInput = fileSampleInput;
    }

    public String getFileSampleOutput() {
        return fileSampleOutput;
    }

    public void setFileSampleOutput(String fileSampleOutput) {
        this.fileSampleOutput = fileSampleOutput;
    }

    public String getFileInputTestCase() {
        return fileInputTestCase;
    }

    public void setFileInputTestCase(String fileInputTestCase) {
        this.fileInputTestCase = fileInputTestCase;
    }

    public String getFileOutputTestCase() {
        return fileOutputTestCase;
    }

    public void setFileOutputTestCase(String fileOutputTestCase) {
        this.fileOutputTestCase = fileOutputTestCase;
    }

    public String getFileIdealSolution() {
        return fileIdealSolution;
    }

    public void setFileIdealSolution(String fileIdealSolution) {
        this.fileIdealSolution = fileIdealSolution;
    }

    public ProblemStatus getProblemStatus() {
        return problemStatus;
    }

    public void setProblemStatus(ProblemStatus problemStatus) {
        this.problemStatus = problemStatus;
    }

    public User getAuthorId() {
        return authorId;
    }

    public void setAuthorId(User authorId) {
        this.authorId = authorId;
    }

    public String getIoExplanation() {
        return ioExplanation;
    }

    public void setIoExplanation(String ioExplanation) {
        this.ioExplanation = ioExplanation;
    }

    public int getMaxCodeSize() {
        return maxCodeSize;
    }

    public void setMaxCodeSize(int maxCodeSize) {
        this.maxCodeSize = maxCodeSize;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

}