package com.example.CodeWar.services;

import com.example.CodeWar.dto.ContestantPayload;
import com.example.CodeWar.dto.CreateLobbyPayload;
import com.example.CodeWar.dto.StartContestPayload;

import java.io.IOException;
import java.util.Map;

public interface LobbyService {
    Map<String, Object> createLobby(CreateLobbyPayload createLobbyPayload);

    Map<String, Object> addContestant(ContestantPayload contestantPayload);

    Map<String, Object> getContestants(String roomId);
}
