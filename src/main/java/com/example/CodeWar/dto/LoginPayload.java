package com.example.CodeWar.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginPayload {
    @NotNull
    private String codeBattleId;
    @NotNull
    private String password;
}
