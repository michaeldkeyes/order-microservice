package com.example.orderservice;

import com.example.orderservice.stub.InventoryStub;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class OrderServiceApplicationTests {

    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:latest"));

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    static {
        mySQLContainer.start();
    }

    @Test
    void shouldSubmitOrder() {
        final String orderRequest = """
                {
                	"skuCode": "SKU-123",
                	"price": 100.00,
                	"quantity": 2
                }
                """;

        InventoryStub.stubInventoryCall("SKU-123", 2);

        final String responseBodyString = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post("/order")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .body()
                .asString();

        assertThat(responseBodyString, Matchers.is("Order placed successfully"));
    }

}
