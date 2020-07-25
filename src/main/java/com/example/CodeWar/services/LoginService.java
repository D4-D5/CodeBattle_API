package com.example.CodeWar.services;

import com.example.CodeWar.dto.LoginPayload;
import com.example.CodeWar.model.User;
import com.example.CodeWar.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.CodeWar.app.Constants.*;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> login(LoginPayload loginPayload) {
        Map<String, Object> response = new HashMap<>();
        // If the payload is NULL
        if(Objects.isNull(loginPayload)){
            response.put(STATUS, STATUS_FAILURE);
            return response;
        }
        //If we do not found any such username
        User user = userRepository.findFirstByUserName(loginPayload.getUserName());
        if(Objects.isNull(user)){
            response.put(STATUS, STATUS_FAILURE);
            response.put(REASON, USER_NOT_REGISTERED);
            return response;
        }
        //If password is matched then return success
        if(user.getPassword().equals(loginPayload.getPassword())){
            response.put(STATUS, STATUS_SUCCESS);
            return response;
        }
        //Password didn't matched so return failure
        response.put(STATUS, STATUS_FAILURE);
        response.put(REASON,WRONG_PASSWORD);
        return response;
    }


    public Map<String, Object> register(User userPayload) {
        Map<String, Object> response = new HashMap<>();

        //If the user payload is null
        if(Objects.isNull(userPayload)){
            response.put(STATUS,STATUS_FAILURE);
            response.put(REASON,INVALID_ARGUMENT);
            return response;
        }

        //If the userName already exists
        if(userRepository.existsByUserName(userPayload.getUserName())){
            response.put(STATUS,STATUS_FAILURE);
            response.put(REASON,USERID_TAKEN);
            return response;
        }

        //If the Phone number already exists
        if(userRepository.existsByPhoneNumber(userPayload.getPhoneNumber())){
            response.put(STATUS,STATUS_FAILURE);
            response.put(REASON,PHONENUMBER_TAKEN);
            return response;
        }

        //If the Email number already exists
        if(userRepository.existsByEmail(userPayload.getEmail())){
            response.put(STATUS,STATUS_FAILURE);
            response.put(REASON,EMAIL_TAKEN);
            return response;
        }

        //Insert the user payload to database
        userRepository.save(userPayload);
        response.put(MESSAGE,userPayload);
        response.put(STATUS,STATUS_SUCCESS);
        return response;
    }

    public Map<String, Object> getAllUsers() {
        Map<String,Object> response = new HashMap<>();
        List<User> users = userRepository.findAll();
        response.put(STATUS,STATUS_SUCCESS);
        response.put(MESSAGE,users);
        return response;
    }
}
