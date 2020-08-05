package com.example.CodeWar.dto;

import java.util.HashSet;
import java.util.Set;

public class ContestantResponse {
    private Set<Contestant> contestantSet = new HashSet<>();
    public ContestantResponse(Set<Contestant> contestantList) {
        this.contestantSet.addAll(contestantList);
    }
}
