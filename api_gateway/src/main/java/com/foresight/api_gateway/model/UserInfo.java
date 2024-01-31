package com.foresight.api_gateway.model;

public class UserInfo {
    private String role;
    private String email;

    public UserInfo(){}
    public UserInfo(String role, String email) {
        this.role = role;
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
