package com.synapse.spaced_repetition_api.dto;

public class LoginDTO{
    private String username;
    private String password;

    public LoginDTO() {} // Constructor rỗng

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
