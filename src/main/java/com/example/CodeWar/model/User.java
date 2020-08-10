package com.example.CodeWar.model;

import com.example.CodeWar.app.UserRole;
import com.example.CodeWar.app.UserVerificationStatus;
import com.example.CodeWar.dto.UserPayload;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Email;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue
    private long id;
    private String codeBattleId;
    @Email
    private String email;
    private String password;
    private String countryCode = "+91";
    private String phoneNumber;
    private String name;
    private String codeforcesId;
    private UserRole userRole = UserRole.PARTICIPANT;
    private UserVerificationStatus userVerificationStatus = UserVerificationStatus.NOT_VERIFIED;
    private int rating;
    private int maxRating;
    private String country;
    private String city;
    private String organization;
    private String avatar;
    private String titlePhoto;
    @ManyToMany
    private Set<Problem> solvedProblems = new HashSet<>();

    public User(UserPayload userPayload) {
        this.codeBattleId = userPayload.getCodeBattleId();
        this.email = userPayload.getEmail();
        this.countryCode = userPayload.getCountryCode();
        this.phoneNumber = userPayload.getPhoneNumber();
        this.name = userPayload.getName();
        this.codeforcesId = userPayload.getCodeforcesId();
        this.password = userPayload.getPassword();
        this.userRole = Objects.requireNonNullElse(userPayload.getUserRole(), UserRole.PARTICIPANT);
    }

    public User() {

    }
}
