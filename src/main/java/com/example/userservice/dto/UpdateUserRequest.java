package com.example.userservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
public class UpdateUserRequest {
    
    @Size(min = 1, message = "username must not be empty")
    private String username;

    @Size(min = 1, message = "lastName must not be empty")
    private String lastName;

    @Min(value = 0, message = "age must be non-negative")
    private Integer age;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
