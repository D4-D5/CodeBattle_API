package com.example.CodeWar.services.implementation;

import com.example.CodeWar.app.ProblemStatus;
import com.example.CodeWar.app.UserRole;
import com.example.CodeWar.dto.Judge0CreateSubmission;
import com.example.CodeWar.dto.ProblemPayload;
import com.example.CodeWar.exception.FileException;
import com.example.CodeWar.model.Problem;
import com.example.CodeWar.model.Tag;
import com.example.CodeWar.model.User;
import com.example.CodeWar.repositories.ProblemRepository;
import com.example.CodeWar.repositories.TagRepository;
import com.example.CodeWar.repositories.UserRepository;
import com.example.CodeWar.services.AWSS3Service;
import com.example.CodeWar.services.ContestService;
import com.example.CodeWar.services.FileStorageService;
import com.example.CodeWar.services.ProblemService;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.example.CodeWar.app.Constants.*;

@Service
public class ProblemServiceImpl implements ProblemService {

    private static final Logger logger = LoggerFactory.getLogger(ProblemServiceImpl.class);

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ContestService contestService;

    @Autowired
    private AWSS3Service awss3Service;

    @Override
    public Map<String, Object> addProblem(ProblemPayload problemPayload) {
        logger.info("This is payload to add problem : {}",problemPayload);
        Map<String, Object> response = new HashMap<>();

        if (!isProblemReadyForDraft(problemPayload, response)) {
            response.put(STATUS, STATUS_FAILURE);
            return response;
        }
        try {
            Problem problem = new Problem(UUID.randomUUID().toString());
            addProblemPayloadToProblem(problem,problemPayload);
            if (!addTagsToProblem(problemPayload.getTags(), problem, response)) {
                response.put(REASON, ERROR_TAG);
                response.put(STATUS, STATUS_FAILURE);
                return response;
            }
            User user = userRepository.findByCodeBattleId(problemPayload.getAuthorId());
            user.setUserRole(UserRole.CONTRIBUTOR);
            problem.setAuthorId(user);
            logger.info("{}", problem);
            userRepository.save(user);
            problemRepository.save(problem);
            response.put(STATUS, STATUS_SUCCESS);
            response.put(MESSAGE, problem);
            return response;
        } catch (FileException e) {
            response.put(REASON, e);
            response.put(STATUS, STATUS_FAILURE);
            return response;
        }
    }

    @Override
    public Map<String, Object> updateProblem(ProblemPayload problemPayload) {
        Map<String, Object> response = new HashMap<>();

        if (!isProblemReadyForDraft(problemPayload, response)) {
            response.put(STATUS, STATUS_FAILURE);
            return response;
        }

        try {
        Optional<Problem> problem = problemRepository.findById(problemPayload.getId());
        if (problem.isPresent()) {
            if (!ProblemStatus.IN_DRAFT.equals(problem.get().getProblemStatus())) {
                response.put(STATUS, STATUS_FAILURE);
                response.put(REASON, PROBLEM_NOT_IN_DRAFT);
                return response;
            }
        } else {
            response.put(STATUS, STATUS_FAILURE);
            response.put(REASON, PROBLEM_NOT_FOUND);
            return response;
        }
//            Problem problem = new Problem();
            addProblemPayloadToProblem(problem.get(),problemPayload);
            problem.get().setId(problemPayload.getId());
            if (!findAuthorFromUser(problemPayload.getAuthorId(), problem.get(), response)) {
                response.put(STATUS, STATUS_FAILURE);
                return response;
            }
            if (!addTagsToProblem(problemPayload.getTags(), problem.get(), response)) {
                response.put(STATUS, STATUS_FAILURE);
                return response;
            }
            logger.info("{}", problem.get());
            problemRepository.save(problem.get());
            response.put(STATUS, STATUS_SUCCESS);
            response.put(MESSAGE, problem);
            return response;
        } catch (FileException e) {
            response.put(REASON, e.getMessage());
            response.put(STATUS, STATUS_FAILURE);
            return response;
        }
    }


    public void addProblemPayloadToProblem(Problem problem,ProblemPayload problemPayload) throws FileException {
        problem.setProblemTitle(problemPayload.getProblemTitle());
        problem.setProblemStatement(problemPayload.getProblemStatement());
        problem.setInputSpecification(problemPayload.getInputSpecification());
        problem.setOutputSpecification(problemPayload.getOutputSpecification());
        problem.setTimeLimit(problemPayload.getTimeLimit());
        problem.setMemoryLimit(problemPayload.getMemoryLimit());
        problem.setDifficultyLevel(problemPayload.getDifficultyLevel());
        problem.setIoExplanation(problemPayload.getIoExplanation());
        problem.setConstraints(problemPayload.getConstraints());
        String uuid = problem.getFileBasePath();
        problem.setSampleInput(problemPayload.getSampleInput());
        problem.setSampleOutput(problemPayload.getSampleOutput());
        String location = "/" + problemPayload.getProblemTitle() + "_" + uuid + "/";
        problem.setFileInputTestCases(location);
        storeFile(problemPayload.getFileInputTestCases(),location);
        storeFile(problemPayload.getFileOutputTestCases(),location);
        problem.setFileOutputTestCases(location);
        problem.setIdealSolution(problemPayload.getIdealSolution());
        problem.setIdealSolutionLanguageId(problemPayload.getIdealSolutionLanguageId());
    }

    public void storeFile(List<MultipartFile> files, String location) throws FileException {
        if(Objects.isNull(files) || files.isEmpty()){
            return;
        }
        System.out.println(location);
        //make dir if not already exits
//        File folder = new File(location);
//        if (!folder.exists()) {
//            if(!folder.mkdirs()){
//                throw new FileException(FILE_MKDIR);
//            }
//        }
        for(MultipartFile file : files){
            // Normalize file name
//            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
//            logger.info("This is file sent by lokesh :"+fileName);
//            logger.info("{}",file.getSize());
            awss3Service.uploadFile(file,location);
//            try {
//                // Check if the file's name contains invalid characters
//                if (fileName.contains("..")) {
//                    throw new FileException(FILE_INVALID_PATH);
//                }
//
//                System.out.println("File content tye "+file.getContentType());
//                // Copy file to the target location (Replacing existing file with the same name)
//                Path path = Paths.get(location + fileName);
//
//                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//            } catch (IOException ex) {
//                throw new FileException(FILE_ERROR);
//            }
        }
    }


    private boolean isProblemReadyForDraft(ProblemPayload problemPayload, Map<String, Object> response) {
        List<String> reasons = new ArrayList<>();
        logger.info("{}", problemPayload);
        //If the problem payload is null
        if (Objects.isNull(problemPayload)) {
            response.put(REASON, INVALID_ARGUMENT);
            return false;
        }
        //If Problem Title is Null
        if (Objects.isNull(problemPayload.getProblemTitle()) || problemPayload.getProblemTitle().isEmpty() || problemPayload.getProblemTitle().isBlank()) {
            reasons.add(PROBLEM_TITLE_IS_NULL);
        }
        //If Problem Statement is Null
        if (Objects.isNull(problemPayload.getProblemStatement()) || problemPayload.getProblemStatement().isEmpty() || problemPayload.getProblemStatement().isBlank()) {
            reasons.add(PROBLEM_STATEMENT_IS_NULL);
        }
        if (Objects.isNull(problemPayload.getAuthorId()) || problemPayload.getAuthorId().isEmpty() || problemPayload.getAuthorId().isBlank()) {
            reasons.add(USERNAME_IS_NULL);
        } else if (!userRepository.existsByCodeBattleId(problemPayload.getAuthorId())) {
            reasons.add(AUTHOR_ID_NOT_FOUND);
        }
        if (reasons.size()==0) {
            return true;
        }
        response.put(REASON, reasons);
        return false;
    }


    @Override
    public Map<String, Object> getProblems(String authorId) {
        Map<String, Object> response = new HashMap<>();
        logger.info("yaha tk bhi aa gaya");
        List<Problem> problems = problemRepository.getListOfProblemsByUserName(authorId);
        logger.info("{}", problems);
        response.put(MESSAGE, problems);
        response.put(STATUS, STATUS_SUCCESS);
        return response;
    }


    @Override
    public Map<String, Object> deleteProblem(long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Problem> problem = problemRepository.findById(id);
        if (problem.isEmpty()) {
            response.put(REASON, PROBLEM_NOT_FOUND);
        } else if (!ProblemStatus.IN_DRAFT.equals(problem.get().getProblemStatus())) {
            response.put(REASON, PROBLEM_NOT_IN_DRAFT);
        }
        if (response.containsKey(REASON)) {
            response.put(STATUS, STATUS_FAILURE);
            return response;
        }
        problemRepository.deleteById(id);
        response.put(STATUS, STATUS_SUCCESS);
        return response;
    }

    @Override
    public Map<String, Object> publishProblem(long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Problem> problem = problemRepository.findById(id);
        logger.info("Problem to be publish {}", problem);
        if (!problem.isPresent()) {
            response.put(REASON, PROBLEM_NOT_FOUND);
        } else if (ProblemStatus.IN_DRAFT.equals(problem.get().getProblemStatus())) {
            response.put(REASON, PROBLEM_IS_IN_DRAFT);
        } else if (ProblemStatus.PUBLISHED.equals(problem.get().getProblemStatus())) {
            response.put(REASON, PROBLEM_ALREADY_PUBLISHED);
        }
        if (response.containsKey(REASON)) {
            response.put(STATUS, STATUS_FAILURE);
            return response;
        }
        if (!isProblemReadyForPublish(problem.get(), response)) {
            response.put(STATUS, STATUS_FAILURE);
            return response;
        }
        problem.get().setProblemStatus(ProblemStatus.PUBLISHED);
        problemRepository.save(problem.get());
        response.put(STATUS, STATUS_SUCCESS);
        return response;
    }

    @Override
    public Map<String, Object> sendToReview(long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Problem> problem = problemRepository.findById(id);
        logger.info("Problem to be publish {}", problem);
        if (!problem.isPresent()) {
            response.put(REASON, PROBLEM_NOT_FOUND);
        } else if (ProblemStatus.IN_REVIEW.equals(problem.get().getProblemStatus())) {
            response.put(REASON, PROBLEM_ALREADY_IN_REVIEW);
        } else if (ProblemStatus.PUBLISHED.equals(problem.get().getProblemStatus())) {
            response.put(REASON, PROBLEM_ALREADY_PUBLISHED);
        }
        if (response.containsKey(REASON)) {
            response.put(STATUS, STATUS_FAILURE);
            return response;
        }

        if (!isProblemReadyForPublish(problem.get(), response)) {
            response.put(STATUS, STATUS_FAILURE);
            return response;
        }
        problem.get().setProblemStatus(ProblemStatus.IN_REVIEW);
        problemRepository.save(problem.get());
        response.put(STATUS, STATUS_SUCCESS);
        return response;
    }

    private boolean isProblemReadyForPublish(Problem problem, Map<String, Object> response) {
        List<String> reasons = new ArrayList<>();
        logger.info("{}", problem);

        //If the problem payload is null
        if (Objects.isNull(problem)) {
            response.put(REASON, INVALID_ARGUMENT);
            return false;
        }
        //If Problem Title is Null
        if (Objects.isNull(problem.getProblemTitle()) || problem.getProblemTitle().isEmpty() || problem.getProblemTitle().isBlank()) {
            reasons.add(PROBLEM_TITLE_IS_NULL);
        }

        //If Problem Statement is Null
        if (Objects.isNull(problem.getProblemStatement()) || problem.getProblemStatement().isEmpty() || problem.getProblemStatement().isBlank()) {
            reasons.add(PROBLEM_STATEMENT_IS_NULL);
        }

        //If Input Specification is Null
        if (Objects.isNull(problem.getInputSpecification()) || problem.getInputSpecification().isEmpty() || problem.getInputSpecification().isBlank()) {
            reasons.add(PROBLEM_INPUT_SPEC_IS_NULL);
        }

        //If Output Specification is Null
        if (Objects.isNull(problem.getOutputSpecification()) || problem.getOutputSpecification().isEmpty() || problem.getOutputSpecification().isBlank()) {
            reasons.add(PROBLEM_OUTPUT_SPEC_IS_NULL);
        }

        //If constraints is Null
        if (Objects.isNull(problem.getConstraints()) || problem.getConstraints().isEmpty() || problem.getConstraints().isBlank()) {
            reasons.add(PROBLEM_CONSTRAINTS_IS_NULL);
        }

        //If sample input file is null
        if (Objects.isNull(problem.getSampleInput())) {
            reasons.add(PROBLEM_SAMPLE_INPUT_IS_NULL);
        }

        //If sample output file is null
        if (Objects.isNull(problem.getSampleOutput())) {
            reasons.add(PROBLEM_SAMPLE_OUTPUT_IS_NULL);
        }

        //If input test case file is null
        if (Objects.isNull(problem.getFileInputTestCases())) {
            reasons.add(PROBLEM_TEST_INPUT_IS_NULL);
        }

        //If output test case file is null
        if (Objects.isNull(problem.getFileOutputTestCases())) {
            reasons.add(PROBLEM_TEST_OUTPUT_IS_NULL);
        }

        //If ideal solution case file is null
        if (Objects.isNull(problem.getIdealSolution())) {
            reasons.add(PROBLEM_IDEAL_SOLUTION_IS_NULL);
        }

        //If difficulty level is null
        if (Objects.isNull(problem.getDifficultyLevel())) {
            reasons.add(DIFFICULTY_LEVEL_IS_NULL);
        }

        //If time limit is < 1
        if (problem.getTimeLimit() < 1) {
            reasons.add(PROBLEM_TIME_LIMIT_IS_INVALID);
        }

        //If memory limit is < 1
        if (problem.getMemoryLimit() < 200) {
            reasons.add(PROBLEM_MEMORY_LIMIT_IS_INVALID);
        }

        //If author id is null
        if (Objects.isNull(problem.getAuthorId())) {
            reasons.add(PROBLEM_AUTHOR_IS_NULL);
        }


        //If no reason is added
        if (reasons.isEmpty()) {
            if(!verifySolution(problem,reasons)){
                response.put(REASON,WRONG_SOLUTION);
                return false;
            }
            return true;
        }

        //If reasons are added
        response.put(REASON, reasons);
        return false;
    }

    private boolean verifySolution(Problem problem, List<String> reasons) {
        Judge0CreateSubmission body = new Judge0CreateSubmission();
        try {
            body.setLanguage_id(problem.getIdealSolutionLanguageId());
            body.setSource_code(problem.getIdealSolution());
//            throw new IOException();
            body.setStdin(Files.readString(Path.of(problem.getFileInputTestCases())));
            body.setExpected_output(Files.readString(Path.of(problem.getFileOutputTestCases())));
//            logger.info("The body of submission {}",body.toString());
            try {
                HttpResponse<JsonNode> httpResponse = Unirest.post("https://judge0.p.rapidapi.com/submissions")
                        .header("x-rapidapi-host", "judge0.p.rapidapi.com")
                        .header("x-rapidapi-key", "9d099c4f7fmsh89afa8cdfb965f3p123e5fjsn10b6969a328e")
                        .header("content-type", "application/json")
                        .header("accept", "application/json")
                        .body(body)
                        .asJson();
                logger.info("{}",httpResponse.getBody());
                String token = httpResponse.getBody().getObject().getString("token");
                logger.info(token);
                TimeUnit.MILLISECONDS.sleep(SLEEP_TIME);
                return getSubmission(token,reasons);
            }
            catch (UnirestException | InterruptedException e){
                reasons.add(e.getMessage());
                return false;
            }
        }
        catch (IOException e){
            reasons.add(e.getMessage());
            return false;
        }
    }

    private boolean getSubmission(String token, List<String> reasons) {
        try {
            HttpResponse<JsonNode> httpResponse = Unirest.get("https://judge0.p.rapidapi.com/submissions/{token}?base64_encoded=true")
                    .header("x-rapidapi-host", "judge0.p.rapidapi.com")
                    .header("x-rapidapi-key", "9d099c4f7fmsh89afa8cdfb965f3p123e5fjsn10b6969a328e")
                    .routeParam("token", token)
                    .asJson();
            logger.info("()()===> {}", httpResponse.getBody());
            int statusId = httpResponse.getBody().getObject().getJSONObject("status").getInt("id");
            if (statusId <= 2) {
                TimeUnit.MILLISECONDS.sleep(SLEEP_TIME / 2);
                return getSubmission(token, reasons);
            }
            if (statusId == 3) {
                return true;
            } else {
                return false;
            }
        }
        catch (UnirestException | InterruptedException e){
            reasons.add(e.getMessage());
            return false;
        }
    }

    private boolean addTagsToProblem(Set<String> tags, Problem problem, Map<String, Object> response) {
        logger.info("mai tag add krne aa gaya {}", tags);
        for (String tag : tags) {
            String tagName = tag.toLowerCase();
            if (!tagRepository.existsByTagNameIgnoreCase(tagName)) {
                tagRepository.save(new Tag(tagName));
            }
            problem.getTags().add(tagRepository.findByTagNameIgnoreCase(tagName));
            logger.info("This is tag: " + tagName);
            logger.info("This are all the tags : {}", problem.getTags());
        }
        return true;
    }

    private boolean findAuthorFromUser(String authorId, Problem problem, Map<String, Object> response) {
        if (userRepository.existsByCodeBattleId(authorId)) {
            User user = userRepository.findByCodeBattleId(authorId);
            if (user.getUserRole().equals(UserRole.PARTICIPANT)) {
                response.put(REASON, AUTHOR_DO_NOT_HAVE_ACCESS);
                return false;
            } else {
                problem.setAuthorId(user);
                return true;
            }
        } else {
            response.put(REASON, AUTHOR_ID_NOT_FOUND);
            return false;
        }
    }
}
