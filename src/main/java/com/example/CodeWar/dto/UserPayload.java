package com.example.CodeWar.dto;

import com.example.CodeWar.app.UserRole;
import lombok.Data;

@Data
public class UserPayload {

    private String codeBattleId;
    private String email;
    private String password;
    private String countryCode = "+91";
    private String phoneNumber;
    private String name;
    private String codeforcesId;
    private UserRole userRole = UserRole.PARTICIPANT;
}