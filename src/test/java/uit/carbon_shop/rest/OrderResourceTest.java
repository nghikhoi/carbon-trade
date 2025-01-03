package uit.carbon_shop.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import uit.carbon_shop.config.BaseIT;


public class OrderResourceTest extends BaseIT {

    @Test
    @Sql("/data/orderData.sql")
    void getAllOrders_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/orders")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(2))
                    .body("content.get(0).orderId", Matchers.equalTo(1100));
    }

    @Test
    @Sql("/data/orderData.sql")
    void getAllOrders_filtered() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/orders?filter=1101")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(1))
                    .body("content.get(0).orderId", Matchers.equalTo(1101));
    }

    @Test
    @Sql("/data/orderData.sql")
    void getOrder_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/orders/1100")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("creditAmount", Matchers.equalTo(77));
    }

    @Test
    void getOrder_notFound() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/orders/1766")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    void createOrder_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/orderDTORequest.json"))
                .when()
                    .post("/api/orders")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(1, orderRepository.count());
    }

    @Test
    @Sql("/data/orderData.sql")
    void updateOrder_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/orderDTORequest.json"))
                .when()
                    .put("/api/orders/1100")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals(((long)92), orderRepository.findById(((long)1100)).orElseThrow().getCreditAmount());
        assertEquals(2, orderRepository.count());
    }

    @Test
    @Sql("/data/orderData.sql")
    void deleteOrder_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .delete("/api/orders/1100")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, orderRepository.count());
    }

}
