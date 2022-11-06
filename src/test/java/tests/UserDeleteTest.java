package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("User delete cases")
@Feature("User delete")
public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @DisplayName("Delete existing user")
    @Link("https://software-testing.ru/lms/mod/assign/view.php?id=289484")
    public void testDeleteExistingUser() {

        String email = "vinkotov@example.com";
        Map<String, String> authData = new HashMap<>();
        authData.put("email", email);
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest(
                        "https://playground.learnqa.ru/api/user/2",
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/2",
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertJsonByName(responseUserData, "email", email);

    }

    @Test
    @DisplayName("Delete just created user")
    @Link("https://software-testing.ru/lms/mod/assign/view.php?id=289484")
    public void testDeleteJustCreatedUser() {

        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData).jsonPath();

        String userId = responseCreateAuth.getString("id");


        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertResponseTextEquals(responseUserData, "User not found");

    }

    @Test
    @DisplayName("Delete just created user with authorization as other user")
    @Link("https://software-testing.ru/lms/mod/assign/view.php?id=289484")
    public void testDeleteJustCreatedUserAuthorizationAsOtherUser() {

        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData).jsonPath();

        String userId = responseCreateAuth.getString("id");

        String email = "vinkotov@example.com";
        Map<String, String> authData = new HashMap<>();
        authData.put("email", email);
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Map<String, String> authData1 = new HashMap<>();
        authData1.put("email", userData.get("email"));
        authData1.put("password", userData.get("password"));

        Response responseGetAuth1 = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData1);

        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth1, "x-csrf-token"),
                        this.getCookie(responseGetAuth1, "auth_sid"));

        System.out.println(responseUserData.asString());
        Assertions.assertJsonByName(responseUserData, "email", userData.get("email"));

    }
}
