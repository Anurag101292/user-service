package com.example.userservice.dto;

import java.time.OffsetDateTime;

public class UserResponse {

    private Long id;
    private String username;
    private String lastName;
    private Integer age;
    private OffsetDateTime createdAt;

    public UserResponse() {
    }

    public UserResponse(Long id, String username, String lastName, Integer age,OffsetDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.lastName = lastName;
        this.age = age;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
