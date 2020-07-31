package com.example.CodeWar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Tag {
    @Id
    @GeneratedValue
    private long id;
    private String tagName;
    @ManyToMany(mappedBy = "tags")
    private Set<Problem> problems = new HashSet<>();


    public Tag(String tagName) {
        this.tagName = tagName;
    }

    public Tag() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

}
