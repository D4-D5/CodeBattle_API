package com.example.CodeWar.controllers;


import com.example.CodeWar.app.Constants;
import com.example.CodeWar.dto.ProblemPayload;
import com.example.CodeWar.services.ProblemService;
import com.example.CodeWar.services.implementation.ProblemServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.example.CodeWar.app.Constants.STATUS_FAILURE;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin(origins = "*")
public class ProblemController {
    String fileBasePath = "/media/mohit/1AB6DA39B6DA155B/uploads/";

    @Autowired
    private ProblemService problemService;

    private static final Logger logger = LoggerFactory.getLogger(ProblemController.class);

    @PostMapping("/createProblem")
    public ResponseEntity<Map<String, Object>> addProblem(@ModelAttribute ProblemPayload problemPayload) {
        logger.info("Problem Payload is -> {}", problemPayload);
        Map<String, Object> response = problemService.addProblem(problemPayload);
        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/getProblems")
    public ResponseEntity<Map<String, Object>> getProblems(@RequestParam String authorId) {
        logger.info(authorId);
        Map<String, Object> response = problemService.getProblems(authorId);
        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


//    @GetMapping("/download/{fileName:.+}")
//    public ResponseEntity downloadFileFromLocal(@PathVariable String fileName) {
//
//        Path path = Paths.get(fileBasePath + fileName);
//        Resource resource = null;
//        try {
//            resource = new UrlResource(path.toUri());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        String contentType = "application/octet-stream";
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(contentType))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                .body(resource);
//    }

}
