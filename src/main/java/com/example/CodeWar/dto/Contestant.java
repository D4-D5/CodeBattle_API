package com.example.CodeWar.dto;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
public class Contestant{

    private String codeBattleId;
    private int numSolved;
    private Set<Long> solvedQuestions = new HashSet<>();
    private long penalty;

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
    }
}
