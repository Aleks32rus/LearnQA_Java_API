import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.hamcrest.core.IsNull.notNullValue;

public class HelloWorldTest {

    @Test
    public void testHelloWorld() {
        System.out.println("Hello from Aleksey");
    }

    @Test
    public void testFirst() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        System.out.println(response.asString());
    }

    @Test
    public void testEx5() {
        JsonPath response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        String answer = response.get("messages.message[1]");
        System.out.println(answer);
    }

    @Test
    public void testEx6() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        System.out.println(response.getHeader("Location"));
    }

    @Test
    public void testEx7() {
        int statusCode, amountOfRedirects = 0;
        String url = "https://playground.learnqa.ru/api/long_redirect";
        while (true) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(url)
                    .andReturn();
            statusCode = response.getStatusCode();
            url = response.getHeader("Location");
            if (statusCode == 200) break;
            System.out.println("Redirect link #" + (amountOfRedirects + 1) + ": " + url + ". Status code: " + statusCode);
            amountOfRedirects++;
        }
        System.out.println("Amount of redirects: " + amountOfRedirects);
    }

}
