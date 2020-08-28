package com.example.CodeWar.controllers;

import com.example.CodeWar.app.Constants;
import com.example.CodeWar.dto.JwtAuthenticationResponse;
import com.example.CodeWar.dto.LoginPayload;
import com.example.CodeWar.dto.UserPayload;
import com.example.CodeWar.services.LoginService;
import com.example.CodeWar.services.implementation.LoginServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static com.example.CodeWar.app.Constants.*;

@RestController
@RequestMapping(path = "/api")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;


    @PostMapping(path = "/login")
    public ResponseEntity<Map<String, Object>> login(HttpServletResponse httpServletResponse,@RequestBody LoginPayload loginPayload) {
        logger.info("Login Request with request object:{}", loginPayload);
        Map<String, Object> response = loginService.login(loginPayload);
        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
//        JwtAuthenticationResponse jwtAuthenticationResponse = (JwtAuthenticationResponse) response.get(MESSAGE);
//        Cookie cookie = new Cookie("token",jwtAuthenticationResponse.getAccessToken());
//        cookie.setHttpOnly(true);
//        cookie.setDomain("bc9f49b6ccbc.ngrok.io");
//        cookie.setPath("/");
//        cookie.setSecure(true);

//        httpServletResponse.addCookie(cookie);
//        httpServletResponse.setHeader("access-control-expose-headers", "Set-Cookie");
//        httpServletResponse.setHeader("Access-Control-Allow-Credentials","true");
//        httpServletResponse.setHeader("Access-Control-Allow-Origin","https://bc9f49b6ccbc.ngrok.io");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = "/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody UserPayload userPayload) {
        logger.info("Register Request with request object:{}", userPayload);
        Map<String, Object> response = loginService.register(userPayload);

        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/allUser")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        logger.info("yaha hu ki nahi");
        Map<String, Object> response = loginService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> confirmUserAccount(@RequestParam("token") String confirmationToken) {
        logger.info(confirmationToken);
        Map<String, Object> response = loginService.confirmUserAccount(confirmationToken);
        if (STATUS_FAILURE.equals(response.get(Constants.STATUS).toString())) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}