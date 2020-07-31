package com.example.CodeWar.services;

import com.example.CodeWar.app.UserVerificationStatus;
import com.example.CodeWar.dto.CodeforcesProfile;
import com.example.CodeWar.dto.LoginPayload;
import com.example.CodeWar.dto.UserPayload;
import com.example.CodeWar.model.ConfirmationToken;
import com.example.CodeWar.model.User;
import com.example.CodeWar.services.implementation.LoginServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.CodeWar.app.Constants.*;

public interface LoginService {
    Map<String, Object> login(LoginPayload loginPayload);

    Map<String, Object> register(UserPayload userPayload);

    Map<String, Object> getAllUsers();

    Map<String, Object> confirmUserAccount(String confirmationToken);
}
