package com.verein.e2e;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("Auth E2E Tests")
public class AuthE2ETest extends BaseE2ETest {

    @Test
    @DisplayName("Register user - 201 Created")
    void testRegisterUser() {
        String username = uniqueUsername();
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "username": "%s",
                        "password": "password123"
                    }
                    """.formatted(username))
        .when()
                .post(getBaseUrl() + "/api/auth/register")
        .then()
                .statusCode(201)
                .body("token", notNullValue())
                .body("username", equalTo(username))
                .body("role", equalTo("USER"));
    }

    @Test
    @DisplayName("Register user with email - 201 Created")
    void testRegisterUserWithEmail() {
        String username = uniqueUsername();
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "username": "%s",
                        "password": "password123",
                        "email": "%s@test.com"
                    }
                    """.formatted(username, username))
        .when()
                .post(getBaseUrl() + "/api/auth/register")
        .then()
                .statusCode(201)
                .body("token", notNullValue());
    }

    @Test
    @DisplayName("Duplicate registration - 400 Bad Request")
    void testDuplicateRegistration() {
        String username = uniqueUsername();
        
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "username": "%s",
                        "password": "password123"
                    }
                    """.formatted(username))
        .when()
                .post(getBaseUrl() + "/api/auth/register")
        .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "username": "%s",
                        "password": "password123"
                    }
                    """.formatted(username))
        .when()
                .post(getBaseUrl() + "/api/auth/register")
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Login success - 200 OK")
    void testLoginSuccess() {
        String username = uniqueUsername();
        
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "username": "%s",
                        "password": "password123"
                    }
                    """.formatted(username))
        .when()
                .post(getBaseUrl() + "/api/auth/register")
        .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "username": "%s",
                        "password": "password123"
                    }
                    """.formatted(username))
        .when()
                .post(getBaseUrl() + "/api/auth/login")
        .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("username", equalTo(username));
    }

    @Test
    @DisplayName("Login invalid credentials - 401 Unauthorized")
    void testLoginInvalidCredentials() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "username": "nonexistentuser",
                        "password": "wrongpassword"
                    }
                    """)
        .when()
                .post(getBaseUrl() + "/api/auth/login")
        .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("Login missing username - 400 Bad Request")
    void testLoginMissingUsername() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "password": "password123"
                    }
                    """)
        .when()
                .post(getBaseUrl() + "/api/auth/login")
        .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Login missing password - 400 Bad Request")
    void testLoginMissingPassword() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "username": "someuser"
                    }
                    """)
        .when()
                .post(getBaseUrl() + "/api/auth/login")
        .then()
                .statusCode(400);
    }
}