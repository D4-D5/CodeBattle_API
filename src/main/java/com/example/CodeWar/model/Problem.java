package com.example.CodeWar.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private String difficultyLevel;
    private String fileSampleInput;
    private String fileSampleOutput;
    private String fileInputTestCase;
    private String fileOutputTestCase;
    private String fileIdealSolution;
    @OneToOne
    private User authorId;
    private String ioExplanation;
    private int maxCodeSize;
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Tags> tags = new HashSet<>();
}