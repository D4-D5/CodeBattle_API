package com.example.CodeWar.dto;

import lombok.Data;

@Data
public class CodeforcesProfile {
    private String handle;
    private String email;
    private String vkId;
    private String openId;
    private String firstName;
    private String lastName;
    private String country;
    private String city;
    private String organization;
    private int contribution;
    private String rank;
    private int rating;
    private int maxRating;
    private String maxRank;
    private int lastOnlineTimeSeconds;
    private int registrationTimeSeconds;
    private int friendOfCount;
    private String avatar;
    private String titlePhoto;
}
