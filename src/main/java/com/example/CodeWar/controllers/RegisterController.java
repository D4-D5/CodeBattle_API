//package com.example.CodeWar.controllers;
//
//import com.example.CodeWar.controllers.requests.UserRegisterRequest;
//import com.example.CodeWar.services.RegisterService;
//import com.twilio.accountsecurity.exceptions.UserExistsException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.validation.Valid;
//
//@RestController
//public class RegisterController {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);
//
//    @Autowired
//    private RegisterService registerService;
//
//    @RequestMapping(value = "/api/register",
//            method = {RequestMethod.GET, RequestMethod.POST},
//            consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity register(@Valid @RequestBody UserRegisterRequest newUserRequest,
//                                   HttpServletRequest request) {
//        try {
//            registerService.register(newUserRequest);
//            request.login(newUserRequest.getUsername(), newUserRequest.getPassword());
//            return ResponseEntity.ok().build();
//        } catch (UserExistsException e) {
//            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
//                    .body("User already exists");
//        } catch (ServletException e) {
//            LOGGER.error(e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//        }
//    }
//}
