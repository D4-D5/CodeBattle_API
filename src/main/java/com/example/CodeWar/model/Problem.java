package com.example.CodeWar.model;

import com.example.CodeWar.app.DifficultyLevel;
import com.example.CodeWar.app.ProblemStatus;
import com.example.CodeWar.dto.ProblemPayload;
import com.example.CodeWar.exception.FileException;
import com.example.CodeWar.services.FileStorageService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static com.example.CodeWar.app.Constants.*;
import static com.example.CodeWar.app.Constants.FILE_ERROR;

@Entity
public class Problem {

    @Id
    @GeneratedValue
    private long id;
    private String problemTitle;
    @Column(length = 655370)
    private String problemStatement;
    @Column(length = 655370)
    private String inputSpecification;
    @Column(length = 655370)
    private String outputSpecification;
    @Column(length = 655370)
    private String constraints;
    @Column(length = 655370)
    private String sampleInput;
    @Column(length = 655370)
    private String sampleOutput;
    @Column(length = 655370)
    private String ioExplanation;
    private String fileInputTestCases;
    private String fileOutputTestCases;
    @Column(length = 655370)
    private String idealSolution;
    private int idealSolutionLanguageId;
    private DifficultyLevel difficultyLevel;
    @ManyToMany
    private Set<Tag> tags = new HashSet<>();
    private int timeLimit;
    private int memoryLimit;
    private ProblemStatus problemStatus = ProblemStatus.IN_DRAFT;
    @OneToOne
    private User authorId;
    @ManyToMany(mappedBy = "solvedProblems")
    private Set<User> solvedByUsers;
    private String fileBasePath;

    public Problem(ProblemPayload problemPayload) throws FileException {
        this.problemStatement = problemPayload.getProblemStatement();
        this.problemTitle = problemPayload.getProblemTitle();
        this.inputSpecification = problemPayload.getInputSpecification();
        this.outputSpecification = problemPayload.getOutputSpecification();
        this.timeLimit = problemPayload.getTimeLimit();
        this.memoryLimit = problemPayload.getMemoryLimit();
        this.difficultyLevel = problemPayload.getDifficultyLevel();
        this.ioExplanation = problemPayload.getIoExplanation();
        this.constraints = problemPayload.getConstraints();
        this.sampleInput = problemPayload.getSampleInput();
        this.sampleOutput = problemPayload.getSampleOutput();
        UUID uuid = UUID.randomUUID();
        String location = FILE_BASE_PATH + problemPayload.getProblemTitle() + "_" + uuid + "/";
        storeFile(problemPayload.getFileInputTestCases(), location);
        storeFile(problemPayload.getFileOutputTestCases(), location);
        this.fileInputTestCases = location;
        this.fileOutputTestCases = location;
        this.idealSolution = problemPayload.getIdealSolution();
        this.idealSolutionLanguageId = problemPayload.getIdealSolutionLanguageId();
    }

    public Problem(String fileBasePath){
        this.fileBasePath = fileBasePath;
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

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getFileInputTestCases() {
        return fileInputTestCases;
    }

    public void setFileInputTestCases(String fileInputTestCases) {
        this.fileInputTestCases = fileInputTestCases;
    }

    public String getFileOutputTestCases() {
        return fileOutputTestCases;
    }

    public void setFileOutputTestCases(String fileOutputTestCases) {
        this.fileOutputTestCases = fileOutputTestCases;
    }

    public String getSampleInput() {
        return sampleInput;
    }

    public void setSampleInput(String sampleInput) {
        this.sampleInput = sampleInput;
    }

    public String getSampleOutput() {
        return sampleOutput;
    }

    public void setSampleOutput(String sampleOutput) {
        this.sampleOutput = sampleOutput;
    }

    public String getIdealSolution() {
        return idealSolution;
    }

    public void setIdealSolution(String idealSolution) {
        this.idealSolution = idealSolution;
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

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public int getIdealSolutionLanguageId() {
        return idealSolutionLanguageId;
    }

    public void setIdealSolutionLanguageId(int idealSolutionLanguageId) {
        this.idealSolutionLanguageId = idealSolutionLanguageId;
    }

    public String getFileBasePath() {
        return fileBasePath;
    }

    public void setFileBasePath(String fileBasePath) {
        this.fileBasePath = fileBasePath;
    }

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }

    public void storeFile(List<MultipartFile> files, String location) throws FileException {
        if(Objects.isNull(files) || files.isEmpty()){
            return;
        }
        System.out.println(location);
        //make dir if not already exits
        File folder = new File(location);
        if (!folder.exists()) {
            if(!folder.mkdirs()){
                throw new FileException(FILE_MKDIR);
            }
        }
        for(MultipartFile file : files){
            // Normalize file name
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

            try {
                // Check if the file's name contains invalid characters
                if (fileName.contains("..")) {
                    throw new FileException(FILE_INVALID_PATH);
                }

                System.out.println("File content tye "+file.getContentType());
                // Copy file to the target location (Replacing existing file with the same name)
                Path path = Paths.get(location + fileName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                throw new FileException(FILE_ERROR);
            }
        }
    }

}