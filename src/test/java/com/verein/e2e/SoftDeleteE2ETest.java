package com.verein.e2e;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SoftDeleteE2ETest {

    @Test
    void testSoftDeleteClub() {
        String adminToken = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"adminuser\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        Long clubId = given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Test Club\",\"city\":\"Berlin\"}")
        .when()
            .post("/api/clubs")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .get("/api/clubs")
        .then()
            .statusCode(200)
            .body("size()", equalTo(1));

        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .delete("/api/clubs/" + clubId)
        .then()
            .statusCode(204);

        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .get("/api/clubs")
        .then()
            .statusCode(200)
            .body("size()", equalTo(0));

        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .get("/api/clubs/deleted")
        .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].name", equalTo("Test Club"));
    }

    @Test
    void testRestoreClub() {
        String adminToken = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"restoreuser\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        Long clubId = given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Restored Club\",\"city\":\"Berlin\"}")
        .when()
            .post("/api/clubs")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .delete("/api/clubs/" + clubId)
        .then()
            .statusCode(204);

        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .put("/api/clubs/" + clubId + "/restore")
        .then()
            .statusCode(200)
            .body("name", equalTo("Restored Club"));

        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .get("/api/clubs")
        .then()
            .statusCode(200)
            .body("size()", equalTo(1));
    }

    @Test
    void testSoftDeleteMember() {
        String adminToken = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"memberuser2\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        Long clubId = given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Test Club\",\"city\":\"Berlin\"}")
        .when()
            .post("/api/clubs")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        Long memberId = given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body("{\"firstName\":\"Max\",\"lastName\":\"Mustermann\",\"email\":\"max2@test.com\",\"membershipType\":\"REGULAR\",\"status\":\"ACTIVE\",\"clubId\":" + clubId + "}")
        .when()
            .post("/api/members")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .delete("/api/members/" + memberId)
        .then()
            .statusCode(204);

        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .get("/api/members")
        .then()
            .statusCode(200)
            .body("size()", equalTo(0));

        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .get("/api/members/deleted")
        .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].firstName", equalTo("Max"));
    }

    @Test
    void testCannotAccessDeletedClub() {
        String adminToken = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"accessuser\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        Long clubId = given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Deleted Club\",\"city\":\"Berlin\"}")
        .when()
            .post("/api/clubs")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .delete("/api/clubs/" + clubId)
        .then()
            .statusCode(204);

        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .get("/api/clubs/" + clubId)
        .then()
            .statusCode(500);
    }
}