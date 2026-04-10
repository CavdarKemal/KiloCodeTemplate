package com.verein.e2e;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class ClubE2ETest extends BaseE2ETest {

    private static Long clubId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void createClub() {
        String requestBody = "{" +
                "\"name\": \"FC Testverein\"," +
                "\"description\": \"Ein Testverein\"," +
                "\"foundedDate\": \"2020-01-01\"," +
                "\"city\": \"Berlin\"" +
                "}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/clubs")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("FC Testverein"))
                .body("city", equalTo("Berlin"))
                .extract().response();

        clubId = response.path("id");
    }

    @Test
    void getClubById() {
        createClub();

        given()
                .when()
                .get("/api/clubs/" + clubId)
                .then()
                .statusCode(200)
                .body("id", equalTo(clubId.intValue()))
                .body("name", equalTo("FC Testverein"));
    }

    @Test
    void getAllClubs() {
        given()
                .when()
                .get("/api/clubs")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(0)));
    }

    @Test
    void updateClub() {
        createClub();

        String updateBody = "{" +
                "\"name\": \"FC Testverein Updated\"," +
                "\"description\": \"Updated Description\"," +
                "\"city\": \"Munich\"" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(updateBody)
                .when()
                .put("/api/clubs/" + clubId)
                .then()
                .statusCode(200)
                .body("name", equalTo("FC Testverein Updated"))
                .body("city", equalTo("Munich"));
    }

    @Test
    void deleteClub() {
        createClub();

        given()
                .when()
                .delete("/api/clubs/" + clubId)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/api/clubs/" + clubId)
                .then()
                .statusCode(400);
    }

    @Test
    void createClub_ValidationError() {
        String requestBody = "{" +
                "\"name\": \"\"," +
                "\"description\": \"Test\"" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/clubs")
                .then()
                .statusCode(400);
    }
}