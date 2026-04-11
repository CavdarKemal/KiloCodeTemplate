package com.verein.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public abstract class AbstractIntegrationTest extends BaseIntegrationTest {

    protected String token;

    protected void login(String username, String password) {
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "username": "%s",
                        "password": "%s"
                    }
                    """.formatted(username, password))
                .when()
                .post(getBaseUrl() + "/api/auth/login");

        if (response.getStatusCode() == 200) {
            token = response.jsonPath().getString("token");
        }
    }

    protected void register(String username, String password, String email) {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "username": "%s",
                        "password": "%s",
                        "email": "%s"
                    }
                    """.formatted(username, password, email))
                .when()
                .post(getBaseUrl() + "/api/auth/register");
    }

    protected Response getWithAuth(String path) {
        return RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get(getBaseUrl() + path);
    }

    protected Response postWithAuth(String path, String body) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(body)
                .when()
                .post(getBaseUrl() + path);
    }

    protected Response putWithAuth(String path, String body) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(body)
                .when()
                .put(getBaseUrl() + path);
    }

    protected Response deleteWithAuth(String path) {
        return RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete(getBaseUrl() + path);
    }
}