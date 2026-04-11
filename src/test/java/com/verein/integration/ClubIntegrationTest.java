package com.verein.integration;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ClubIntegrationTest extends AbstractIntegrationTest {

    private static final String TEST_USER = "testuser";
    private static final String TEST_PASS = "testpass123";
    private static final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        register(TEST_USER, TEST_PASS, TEST_EMAIL);
        login(TEST_USER, TEST_PASS);
    }

    @Test
    @DisplayName("Club erstellen - 201 Created")
    void testCreateClub() {
        String requestBody = """
            {
                "name": "Test Verein",
                "description": "Test Beschreibung",
                "city": "Berlin",
                "foundedDate": "2020-01-15"
            }
            """;

        postWithAuth("/api/clubs", requestBody)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Test Verein"))
                .body("city", equalTo("Berlin"));
    }

    @Test
    @DisplayName("Club Liste abrufen - 200 OK")
    void testGetClubs() {
        getWithAuth("/api/clubs")
                .then()
                .statusCode(200)
                .body("content", is(notNullValue()));
    }

    @Test
    @DisplayName("Club nach ID abrufen - 200 OK")
    void testGetClubById() {
        String requestBody = """
            {
                "name": "Einzelner Verein",
                "city": "München"
            }
            """;

        Long clubId = postWithAuth("/api/clubs", requestBody)
                .jsonPath().getLong("id");

        getWithAuth("/api/clubs/" + clubId)
                .then()
                .statusCode(200)
                .body("id", equalTo(clubId.intValue()))
                .body("name", equalTo("Einzelner Verein"));
    }

    @Test
    @DisplayName("Club aktualisieren - 200 OK")
    void testUpdateClub() {
        String createBody = """
            {
                "name": "Vor Update",
                "city": "Hamburg"
            }
            """;

        Long clubId = postWithAuth("/api/clubs", createBody)
                .jsonPath().getLong("id");

        String updateBody = """
            {
                "name": "Nach Update",
                "city": "Köln"
            }
            """;

        putWithAuth("/api/clubs/" + clubId, updateBody)
                .then()
                .statusCode(200)
                .body("name", equalTo("Nach Update"))
                .body("city", equalTo("Köln"));
    }

    @Test
    @DisplayName("Club soft delete - 204 No Content")
    void testSoftDeleteClub() {
        String requestBody = """
            {
                "name": "Zu Löschen",
                "city": "Frankfurt"
            }
            """;

        Long clubId = postWithAuth("/api/clubs", requestBody)
                .jsonPath().getLong("id");

        deleteWithAuth("/api/clubs/" + clubId)
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Club Suche - 200 OK")
    void testSearchClubs() {
        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("search", "Test")
                .when()
                .get(getBaseUrl() + "/api/clubs")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Club Pagination - 200 OK")
    void testPagination() {
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

    @Test
    @DisplayName("Unautorisiert ohne Token - 401 Unauthorized")
    void testUnauthorized() {
        given()
                .when()
                .get(getBaseUrl() + "/api/clubs")
                .then()
                .statusCode(401);
    }
}