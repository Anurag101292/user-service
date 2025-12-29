package com.example.userservice.service;

import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.dto.UpdateUserRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse getUserById(Long id);

    UserResponse createUser(UserRequest request);

    UserResponse getUserByUsername(String username);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);

    Page<UserResponse> getUsers(Pageable pageable);
}
