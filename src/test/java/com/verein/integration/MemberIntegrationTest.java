package com.verein.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class MemberIntegrationTest extends AbstractIntegrationTest {

    private static final String TEST_USER = "memberuser";
    private static final String TEST_PASS = "memberpass123";
    private static final String TEST_EMAIL = "member@example.com";

    @BeforeEach
    void setUp() {
        register(TEST_USER, TEST_PASS, TEST_EMAIL);
        login(TEST_USER, TEST_PASS);
    }

    @Test
    @DisplayName("Member erstellen - 201 Created")
    void testCreateMember() {
        String requestBody = """
            {
                "firstName": "Max",
                "lastName": "Mustermann",
                "email": "max@example.com",
                "phone": "+49123456789",
                "membershipType": "ACTIVE"
            }
            """;

        postWithAuth("/api/members", requestBody)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("firstName", equalTo("Max"))
                .body("lastName", equalTo("Mustermann"));
    }

    @Test
    @DisplayName("Member Liste abrufen - 200 OK")
    void testGetMembers() {
        getWithAuth("/api/members")
                .then()
                .statusCode(200)
                .body("content", is(notNullValue()));
    }

    @Test
    @DisplayName("Member nach ID abrufen - 200 OK")
    void testGetMemberById() {
        String requestBody = """
            {
                "firstName": "Hans",
                "lastName": "Mueller",
                "email": "hans@example.com",
                "membershipType": "ACTIVE"
            }
            """;

        Long memberId = postWithAuth("/api/members", requestBody)
                .jsonPath().getLong("id");

        getWithAuth("/api/members/" + memberId)
                .then()
                .statusCode(200)
                .body("id", equalTo(memberId.intValue()))
                .body("firstName", equalTo("Hans"));
    }

    @Test
    @DisplayName("Member aktualisieren - 200 OK")
    void testUpdateMember() {
        String createBody = """
            {
                "firstName": "Vor",
                "lastName": "Update",
                "email": "vor@example.com",
                "membershipType": "ACTIVE"
            }
            """;

        Long memberId = postWithAuth("/api/members", createBody)
                .jsonPath().getLong("id");

        String updateBody = """
            {
                "firstName": "Nach",
                "lastName": "Update",
                "email": "nach@example.com",
                "membershipType": "PASSIVE"
            }
            """;

        putWithAuth("/api/members/" + memberId, updateBody)
                .then()
                .statusCode(200)
                .body("firstName", equalTo("Nach"))
                .body("membershipType", equalTo("PASSIVE"));
    }

    @Test
    @DisplayName("Member soft delete - 204 No Content")
    void testSoftDeleteMember() {
        String requestBody = """
            {
                "firstName": "Loesch",
                "lastName": "User",
                "email": "loesch@example.com",
                "membershipType": "ACTIVE"
            }
            """;

        Long memberId = postWithAuth("/api/members", requestBody)
                .jsonPath().getLong("id");

        deleteWithAuth("/api/members/" + memberId)
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Member Suche - 200 OK")
    void testSearchMembers() {
        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("search", "Max")
                .when()
                .get(getBaseUrl() + "/api/members")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Member Pagination - 200 OK")
    void testPagination() {
        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get(getBaseUrl() + "/api/members")
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
                .get(getBaseUrl() + "/api/members")
                .then()
                .statusCode(401);
    }
}