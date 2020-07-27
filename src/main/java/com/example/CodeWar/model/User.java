package com.example.CodeWar.model;

import com.example.CodeWar.app.UserRole;
import com.example.CodeWar.app.UserVerificationStatus;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue
    private long id;
    private String userName;
    @Email
    private String email;
    private String password;
    private String countryCode;
    private String phoneNumber;
    private String name;
    private String codeforcesId;
    private UserRole userRole = UserRole.PARTICIPANT;
    private UserVerificationStatus userVerificationStatus = UserVerificationStatus.NOT_VERIFIED;
    private int rating;
}
