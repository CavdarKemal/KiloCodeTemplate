package com.verein.e2e;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SecurityE2ETest {

    @Test
    void testUnauthenticatedAccess() {
        given()
        .when()
            .get("/api/clubs")
        .then()
            .statusCode(401);
    }

    @Test
    void testAuthenticatedUserAccess() {
        String token = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"useraccess\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/clubs")
        .then()
            .statusCode(200);
    }

    @Test
    void testInvalidToken() {
        given()
            .header("Authorization", "Bearer invalidtoken")
        .when()
            .get("/api/clubs")
        .then()
            .statusCode(401);
    }

    @Test
    void testAdminCanCreateClub() {
        String adminToken = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"adminuser\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .body("role", equalTo("USER"))
            .extract()
            .path("token");

        given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Test Club\",\"description\":\"Description\"}")
        .when()
            .post("/api/clubs")
        .then()
            .statusCode(403);
    }

    @Test
    void testUserCannotCreateClub() {
        String userToken = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"reguser\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        given()
            .header("Authorization", "Bearer " + userToken)
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Unauthorized Club\",\"description\":\"Should fail\"}")
        .when()
            .post("/api/clubs")
        .then()
            .statusCode(403);
    }
}