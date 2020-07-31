package com.example.CodeWar.services.implementation;

import com.example.CodeWar.app.Constants;
import com.example.CodeWar.app.ProblemStatus;
import com.example.CodeWar.app.UserRole;
import com.example.CodeWar.dto.ProblemPayload;
import com.example.CodeWar.model.Problem;
import com.example.CodeWar.model.Tag;
import com.example.CodeWar.model.User;
import com.example.CodeWar.repositories.ProblemRepository;
import com.example.CodeWar.repositories.TagRepository;
import com.example.CodeWar.repositories.UserRepository;
import com.example.CodeWar.services.FileStorageService;
import com.example.CodeWar.services.ProblemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

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

    @Override
    public Map<String, Object> addProblem(ProblemPayload problemPayload) {
        Map<String, Object> response = new HashMap<>();
//
        //Validate all fields for userPayload
        if (!isValidProblem(problemPayload, response)) {
            response.put(STATUS, STATUS_FAILURE);
            return response;
        }
//
        Problem problem = new Problem(problemPayload);
//
        if (!findAuthorFromUser(problemPayload.getAuthorId(), problem, response)) {
            response.put(STATUS, STATUS_FAILURE);
            return response;
        }

        if (!addTagsToProblem(problemPayload.getTags(), problem, response)) {
            response.put(STATUS, STATUS_FAILURE);
            return response;
        }



        if (saveFilesAndItsLocation(problemPayload.getFileSampleInput(), problem, response)) {
            problem.setFileSampleInput(response.get(MESSAGE).toString());
        } else {
            return response;
        }

        if (saveFilesAndItsLocation(problemPayload.getFileSampleOutput(), problem, response)) {
            problem.setFileSampleOutput(response.get(MESSAGE).toString());
        } else {
            return response;
        }

        if (saveFilesAndItsLocation(problemPayload.getFileInputTestCase(), problem, response)) {
            problem.setFileInputTestCase(response.get(MESSAGE).toString());
        } else {
            return response;
        }

        if (saveFilesAndItsLocation(problemPayload.getFileOutputTestCase(), problem, response)) {
            problem.setFileOutputTestCase(response.get(MESSAGE).toString());
        } else {
            return response;
        }

        if (saveFilesAndItsLocation(problemPayload.getFileIdealSolution(), problem, response)) {
            problem.setFileIdealSolution(response.get(MESSAGE).toString());
        } else {
            return response;
        }
//
        logger.info("{}", problem);
        problemRepository.save(problem);
        response.put(STATUS, STATUS_SUCCESS);
        response.put(MESSAGE, problem);
        return response;
    }

    @Override
    public Map<String, Object> getProblems(String authorId) {
        Map<String, Object> response = new HashMap<>();
        logger.info("yaha tk bhi aa gaya");
        List<Problem> problems = problemRepository.getListOfUsers(authorId);
        logger.info("{}", problems);
        response.put(MESSAGE, problems);
        response.put(STATUS, STATUS_SUCCESS);
        return response;
    }

    @Override
    public Map<String, Object> updateProblem(ProblemPayload problemPayload) {
        Map<String, Object> response = new HashMap<>();

        if(problemPayload.getId()==0){
            response.put(STATUS,STATUS_FAILURE);
            response.put(REASON,PROBLEM_ID_IS_NULL);
            return response;
        }

        Optional<Problem> problem1 = problemRepository.findById(problemPayload.getId());
        if(problem1.isPresent()){
            if(!ProblemStatus.IN_DRAFT.equals(problem1.get().getProblemStatus())){
                response.put(STATUS,STATUS_FAILURE);
                response.put(REASON,PROBLEM_UNAUTHORIZED);
                return response;
            }
        }
        else {
            response.put(STATUS,STATUS_FAILURE);
            response.put(REASON,PROBLEM_NOT_FOUND);
            return response;
        }

        //Check if any problem exits with this problem ID
        if(!problemRepository.existsById(problemPayload.getId())){
            response.put(STATUS,STATUS_FAILURE);
            response.put(REASON,PROBLEM_NOT_FOUND);
            return response;
        }

        //Validate all fields for userPayload
        if (!isValidProblem(problemPayload, response)) {
            response.put(STATUS, STATUS_FAILURE);
            return response;
        }
//
        Problem problem = new Problem(problemPayload);

        problem.setId(problemPayload.getId());
//
        if (!findAuthorFromUser(problemPayload.getAuthorId(), problem, response)) {
            response.put(STATUS, STATUS_FAILURE);
            return response;
        }

        if (!addTagsToProblem(problemPayload.getTags(), problem, response)) {
            response.put(STATUS, STATUS_FAILURE);
            return response;
        }

        if (saveFilesAndItsLocation(problemPayload.getFileSampleInput(), problem, response)) {
            problem.setFileSampleInput(response.get(MESSAGE).toString());
        } else {
            return response;
        }

        if (saveFilesAndItsLocation(problemPayload.getFileSampleOutput(), problem, response)) {
            problem.setFileSampleOutput(response.get(MESSAGE).toString());
        } else {
            return response;
        }

        if (saveFilesAndItsLocation(problemPayload.getFileInputTestCase(), problem, response)) {
            problem.setFileInputTestCase(response.get(MESSAGE).toString());
        } else {
            return response;
        }

        if (saveFilesAndItsLocation(problemPayload.getFileOutputTestCase(), problem, response)) {
            problem.setFileOutputTestCase(response.get(MESSAGE).toString());
        } else {
            return response;
        }

        if(!Objects.isNull(problemPayload.getFileIdealSolution())) {
            if (saveFilesAndItsLocation(problemPayload.getFileIdealSolution(), problem, response)) {
                problem.setFileIdealSolution(response.get(MESSAGE).toString());
            } else {
                return response;
            }
        }
//
        logger.info("{}", problem);
        problemRepository.save(problem);
        response.put(STATUS, STATUS_SUCCESS);
        response.put(MESSAGE, problem);
        return response;
    }

    @Override
    public Map<String, Object> deleteProblem(long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Problem> problem = problemRepository.findById(id);
        if(problem.isEmpty()){
            response.put(REASON,PROBLEM_NOT_FOUND);
        }
        else if(!ProblemStatus.IN_DRAFT.equals(problem.get().getProblemStatus())){
            response.put(REASON,PROBLEM_UNAUTHORIZED);
        }
        if(response.containsKey(REASON)){
            response.put(STATUS,STATUS_FAILURE);
            return response;
        }
        problemRepository.deleteById(id);
        response.put(STATUS,STATUS_SUCCESS);
        return response;
    }

    @Override
    public Map<String, Object> publishProblem(long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Problem> problem = problemRepository.findById(id);
        logger.info("Problem to be publish {}",problem);
        if(!problem.isPresent()){
            response.put(REASON,PROBLEM_NOT_FOUND);
        }
        else if(ProblemStatus.IN_DRAFT.equals(problem.get().getProblemStatus())){
            response.put(REASON,PROBLEM_IS_IN_DRAFT);
        }
        else if(ProblemStatus.PUBLISHED.equals(problem.get().getProblemStatus())){
            response.put(REASON,PROBLEM_ALREADY_PUBLISHED);
        }
        if(response.containsKey(REASON)){
            response.put(STATUS,STATUS_FAILURE);
            return response;
        }
        problem.get().setProblemStatus(ProblemStatus.PUBLISHED);
        problemRepository.save(problem.get());
        response.put(STATUS,STATUS_SUCCESS);
        return response;
    }


    private boolean saveFilesAndItsLocation(MultipartFile file, Problem problem, Map<String, Object> response) {
        String location = FILE_BASE_PATH + problem.getId() + "/";
        response.putAll(fileStorageService.storeFile(file, location));
        return !STATUS_FAILURE.equals(response.get(Constants.STATUS).toString());
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
            if (!user.getUserRole().equals(UserRole.EDITOR) && !user.getUserRole().equals(UserRole.CONTRIBUTOR)) {
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

    private boolean isValidProblem(ProblemPayload problemPayload, Map<String, Object> response) {

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

        //If Input Specification is Null
        if (Objects.isNull(problemPayload.getInputSpecification()) || problemPayload.getInputSpecification().isEmpty() || problemPayload.getInputSpecification().isBlank()) {
            reasons.add(PROBLEM_INPUT_SPEC_IS_NULL);
        }

        //If Output Specification is Null
        if (Objects.isNull(problemPayload.getOutputSpecification()) || problemPayload.getOutputSpecification().isEmpty() || problemPayload.getOutputSpecification().isBlank()) {
            reasons.add(PROBLEM_OUTPUT_SPEC_IS_NULL);
        }

        //If time limit is < 1
        if (problemPayload.getTimeLimit() < 1) {
            reasons.add(PROBLEM_TIME_LIMIT_IS_INVALID);
        }

        //If memory limit is < 1
        if (problemPayload.getMemoryLimit() < 1) {
            reasons.add(PROBLEM_MEMORY_LIMIT_IS_INVALID);
        }

        //If author id is null
        if (Objects.isNull(problemPayload.getAuthorId()) || problemPayload.getAuthorId().isEmpty() || problemPayload.getAuthorId().isBlank()) {
            reasons.add(PROBLEM_AUTHOR_IS_NULL);
        }

        //If input test case file is null
        if (Objects.isNull(problemPayload.getFileInputTestCase())) {
            reasons.add(PROBLEM_TEST_INPUT_IS_NULL);
        }

        //If output test case file is null
        if (Objects.isNull(problemPayload.getFileOutputTestCase())) {
            reasons.add(PROBLEM_TEST_OUTPUT_IS_NULL);
        }

        //If sample input file is null
        if (Objects.isNull(problemPayload.getFileSampleInput())) {
            reasons.add(PROBLEM_SAMPLE_INPUT_IS_NULL);
        }

        //If sample output file is null
        if (Objects.isNull(problemPayload.getFileSampleOutput())) {
            reasons.add(PROBLEM_SAMPLE_OUTPUT_IS_NULL);
        }

        //If difficulty level is null
        if (Objects.isNull(problemPayload.getDifficultyLevel())) {
            reasons.add(DIFFICULTY_LEVEL_IS_NULL);
        }

        //If no reason is added
        if (reasons.isEmpty()) {
            return true;
        }

        //If reasons are added
        response.put(REASON, reasons);
        return false;

    }
}
