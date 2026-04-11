package com.verein.e2e;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuditE2ETest {

    @Test
    void testAuditLogOnClubCreate() {
        String adminToken = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"audituser1\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Test Club\",\"city\":\"Berlin\"}")
        .when()
            .post("/api/clubs")
        .then()
            .statusCode(201);

        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .get("/api/audit/type/Club")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("[0].entityName", equalTo("Club"))
            .body("[0].action", equalTo("CREATE"));
    }

    @Test
    void testAuditLogOnClubDelete() {
        String adminToken = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"audituser2\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        Long clubId = given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body("{\"name\":\"To Delete\",\"city\":\"Berlin\"}")
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
            .get("/api/audit/entity/Club/" + clubId)
        .then()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("[0].action", equalTo("DELETE"));
    }

    @Test
    void testAuditLogOnClubUpdate() {
        String adminToken = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"audituser3\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        Long clubId = given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Original Name\",\"city\":\"Berlin\"}")
        .when()
            .post("/api/clubs")
        .then()
            .statusCode(201)
            .extract()
            .path("id");

        given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Updated Name\",\"city\":\"Hamburg\"}")
        .when()
            .put("/api/clubs/" + clubId)
        .then()
            .statusCode(200);

        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .get("/api/audit/entity/Club/" + clubId)
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0));
    }

    @Test
    void testGetAllAuditLogs() {
        String adminToken = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"audituser4\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Club1\",\"city\":\"Berlin\"}")
        .when()
            .post("/api/clubs")
        .then()
            .statusCode(201);

        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .get("/api/audit/all?page=0&size=10")
        .then()
            .statusCode(200)
            .body("content.size()", greaterThan(0));
    }

    @Test
    void testAuditEndpointAccessDeniedForUser() {
        String userToken = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"regularuser\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        given()
            .header("Authorization", "Bearer " + userToken)
        .when()
            .get("/api/audit/all")
        .then()
            .statusCode(403);
    }

    @Test
    void testAuditEndpointAccessibleForAdmin() {
        String adminToken = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"admin4audit\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        given()
            .header("Authorization", "Bearer " + adminToken)
        .when()
            .get("/api/audit/all")
        .then()
            .statusCode(200);
    }
}