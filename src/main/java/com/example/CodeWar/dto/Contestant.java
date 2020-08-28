package com.example.CodeWar.dto;

import com.example.CodeWar.app.ContestStatus;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@Data
public class Contestant{

    private String codeBattleId;
    private int numSolved;
    private Set<Long> solvedQuestions = new HashSet<>();
    private long penalty;
    private int rank;
    private ContestStatus contestStatus = ContestStatus.IDLE;
    private int delta;
    private int rating;

    public Contestant(){

    }

    public Contestant(String codeBattleId) {
        this.codeBattleId = codeBattleId;;
    }

    public Contestant(Contestant contestant){
        this.codeBattleId = contestant.getCodeBattleId();
        this.numSolved = contestant.getNumSolved();
        this.solvedQuestions.addAll(contestant.solvedQuestions);
        this.penalty = contestant.getPenalty();
        this.rank = contestant.getRank();
        this.contestStatus = contestant.getContestStatus();
        this.rating = contestant.getRating();
        this.delta = contestant.getDelta();
    }

    public Contestant(String codeBattleId, int rank, long seed, int delta, int rating) {
        this.codeBattleId = codeBattleId;
        this.rank = rank;
        this.delta = delta;
        this.rating = rating;
    }

    public Contestant(String codeBattleId, double rank, double seed, int delta, int rating) {
        this.codeBattleId = codeBattleId;
        this.rank = (int) rank;
        this.delta = delta;
        this.rating = rating;
    }

    public Contestant(String codeBattleId, int rating, ContestStatus contestStatus) {
        this.codeBattleId = codeBattleId;
        this.rating = rating;
        this.contestStatus = contestStatus;
    }

    public Contestant(int rating) {
        this.rating = rating;
    }
}
