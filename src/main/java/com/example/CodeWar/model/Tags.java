package com.example.CodeWar.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class Tags {
    @Id
    @GeneratedValue
    private long id;
    private String tagName;
    @ManyToMany(mappedBy = "tags")
    private Set<Problem> problems = new HashSet<>();
}
