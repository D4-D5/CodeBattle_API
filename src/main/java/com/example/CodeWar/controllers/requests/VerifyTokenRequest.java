package com.example.CodeWar.controllers.requests;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class VerifyTokenRequest {

    @NotNull
    private String token;

    public VerifyTokenRequest(String token) {
        this.token = token;
    }

    public VerifyTokenRequest() {

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VerifyTokenRequest that = (VerifyTokenRequest) o;

        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return token != null ? token.hashCode() : 0;
    }
}
