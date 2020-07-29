package com.example.CodeWar.services;

import com.example.CodeWar.app.UserVerificationStatus;
import com.example.CodeWar.dto.LoginPayload;
import com.example.CodeWar.model.ConfirmationToken;
import com.example.CodeWar.model.User;
import com.example.CodeWar.repositories.ConfirmationTokenRepository;
import com.example.CodeWar.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.CodeWar.app.Constants.*;

@Service
public class LoginService {

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> login(LoginPayload loginPayload) {
        Map<String, Object> response = new HashMap<>();
        //If the payload is NULL
        if (Objects.isNull(loginPayload)) {
            response.put(STATUS, STATUS_FAILURE);
            return response;
        }
        // Fetch the user as per userName
        User user = userRepository.findFirstByUserName(loginPayload.getUserName());

        //If we do not found any such username
        if (Objects.isNull(user)) {
            response.put(STATUS, STATUS_FAILURE);
            response.put(REASON, USER_NOT_REGISTERED);
            return response;
        }

        //If password is matched then return success
        if (user.getPassword().equals(loginPayload.getPassword())) {
            response.put(STATUS, STATUS_SUCCESS);
            return response;
        }

        //Password didn't matched so return failure
        response.put(STATUS, STATUS_FAILURE);
        response.put(REASON, WRONG_PASSWORD);
        return response;
    }


    public Map<String, Object> register(User userPayload) {
        Map<String, Object> response = new HashMap<>();

        //If the user payload is null
        if (Objects.isNull(userPayload)) {
            response.put(STATUS, STATUS_FAILURE);
            response.put(REASON, INVALID_ARGUMENT);
            return response;
        }
        //Validate all fields for userPayload
        if (!isValidUserRegister(userPayload, response)) {
            response.put(STATUS, STATUS_FAILURE);
            return response;
        }

        //Insert the user payload to database
        userRepository.save(userPayload);
        sendEmailVerification(userPayload, userPayload.getEmail());
        response.put(MESSAGE, userPayload);
        response.put(STATUS, STATUS_SUCCESS);
        return response;
    }

    private boolean isValidUserRegister(User userPayload, Map<String, Object> response) {
        List<String> reasons = new ArrayList<>();

        //If userName is null or empty or blank
        //else If the userName already exists
        if (Objects.isNull(userPayload.getUserName()) || userPayload.getUserName().isEmpty() || userPayload.getUserName().isBlank()) {
            reasons.add(USERNAME_IS_NULL);
        } else if (userRepository.existsByUserName(userPayload.getUserName())) {
            reasons.add(USER_ID_TAKEN);
        }

        //If phoneNumber is null or empty or blank
        //else If the phoneNumber already exists
        if (Objects.isNull(userPayload.getPhoneNumber()) || userPayload.getPhoneNumber().isEmpty() || userPayload.getPhoneNumber().isBlank()) {
            reasons.add(PHONE_NUMBER_IS_NULL);
        } else if (userRepository.existsByPhoneNumber(userPayload.getPhoneNumber())) {
            reasons.add(PHONE_NUMBER_TAKEN);
        }

        //If email is null or empty or blank
        //else If email is invalid
        //else If the email already exist
        if (Objects.isNull(userPayload.getEmail()) || userPayload.getEmail().isEmpty() || userPayload.getEmail().isBlank()) {
            reasons.add(EMAIL_IS_NULL);
        } else if (!regexMatcher(userPayload.getEmail(),EMAIL_REGEX)) {
            reasons.add(EMAIL_INVALID);
        } else if (userRepository.existsByEmail(userPayload.getEmail())) {
            reasons.add(EMAIL_TAKEN);
        }

        //If password is null or empty or blank
        //else If the password do not match regex
        if (Objects.isNull(userPayload.getPassword()) || userPayload.getPassword().isEmpty() || userPayload.getPassword().isBlank()) {
            reasons.add(PHONE_NUMBER_IS_NULL);
        } else if (!regexMatcher(userPayload.getPassword(),PASSWORD_REGEX)) {
            reasons.add(PASSWORD_TOO_WEAK);
        }

        if (reasons.isEmpty()) {
            return true;
        }
        response.put(REASON, reasons);
        return false;

    }

    private void sendEmailVerification(User user, String email) {

        ConfirmationToken confirmationToken = new ConfirmationToken(user);

        confirmationTokenRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("CODE BATTLE - Complete Registration!");
        mailMessage.setFrom("mohitagr00001@gmail.com");
        mailMessage.setText("To confirm your account, please click here : "
                +URI_CONFIRM_ACCOUNT+"?token=" + confirmationToken.getConfirmationToken());

        emailSenderService.sendEmail(mailMessage);
    }

    private boolean regexMatcher(String value,String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        System.out.println(value + " : " + matcher.matches());
        return matcher.matches();
    }

    public Map<String, Object> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        List<User> users = userRepository.findAll();
        response.put(STATUS, STATUS_SUCCESS);
        response.put(MESSAGE, users);
        return response;
    }

    public Map<String, Object> confirmUserAccount(String confirmationToken) {
        Map<String, Object> response = new HashMap<>();
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if (token != null) {
            User user = userRepository.findByEmail(token.getUser().getEmail());
            user.setUserVerificationStatus(UserVerificationStatus.VERIFIED);
            userRepository.save(user);
            response.put(STATUS, STATUS_SUCCESS);
            response.put(MESSAGE, ACCOUNT_VERIFIED);
        } else {
            response.put(STATUS, STATUS_FAILURE);
            response.put(MESSAGE, ACCOUNT_NOT_VERIFIED);
        }
        return response;
    }
}
