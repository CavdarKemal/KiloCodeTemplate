package com.verein.e2e;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OpenApiE2ETest {

    @Test
    void testOpenApiDocsAvailable() {
        given()
        .when()
            .get("/v3/api-docs")
        .then()
            .statusCode(200)
            .body("openapi", startsWith("3."))
            .body("info.title", equalTo("Verein Verwaltung API"))
            .body("info.version", equalTo("1.0.0"));
    }

    @Test
    void testSwaggerUiAvailable() {
        given()
        .when()
            .get("/swagger-ui.html")
        .then()
            .statusCode(200);
    }

    @Test
    void testSwaggerUiIndexAvailable() {
        given()
        .when()
            .get("/swagger-ui/index.html")
        .then()
            .statusCode(200);
    }

    @Test
    void testOpenApiDocsHasAuthEndpoints() {
        given()
        .when()
            .get("/v3/api-docs")
        .then()
            .statusCode(200)
            .body("paths.'/api/auth/register'", notNullValue())
            .body("paths.'/api/auth/login'", notNullValue());
    }

    @Test
    void testOpenApiDocsHasClubEndpoints() {
        given()
        .when()
            .get("/v3/api-docs")
        .then()
            .statusCode(200)
            .body("paths.'/api/clubs'", notNullValue())
            .body("paths.'/api/clubs/{id}'", notNullValue())
            .body("paths.'/api/clubs/paginated'", notNullValue());
    }

    @Test
    void testOpenApiDocsHasMemberEndpoints() {
        given()
        .when()
            .get("/v3/api-docs")
        .then()
            .statusCode(200)
            .body("paths.'/api/members'", notNullValue())
            .body("paths.'/api/members/{id}'", notNullValue())
            .body("paths.'/api/members/paginated'", notNullValue());
    }

    @Test
    void testOpenApiDocsHasSecurityScheme() {
        given()
        .when()
            .get("/v3/api-docs")
        .then()
            .statusCode(200)
            .body("components.securitySchemes.'Bearer Authentication'", notNullValue());
    }

    @Test
    void testOpenApiDocsHasServerInfo() {
        given()
        .when()
            .get("/v3/api-docs")
        .then()
            .statusCode(200)
            .body("servers", notNullValue())
            .body("servers.size()", greaterThan(0));
    }
}