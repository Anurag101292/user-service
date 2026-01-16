package com.example.userservice;

import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.dto.UpdateUserRequest;
import com.example.userservice.model.User;
import com.example.userservice.service.UserService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestAssuredUserApiTest {

    @LocalServerPort
    private int port;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
    }

    @Test
    public void testCreateUser() {
        UserRequest request = new UserRequest();
        request.setId(1L);
        request.setUsername("jdoe");
        request.setLastName("Doe");
        request.setAge(30);

        UserResponse response = new UserResponse(1L, "jdoe", "Doe", 30, OffsetDateTime.now());

        when(userService.createUser(any(UserRequest.class))).thenReturn(response);

        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/api/users")
        .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body("id", equalTo(1))
                .body("username", equalTo("jdoe"))
                .body("lastName", equalTo("Doe"))
                .body("age", equalTo(30));
    }

    @Test
    public void testGetUserByUsername() {
        String username = "jdoe";
        UserResponse response = new UserResponse(1L, username, "Doe", 30, OffsetDateTime.now());

        when(userService.getUserByUsername(username)).thenReturn(response);

        given()
                .accept(ContentType.JSON)
        .when()
                .get("/api/users/username/{username}", username)
        .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("id", equalTo(1))
                .body("username", equalTo(username))
                .body("lastName", equalTo("Doe"))
                .body("age", equalTo(30));
    }
}
