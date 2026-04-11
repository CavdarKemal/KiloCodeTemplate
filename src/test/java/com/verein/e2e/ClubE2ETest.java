package com.verein.e2e;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("Club E2E Tests")
public class ClubE2ETest extends BaseE2ETest {

    private static Long clubId;

    @BeforeEach
    void setUp() {
        String username = uniqueUsername();
        register(username, "password123", username + "@test.com");
        login(username, "password123");
    }

    @Test
    @DisplayName("Create club - 201 Created")
    void createClub() {
        String requestBody = """
            {
                "name": "FC Testverein",
                "description": "Ein Testverein",
                "foundedDate": "2020-01-01",
                "city": "Berlin"
            }
            """;

        Response response = postWithAuth("/api/clubs", requestBody)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("FC Testverein"))
                .body("city", equalTo("Berlin"))
                .extract().response();

        clubId = response.path("id");
    }

    @Test
    @DisplayName("Get club by ID - 200 OK")
    void getClubById() {
        String createBody = """
            {
                "name": "Einzelner Verein",
                "city": "Hamburg"
            }
            """;

        Long id = postWithAuth("/api/clubs", createBody)
                .jsonPath().getLong("id");

        getWithAuth("/api/clubs/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("name", equalTo("Einzelner Verein"));
    }

    @Test
    @DisplayName("Get all clubs - 200 OK")
    void getAllClubs() {
        getWithAuth("/api/clubs")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(0)));
    }

    @Test
    @DisplayName("Update club - 200 OK")
    void updateClub() {
        String createBody = """
            {
                "name": "Vor Update",
                "city": "Hamburg"
            }
            """;

        Long id = postWithAuth("/api/clubs", createBody)
                .jsonPath().getLong("id");

        String updateBody = """
            {
                "name": "FC Testverein Updated",
                "description": "Updated Description",
                "city": "Munich"
            }
            """;

        putWithAuth("/api/clubs/" + id, updateBody)
                .then()
                .statusCode(200)
                .body("name", equalTo("FC Testverein Updated"))
                .body("city", equalTo("Munich"));
    }

    @Test
    @DisplayName("Soft delete club - 204 No Content")
    void deleteClub() {
        String createBody = """
            {
                "name": "Zu Loeschen",
                "city": "Frankfurt"
            }
            """;

        Long id = postWithAuth("/api/clubs", createBody)
                .jsonPath().getLong("id");

        deleteWithAuth("/api/clubs/" + id)
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Create club validation error - 400 Bad Request")
    void createClub_ValidationError() {
        String requestBody = """
            {
                "name": "",
                "description": "Test"
            }
            """;

        postWithAuth("/api/clubs", requestBody)
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Get club unauthorized - 401 Unauthorized")
    void getClubUnauthorized() {
        given()
                .when()
                .get(getBaseUrl() + "/api/clubs")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("Search clubs - 200 OK")
    void searchClubs() {
        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("search", "Test")
                .when()
                .get(getBaseUrl() + "/api/clubs")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Pagination clubs - 200 OK")
    void paginationClubs() {
        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get(getBaseUrl() + "/api/clubs")
                .then()
                .statusCode(200)
                .body("content", is(notNullValue()))
                .body("totalElements", is(notNullValue()));
    }
}