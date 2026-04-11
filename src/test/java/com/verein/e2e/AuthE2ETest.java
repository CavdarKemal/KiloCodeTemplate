package com.verein.e2e;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthE2ETest {

    @Test
    void testRegisterUser() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"e2euser\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .body("token", notNullValue())
            .body("username", equalTo("e2euser"))
            .body("role", equalTo("USER"));
    }

    @Test
    void testDuplicateRegistration() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"duplicate\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201);

        given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"duplicate\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(400);
    }

    @Test
    void testLoginSuccess() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"loginuser\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201);

        given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"loginuser\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(200)
            .body("token", notNullValue())
            .body("username", equalTo("loginuser"));
    }

    @Test
    void testLoginInvalidCredentials() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"nonexistent\",\"password\":\"wrongpassword\"}")
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(401);
    }
}