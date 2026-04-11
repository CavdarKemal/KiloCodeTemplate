package com.verein.e2e;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("Member E2E Tests")
public class MemberE2ETest extends BaseE2ETest {

    private static Long clubId;
    private static Long memberId;

    @BeforeEach
    void setUp() {
        String username = uniqueUsername();
        register(username, "password123", username + "@test.com");
        login(username, "password123");
        createTestClub();
    }

    private void createTestClub() {
        String clubRequest = """
            {
                "name": "Test Club",
                "description": "Test Description",
                "city": "Berlin"
            }
            """;

        clubId = postWithAuth("/api/clubs", clubRequest)
                .jsonPath().getLong("id");
    }

    @Test
    @DisplayName("Create member - 201 Created")
    void createMember() {
        String requestBody = """
            {
                "firstName": "Max",
                "lastName": "Mustermann",
                "email": "max.mustermann@test.de",
                "phoneNumber": "123456789",
                "birthDate": "1990-01-15",
                "gender": "M",
                "membershipType": "REGULAR",
                "status": "ACTIVE",
                "clubId": %d
            }
            """.formatted(clubId);

        Response response = postWithAuth("/api/members", requestBody)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("firstName", equalTo("Max"))
                .body("lastName", equalTo("Mustermann"))
                .body("email", equalTo("max.mustermann@test.de"))
                .body("clubId", equalTo(clubId.intValue()))
                .extract().response();

        memberId = response.path("id");
    }

    @Test
    @DisplayName("Get member by ID - 200 OK")
    void getMemberById() {
        String createBody = """
            {
                "firstName": "Hans",
                "lastName": "Mueller",
                "email": "hans@test.de",
                "membershipType": "REGULAR",
                "status": "ACTIVE",
                "clubId": %d
            }
            """.formatted(clubId);

        Long id = postWithAuth("/api/members", createBody)
                .jsonPath().getLong("id");

        getWithAuth("/api/members/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("firstName", equalTo("Hans"))
                .body("email", equalTo("hans@test.de"));
    }

    @Test
    @DisplayName("Get all members - 200 OK")
    void getAllMembers() {
        getWithAuth("/api/members")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(0)));
    }

    @Test
    @DisplayName("Get members by club - 200 OK")
    void getMembersByClub() {
        String memberBody = """
            {
                "firstName": "Member",
                "lastName": "Test",
                "email": "member@test.de",
                "membershipType": "REGULAR",
                "status": "ACTIVE",
                "clubId": %d
            }
            """.formatted(clubId);

        postWithAuth("/api/members", memberBody);

        getWithAuth("/api/members/club/" + clubId)
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("[0].clubId", equalTo(clubId.intValue()));
    }

    @Test
    @DisplayName("Update member - 200 OK")
    void updateMember() {
        String createBody = """
            {
                "firstName": "Max",
                "lastName": "Mustermann",
                "email": "max@test.de",
                "membershipType": "REGULAR",
                "status": "ACTIVE",
                "clubId": %d
            }
            """.formatted(clubId);

        Long id = postWithAuth("/api/members", createBody)
                .jsonPath().getLong("id");

        String updateBody = """
            {
                "firstName": "Max",
                "lastName": "Mustermann Updated",
                "email": "max.updated@test.de",
                "phoneNumber": "987654321",
                "membershipType": "SENIOR",
                "status": "ACTIVE",
                "clubId": %d
            }
            """.formatted(clubId);

        putWithAuth("/api/members/" + id, updateBody)
                .then()
                .statusCode(200)
                .body("lastName", equalTo("Mustermann Updated"))
                .body("email", equalTo("max.updated@test.de"))
                .body("membershipType", equalTo("SENIOR"));
    }

    @Test
    @DisplayName("Soft delete member - 204 No Content")
    void deleteMember() {
        String createBody = """
            {
                "firstName": "Loesch",
                "lastName": "User",
                "email": "loesch@test.de",
                "membershipType": "REGULAR",
                "status": "ACTIVE",
                "clubId": %d
            }
            """.formatted(clubId);

        Long id = postWithAuth("/api/members", createBody)
                .jsonPath().getLong("id");

        deleteWithAuth("/api/members/" + id)
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Create member duplicate email - 400 Bad Request")
    void createMember_DuplicateEmail() {
        String createBody = """
            {
                "firstName": "Erster",
                "lastName": "User",
                "email": "duplicate@test.de",
                "membershipType": "REGULAR",
                "status": "ACTIVE",
                "clubId": %d
            }
            """.formatted(clubId);

        postWithAuth("/api/members", createBody);

        String duplicateBody = """
            {
                "firstName": "Zweiter",
                "lastName": "User",
                "email": "duplicate@test.de",
                "membershipType": "REGULAR",
                "status": "ACTIVE",
                "clubId": %d
            }
            """.formatted(clubId);

        postWithAuth("/api/members", duplicateBody)
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Create member validation error - 400 Bad Request")
    void createMember_ValidationError() {
        String requestBody = """
            {
                "firstName": "",
                "lastName": "",
                "email": "invalid-email",
                "membershipType": "REGULAR",
                "clubId": %d
            }
            """.formatted(clubId);

        postWithAuth("/api/members", requestBody)
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Create member club not found - 400 Bad Request")
    void createMember_ClubNotFound() {
        String requestBody = """
            {
                "firstName": "Max",
                "lastName": "Mustermann",
                "email": "max@test.de",
                "membershipType": "REGULAR",
                "status": "ACTIVE",
                "clubId": 99999
            }
            """;

        postWithAuth("/api/members", requestBody)
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Get member unauthorized - 401 Unauthorized")
    void getMemberUnauthorized() {
        given()
                .when()
                .get(getBaseUrl() + "/api/members")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("Search members - 200 OK")
    void searchMembers() {
        given()
                .header("Authorization", "Bearer " + token)
                .queryParam("search", "Max")
                .when()
                .get(getBaseUrl() + "/api/members")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Pagination members - 200 OK")
    void paginationMembers() {
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
}