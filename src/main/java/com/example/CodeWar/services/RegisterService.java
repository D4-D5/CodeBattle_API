//package com.example.CodeWar.services;
//
//import com.example.CodeWar.controllers.RegisterController;
//import com.example.CodeWar.controllers.requests.UserRegisterRequest;
//import com.example.CodeWar.model.User;
//import com.example.CodeWar.model.UserRoles;
//import com.example.CodeWar.repositories.UserRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//public class RegisterService {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);
//
//    private UserRepository userRepository;
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    public RegisterService(UserRepository userRepository,
//                           PasswordEncoder passwordEncoder,
//                           AuthyApiClient authyClient) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.authyClient = authyClient;
//    }
//
//    public void register(UserRegisterRequest request) {
//        User userModel = userRepository.findFirstByUsername(request.getUsername());
//
//        if(userModel != null) {
//            LOGGER.warn(String.format("User already exist: {}", request.getUsername()));
//            throw new com.twilio.accountsecurity.exceptions.UserExistsException();
//        }
//
//        User authyUser = authyClient.getUsers().createUser(request.getEmail(),
//                request.getPhoneNumber(),
//                request.getCountryCode());
//
//        if(authyUser.getError() != null) {
//            throw new com.twilio.accountsecurity.exceptions.UserRegistrationException(authyUser.getError().getMessage());
//        }
//        User newUserModel = request.toModel(passwordEncoder.encode(request.getPassword()));
//        newUserModel.setRole(UserRoles.ROLE_USER);
//        newUserModel.setAuthyId(authyUser.getId());
//        userRepository.save(newUserModel);
//    }
//}
