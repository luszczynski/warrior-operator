package org.example;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class QuarkusAppTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("${path}")
          .then()
             .statusCode(200)
             .body(is("hello"));
    }

}