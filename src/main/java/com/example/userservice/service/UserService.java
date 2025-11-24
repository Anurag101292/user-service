package com.example.userservice.service;

import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;

public interface UserService {

    UserResponse getUserById(Long id);

    UserResponse createUser(UserRequest request);

    UserResponse getUserByUsername(String username);
}
