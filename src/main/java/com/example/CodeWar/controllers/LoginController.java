package com.example.CodeWar.controllers;

import com.example.CodeWar.app.Constants;
import com.example.CodeWar.app.UserVerificationStatus;
import com.example.CodeWar.dto.LoginPayload;
import com.example.CodeWar.model.ConfirmationToken;
import com.example.CodeWar.model.User;
import com.example.CodeWar.repositories.ConfirmationTokenRepository;
import com.example.CodeWar.repositories.UserRepository;
import com.example.CodeWar.services.EmailSenderService;
import com.example.CodeWar.services.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.ManyToMany;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.CodeWar.app.Constants.*;


@RestController
@RequestMapping(path = "/api")
@CrossOrigin(origins = "*")
public class LoginController {
    String fileBasePath = "/media/mohit/1AB6DA39B6DA155B/uploads/";

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private LoginService loginService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;


    @PostMapping(path = "/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginPayload loginPayload) {
        logger.info("Login Request with request object:{}", loginPayload);
        Map<String, Object> response = loginService.login(loginPayload);
        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = "/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User userPayload) {
        logger.info("Register Request with request object:{}", userPayload);
        Map<String, Object> response = loginService.register(userPayload);

        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/allUser")
    public ResponseEntity<Map<String,Object>> getAllUsers() {
        Map<String, Object> response = loginService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String,Object>> confirmUserAccount(@RequestParam("token")String confirmationToken)
    {
        logger.info(confirmationToken);
        Map<String,Object> response = loginService.confirmUserAccount(confirmationToken);
        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}