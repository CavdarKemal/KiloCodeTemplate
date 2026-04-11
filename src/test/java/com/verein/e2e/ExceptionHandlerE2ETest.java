package com.verein.e2e;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ExceptionHandlerE2ETest {

    @Test
    void testResourceNotFoundException() {
        String token = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"erruser1\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/clubs/99999")
        .then()
            .statusCode(404)
            .body("status", equalTo(404))
            .body("message", containsString("nicht gefunden"));
    }

    @Test
    void testValidationError() {
        String token = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"erruser2\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body("{\"name\":\"\"}")
        .when()
            .post("/api/clubs")
        .then()
            .statusCode(400)
            .body("status", equalTo(400))
            .body("errors", notNullValue());
    }

    @Test
    void testDuplicateResourceException() {
        String token = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"erruser3\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Duplicate Club\",\"city\":\"Berlin\"}")
        .when()
            .post("/api/clubs")
        .then()
            .statusCode(201);

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Duplicate Club\",\"city\":\"Hamburg\"}")
        .when()
            .post("/api/clubs")
        .then()
            .statusCode(400)
            .body("status", equalTo(400))
            .body("message", containsString("existiert bereits"));
    }

    @Test
    void testAccessDeniedException() {
        String userToken = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"regularuser2\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        given()
            .header("Authorization", "Bearer " + userToken)
        .when()
            .get("/api/clubs/deleted")
        .then()
            .statusCode(403)
            .body("status", equalTo(403))
            .body("message", containsString("Zugriff verweigert"));
    }

    @Test
    void testGenericException() {
        String token = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"erruser4\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Test Club\",\"city\":\"Berlin\"}")
        .when()
            .post("/api/clubs")
        .then()
            .statusCode(201);

        Long clubId = given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/clubs")
        .then()
            .statusCode(200)
            .extract()
            .path("id[0]");

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/members/club/" + clubId)
        .then()
            .statusCode(500);
    }

    @Test
    void testErrorResponseFormat() {
        String token = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"erruser5\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/clubs/99999")
        .then()
            .statusCode(404)
            .body("status", notNullValue())
            .body("message", notNullValue())
            .body("timestamp", notNullValue());
    }
}