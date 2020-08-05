package com.example.CodeWar.dto;

import com.example.CodeWar.app.ContestType;
import lombok.Data;

@Data
public class CreateLobbyPayload {
    private String roomId;
    private String owner;
    private ContestType contestType;
}
