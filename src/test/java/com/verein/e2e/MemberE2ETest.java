package com.verein.e2e;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class MemberE2ETest extends BaseE2ETest {

    private static Long clubId;
    private static Long memberId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        createTestClub();
    }

    private void createTestClub() {
        String clubRequest = "{" +
                "\"name\": \"Test Club\"," +
                "\"description\": \"Test Description\"," +
                "\"city\": \"Berlin\"" +
                "}";

        clubId = given()
                .contentType(ContentType.JSON)
                .body(clubRequest)
                .when()
                .post("/api/clubs")
                .then()
                .statusCode(201)
                .extract().path("id");
    }

    @Test
    void createMember() {
        String requestBody = "{" +
                "\"firstName\": \"Max\"," +
                "\"lastName\": \"Mustermann\"," +
                "\"email\": \"max.mustermann@test.de\"," +
                "\"phoneNumber\": \"123456789\"," +
                "\"birthDate\": \"1990-01-15\"," +
                "\"gender\": \"M\"," +
                "\"membershipType\": \"REGULAR\"," +
                "\"status\": \"ACTIVE\"," +
                "\"clubId\": " + clubId +
                "}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/members")
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
    void getMemberById() {
        createMember();

        given()
                .when()
                .get("/api/members/" + memberId)
                .then()
                .statusCode(200)
                .body("id", equalTo(memberId.intValue()))
                .body("firstName", equalTo("Max"))
                .body("email", equalTo("max.mustermann@test.de"));
    }

    @Test
    void getAllMembers() {
        given()
                .when()
                .get("/api/members")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(0)));
    }

    @Test
    void getMembersByClub() {
        createMember();

        given()
                .when()
                .get("/api/members/club/" + clubId)
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("[0].clubId", equalTo(clubId.intValue()));
    }

    @Test
    void updateMember() {
        createMember();

        String updateBody = "{" +
                "\"firstName\": \"Max\"," +
                "\"lastName\": \"Mustermann Updated\"," +
                "\"email\": \"max.updated@test.de\"," +
                "\"phoneNumber\": \"987654321\"," +
                "\"membershipType\": \"SENIOR\"," +
                "\"status\": \"ACTIVE\"," +
                "\"clubId\": " + clubId +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(updateBody)
                .when()
                .put("/api/members/" + memberId)
                .then()
                .statusCode(200)
                .body("lastName", equalTo("Mustermann Updated"))
                .body("email", equalTo("max.updated@test.de"))
                .body("membershipType", equalTo("SENIOR"));
    }

    @Test
    void deleteMember() {
        createMember();

        given()
                .when()
                .delete("/api/members/" + memberId)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/api/members/" + memberId)
                .then()
                .statusCode(400);
    }

    @Test
    void createMember_DuplicateEmail() {
        createMember();

        String requestBody = "{" +
                "\"firstName\": \"Anna\"," +
                "\"lastName\": \"Musterfrau\"," +
                "\"email\": \"max.mustermann@test.de\"," +
                "\"membershipType\": \"REGULAR\"," +
                "\"status\": \"ACTIVE\"," +
                "\"clubId\": " + clubId +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/members")
                .then()
                .statusCode(400);
    }

    @Test
    void createMember_ValidationError() {
        String requestBody = "{" +
                "\"firstName\": \"\"," +
                "\"lastName\": \"\"," +
                "\"email\": \"invalid-email\"," +
                "\"membershipType\": \"REGULAR\"," +
                "\"clubId\": " + clubId +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/members")
                .then()
                .statusCode(400);
    }

    @Test
    void createMember_ClubNotFound() {
        String requestBody = "{" +
                "\"firstName\": \"Max\"," +
                "\"lastName\": \"Mustermann\"," +
                "\"email\": \"max@test.de\"," +
                "\"membershipType\": \"REGULAR\"," +
                "\"status\": \"ACTIVE\"," +
                "\"clubId\": 99999" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/members")
                .then()
                .statusCode(400);
    }
}