package uit.carbon_shop.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import uit.carbon_shop.config.BaseIT;


public class ChatMessageResourceTest extends BaseIT {

    @Test
    @Sql("/data/chatMessageData.sql")
    void getAllChatMessages_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/chatMessages")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(2))
                    .body("content.get(0).id", Matchers.equalTo(1800));
    }

    @Test
    @Sql("/data/chatMessageData.sql")
    void getAllChatMessages_filtered() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/chatMessages?filter=1801")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(1))
                    .body("content.get(0).id", Matchers.equalTo(1801));
    }

    @Test
    @Sql("/data/chatMessageData.sql")
    void getChatMessage_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/chatMessages/1800")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("content", Matchers.equalTo("Eget est lorem."));
    }

    @Test
    void getChatMessage_notFound() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/chatMessages/2466")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    void createChatMessage_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/chatMessageDTORequest.json"))
                .when()
                    .post("/api/chatMessages")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(1, chatMessageRepository.count());
    }

    @Test
    @Sql("/data/chatMessageData.sql")
    void updateChatMessage_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/chatMessageDTORequest.json"))
                .when()
                    .put("/api/chatMessages/1800")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals("Consectetuer adipiscing.", chatMessageRepository.findById(((long)1800)).orElseThrow().getContent());
        assertEquals(2, chatMessageRepository.count());
    }

    @Test
    @Sql("/data/chatMessageData.sql")
    void deleteChatMessage_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .delete("/api/chatMessages/1800")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, chatMessageRepository.count());
    }

}
