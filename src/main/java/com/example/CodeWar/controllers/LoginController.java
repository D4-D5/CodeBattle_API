package com.example.CodeWar.controllers;

import com.example.CodeWar.app.Constants;
import com.example.CodeWar.dto.LoginPayload;
import com.example.CodeWar.model.User;
import com.example.CodeWar.repositories.UserRepository;
import com.example.CodeWar.services.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.example.CodeWar.app.Constants.*;


@RestController
@RequestMapping(path = "/api")
@CrossOrigin(origins = "*")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private LoginService loginService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping(path = "/login")
    public ResponseEntity login(@RequestBody LoginPayload loginPayload) {

        logger.info("Login Request with request object:{}",loginPayload);

        Map<String, Object> response = loginService.login(loginPayload);

        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = "/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User userPayload) {

        logger.info("Register Request with request object:{}",userPayload);

        Map<String,Object> response = loginService.register(userPayload);
        logger.info("{}",response);
        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/allUser")
    public ResponseEntity getAllUsers(){
        Map<String,Object> response = loginService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}