//package com.example.CodeWar.controllers.requests;
//
//import com.example.CodeWar.model.User;
//
//import javax.validation.constraints.NotNull;
//import java.util.Objects;
//
//public class UserRegisterRequest {
//    @NotNull
//    private String username;
//    @NotNull
//    private String email;
//    @NotNull
//    private String password;
//    @NotNull
//    private String countryCode;
//    @NotNull
//    private String phoneNumber;
//
//    @Override
//    public String toString() {
//        return "UserRegisterRequest{" +
//                "username='" + username + '\'' +
//                ", email='" + email + '\'' +
//                ", password='" + password + '\'' +
//                ", countryCode='" + countryCode + '\'' +
//                ", phoneNumber='" + phoneNumber + '\'' +
//                '}';
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        UserRegisterRequest that = (UserRegisterRequest) o;
//
//        if (!Objects.equals(username, that.username))
//            return false;
//        if (!Objects.equals(email, that.email)) return false;
//        if (!Objects.equals(password, that.password))
//            return false;
//        if (!Objects.equals(countryCode, that.countryCode))
//            return false;
//        return Objects.equals(phoneNumber, that.phoneNumber);
//    }
//
//    @Override
//    public int hashCode() {
//        int result = username != null ? username.hashCode() : 0;
//        result = 31 * result + (email != null ? email.hashCode() : 0);
//        result = 31 * result + (password != null ? password.hashCode() : 0);
//        result = 31 * result + (countryCode != null ? countryCode.hashCode() : 0);
//        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
//        return result;
//    }
//
//    public UserRegisterRequest(String username, String email, String password, String countryCode, String phoneNumber) {
//
//        this.username = username;
//        this.email = email;
//        this.password = password;
//        this.countryCode = countryCode;
//        this.phoneNumber = phoneNumber;
//    }
//
//    public UserRegisterRequest() {
//
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getCountryCode() {
//        return countryCode;
//    }
//
//    public void setCountryCode(String countryCode) {
//        this.countryCode = countryCode;
//    }
//
//    public String getPhoneNumber() {
//        return phoneNumber;
//    }
//
//    public void setPhoneNumber(String phoneNumber) {
//        this.phoneNumber = phoneNumber;
//    }
//
//    public User toModel(String encodedPassword) {
//        return new User(getUsername(), getEmail(), encodedPassword, getCountryCode(),
//                getPhoneNumber());
//    }
//}
