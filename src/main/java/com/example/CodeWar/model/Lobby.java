package com.example.CodeWar.model;

import com.example.CodeWar.app.ContestStatus;
import com.example.CodeWar.app.ContestType;
import com.example.CodeWar.dto.Contestant;
import com.example.CodeWar.dto.CreateLobbyPayload;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document
@Data
public class Lobby {
    private String roomId;
    private int rating;
    private String owner;
    private ContestStatus contestStatus;
    private long startTime;
    private Set<Contestant> contestants = new HashSet<>();
    private Set<Long> questions = new HashSet<>();
    private Set<String> leaderBoard = new HashSet<>();
    private ContestType contestType;

    public Lobby(){

    }

    public Lobby(CreateLobbyPayload createLobbyPayload){
        this.roomId = createLobbyPayload.getRoomId();
        this.owner = createLobbyPayload.getOwner();
        this.contestType = createLobbyPayload.getContestType();
    }

    public Lobby(String roomId, int rating, String owner, ContestStatus contestStatus, long startTime, Set<Contestant> contestants, Set<Long> questions, Set<String> leaderBoard, ContestType contestType) {
        this.roomId = roomId;
        this.rating = rating;
        this.owner = owner;
        this.contestStatus = contestStatus;
        this.startTime = startTime;
        this.contestants = contestants;
        this.questions = questions;
        this.leaderBoard = leaderBoard;
        this.contestType = contestType;
    }


}
