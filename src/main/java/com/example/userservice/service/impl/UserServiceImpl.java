// src/main/java/com/example/userservice/service/impl/UserServiceImpl.java
package com.example.userservice.service.impl;

import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.dto.UpdateUserRequest;
import com.example.userservice.dto.WorldTimeResponse;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WebClient worldTimeWebClient;

    public UserServiceImpl(UserRepository userRepository, WebClient worldTimeWebClient) {
        this.userRepository = userRepository;
        this.worldTimeWebClient = worldTimeWebClient;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + id));

        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
     User user = userRepository.findByUsername(username)
             .orElseThrow(() ->
                     new UserNotFoundException("User not found with username: " + username));
 
     return mapToResponse(user);
    }

    @Override
    public UserResponse createUser(UserRequest request) {
        // fetch current time from worldtimeapi
        OffsetDateTime createdAt = fetchCurrentKolkataTimeOrFallback();

        User user = new User(
                request.getId(),
                request.getUsername(),
                request.getLastName(),
                request.getAge(),
                createdAt
        );

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    private OffsetDateTime fetchCurrentKolkataTimeOrFallback() {
        try {
            // worldTimeWebClient is already configured with baseUrl = https://worldtimeapi.org/api/timezone/Asia/Kolkata
            WorldTimeResponse resp = worldTimeWebClient.get()
                    .retrieve()
                    .bodyToMono(WorldTimeResponse.class)
                    .block(); // synchronous here - ok inside service

            if (resp != null && resp.getDatetime() != null) {
                try {
                    return OffsetDateTime.parse(resp.getDatetime());
                } catch (DateTimeParseException e) {
                    // fallback to system offset time if parsing fails
                }
            }
        } catch (WebClientResponseException wex) {
            // log if you have logger (omitted here). fallback below.
        } catch (Exception ex) {
            // any other failure -> fallback
        }

        // fallback to server time with Asia/Kolkata offset
        // Using system default time zone converted to offset of Asia/Kolkata would require ZoneId.of("Asia/Kolkata").
        return OffsetDateTime.now(java.time.ZoneId.of("Asia/Kolkata"));
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getLastName(),
                user.getAge(),
                user.getCreatedAt()
        );
    }

    @Override
    public void deleteUser(Long id) {
        boolean exists = userRepository.existsById(id);
        if (!exists) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // Only update non-null fields (partial update)
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getAge() != null) {
            user.setAge(request.getAge());
        }

        OffsetDateTime newTime = fetchCurrentKolkataTimeOrFallback();
        user.setCreatedAt(newTime);

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return page.map(this::mapToResponse);
    }
    // you can keep getUserByUsername etc. unchanged
}
