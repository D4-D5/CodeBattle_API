package com.example.CodeWar.controllers;

import com.example.CodeWar.dto.LoginPayload;
import com.example.CodeWar.services.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(value = LoginController.class)
@WithMockUser
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginService loginService;


    @Test
    void login() {
    }

    @Test
    void register() {
    }

    @Test
    void getAllUsers() {
    }

    @Test
    void confirmUserAccount() {
    }
}