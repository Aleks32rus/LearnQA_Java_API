import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class HelloWorldTests {

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

    @Test
    public void testEx8() throws InterruptedException {
        String url = "https://playground.learnqa.ru/ajax/api/longtime_job";
        JsonPath response = RestAssured
                .get(url)
                .jsonPath();
        String token = response.get("token");
        int seconds = response.get("seconds");

        Map<String, String> params = new HashMap<>();
        params.put("token", token);

        JsonPath response2 = RestAssured
                .given()
                .queryParams(params)
                .get(url)
                .jsonPath();

        String status = response2.get("status");
        if (status.equals("Job is NOT ready")) {
            Thread.sleep(seconds * 1000L);
            JsonPath response3 = RestAssured
                    .given()
                    .queryParams(params)
                    .get(url)
                    .jsonPath();
            status = response3.get("status");
            String result = response3.get("result");
            if (status.equals("Job is ready") & (result == null)) {
                System.out.println("The key 'result' is absent");
            }
            System.out.println(status);
            System.out.println(result);
        } else System.out.println("Status is incorrect");
    }

    @Test
    public void testEx10() {
        String text = "fewofijewwwwwwwwwwwwww";
        assertTrue(text.length() >= 15, "Incorrect number of characters in the text");
    }

    @Test
    public void testEx11() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();
        Map<String, String> cookies = response.getCookies();
        assertTrue(cookies.containsKey("HomeWork"), "Invalid key");
        assertTrue(cookies.containsValue("hw_value"), "Invalid value");
    }

    @Test
    public void testEx12() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();
        Headers header = response.getHeaders();
        System.out.println(header);
        assertTrue(header.hasHeaderWithName("x-secret-homework-header"), "Invalid header");
        assertEquals("Some secret value", header.getValue("x-secret-homework-header"), "Invalid value header");

    }

    @ParameterizedTest
    @CsvSource({"'Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30', Mobile, No, Android",
            "'Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1', Mobile, Chrome, iOS",
            "'Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)', Googlebot, Unknown, Unknown",
            "'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0', Web, Chrome, No",
            "'Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1', Mobile, No, iPhone"}
    )
    public void testEx13(String userAgent, String platform, String browser, String device) {
        JsonPath response = RestAssured
                .given()
                .header("User-Agent", userAgent)
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .jsonPath();

        String responsePlatform = response.getString("platform");
        String responseBrowser = response.getString("browser");
        String responseDevice = response.getString("device");

        assertEquals(platform, responsePlatform, ("User Agent= " + userAgent + "с неправильным " + "platform=" + responsePlatform));
        assertEquals(browser, responseBrowser, ("User Agent= " +  userAgent + "с неправильным " + "browser= "  + responseBrowser));
        assertEquals(device, responseDevice, ("User Agent= " + userAgent + "с неправильным " + "device = " + responseDevice));
    }
}
