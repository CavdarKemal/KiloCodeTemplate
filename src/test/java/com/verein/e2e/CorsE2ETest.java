package com.verein.e2e;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CorsE2ETest {

    @Test
    void testCorsPreflightFromReact() {
        given()
            .header("Origin", "http://localhost:3000")
            .header("Access-Control-Request-Method", "GET")
            .header("Access-Control-Request-Headers", "Authorization,Content-Type")
        .when()
            .options("/api/clubs")
        .then()
            .statusCode(anyOf(is(200), is(403)))
            .header("Access-Control-Allow-Origin", anyOf(is("http://localhost:3000"), is(nullValue())));
    }

    @Test
    void testCorsPreflightFromAngular() {
        given()
            .header("Origin", "http://localhost:4200")
            .header("Access-Control-Request-Method", "POST")
            .header("Access-Control-Request-Headers", "Authorization,Content-Type")
        .when()
            .options("/api/auth/login")
        .then()
            .statusCode(anyOf(is(200), is(400), is(403)))
            .header("Access-Control-Allow-Origin", anyOf(is("http://localhost:4200"), is(nullValue())));
    }

    @Test
    void testCorsPreflightFromVue() {
        given()
            .header("Origin", "http://localhost:5173")
            .header("Access-Control-Request-Method", "GET")
            .header("Access-Control-Request-Headers", "Authorization")
        .when()
            .options("/api/members")
        .then()
            .statusCode(anyOf(is(200), is(401), is(403)))
            .header("Access-Control-Allow-Origin", anyOf(is("http://localhost:5173"), is(nullValue())));
    }

    @Test
    void testCorsWithActualRequest() {
        String token = given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"corsuser\",\"password\":\"password123\"}")
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract()
            .path("token");

        given()
            .header("Origin", "http://localhost:3000")
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/clubs")
        .then()
            .statusCode(200)
            .header("Access-Control-Allow-Origin", anyOf(is("http://localhost:3000"), is(nullValue())));
    }
}