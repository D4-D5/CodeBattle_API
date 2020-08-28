package com.example.CodeWar.app;

import com.example.CodeWar.dto.Contestant;

import java.util.Comparator;
import java.util.function.ToLongFunction;

public class ContestantComparator implements Comparator<Contestant> {

    @Override
    public int compare(Contestant o1, Contestant o2) {
        if(o1.getNumSolved() == o2.getNumSolved()){
            if(o1.getPenalty() == o2.getPenalty()){
                return Integer.compare(o1.getRating(),o2.getRating());
            }
            return Long.compare(-o1.getPenalty(), -o2.getPenalty());
        }
        return Integer.compare(o1.getNumSolved(), o2.getNumSolved());
    }
}
