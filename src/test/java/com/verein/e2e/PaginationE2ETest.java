package com.verein.e2e;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PaginationE2ETest {

    @Test
    void testClubPagination() {
        String token = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"testuser\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        for (int i = 1; i <= 5; i++) {
            given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("{\"name\":\"Club" + i + "\",\"city\":\"City" + i + "\"}")
            .when()
                .post("/api/clubs")
            .then()
                .statusCode(201);
        }

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/clubs/paginated?page=0&size=3")
        .then()
            .statusCode(200)
            .body("content.size()", equalTo(3))
            .body("page", equalTo(0))
            .body("totalElements", equalTo(5))
            .body("totalPages", equalTo(2));
    }

    @Test
    void testMemberPagination() {
        String token = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"memberuser\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        String clubResponse = given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body("{\"name\":\"Test Club\",\"city\":\"Berlin\"}")
        .when()
            .post("/api/clubs")
        .then()
            .statusCode(201)
            .extract()
            .asString();

        Long clubId = given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/clubs")
        .then()
            .statusCode(200)
            .extract()
            .path("id[0]");

        for (int i = 1; i <= 5; i++) {
            given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("{\"firstName\":\"Member" + i + "\",\"lastName\":\"Test" + i + "\",\"email\":\"member" + i + "@test.com\",\"membershipType\":\"REGULAR\",\"status\":\"ACTIVE\",\"clubId\":" + clubId + "}")
            .when()
                .post("/api/members")
            .then()
                .statusCode(201);
        }

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/members/paginated?page=0&size=3")
        .then()
            .statusCode(200)
            .body("content.size()", equalTo(3))
            .body("page", equalTo(0))
            .body("totalElements", equalTo(5));
    }

    @Test
    void testSearchPagination() {
        String token = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"searchuser\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body("{\"name\":\"FC Berlin\",\"city\":\"Berlin\"}")
        .when()
            .post("/api/clubs")
        .then()
            .statusCode(201);

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body("{\"name\":\"FC Hamburg\",\"city\":\"Hamburg\"}")
        .when()
            .post("/api/clubs")
        .then()
            .statusCode(201);

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/clubs/search?search=Berlin&page=0&size=10")
        .then()
            .statusCode(200)
            .body("content.size()", equalTo(1))
            .body("content[0].name", equalTo("FC Berlin"));
    }
}