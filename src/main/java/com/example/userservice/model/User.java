package com.example.userservice.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    private Long id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public User() {
    }

    public User(Long id, String username, String lastName, Integer age,OffsetDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.lastName = lastName;
        this.age = age;
        this.createdAt = createdAt;

    }
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
