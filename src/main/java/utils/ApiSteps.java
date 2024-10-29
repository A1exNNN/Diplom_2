package utils;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.Order;
import models.User;
import models.UserCredentials;
import io.qameta.allure.Param;

import static io.qameta.allure.model.Parameter.Mode.HIDDEN;


import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static java.net.HttpURLConnection.*;

// Класс для выполнения запросов API
public class ApiSteps {

    @Step("Создание уникального email")
    public static String generateUniqueEmail() {
        return TestConstants.UNIQUE_EMAIL_PREFIX + System.currentTimeMillis() + TestConstants.EMAIL_DOMAIN;
    }

    @Step("Создание пользователя")
    public static Response createUser(User user) {
        return RestAssured
                .given().log().all()
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .body(user)
                .post(TestConstants.ENDPOINT_REGISTER);
    }

    @Step("Логин пользователя")
    public static Response loginUser(UserCredentials credentials) {
        return RestAssured
                .given().log().all()
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .body(credentials)
                .post(TestConstants.ENDPOINT_LOGIN);
    }

    @Step("Удаление пользователя")
    public static void deleteUser(UserCredentials credentials, @Param(mode = HIDDEN) String accessToken) {
        // Печатаем токен для проверки
        System.out.println("Access Token: " + accessToken);
        RestAssured
                .given().log().all()
                .header(TestConstants.AUTHORIZATION_HEADER, accessToken)
                .delete(TestConstants.ENDPOINT_USER)
                .then()
                .statusCode(anyOf(is(HTTP_OK), is(HTTP_ACCEPTED))); // Проверка успешного удаления с кодами 200 или 202
    }

    @Step("Обновление данных пользователя с авторизацией")
    public static Response updateUserWithAuthorization(@Param(mode = HIDDEN) String accessToken, User updatedUser) {
        return RestAssured
                .given().log().all()
                .header(TestConstants.AUTHORIZATION_HEADER, accessToken)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .body(updatedUser)
                .patch(TestConstants.ENDPOINT_USER);
    }

    @Step("Обновление данных пользователя без авторизации")
    public static Response updateUserWithoutAuthorization(User user) {
        return RestAssured
                .given().log().all()
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .body(user)
                .patch(TestConstants.ENDPOINT_USER);
    }

    @Step("Получение списка ингредиентов")
    public static String[] getIngredients() {
        Response response = RestAssured
                .given().log().all()
                .get(TestConstants.ENDPOINT_INGREDIENTS);

        response.then().statusCode(HTTP_OK);
        return response.jsonPath().getList(TestConstants.FIELD_INGREDIENTS, String.class).toArray(new String[0]);
    }

    @Step("Создание заказа")
    public static Response createOrder(String[] ingredients, @Param(mode = HIDDEN) String accessToken) {
        return RestAssured
                .given().log().all()
                .header(TestConstants.AUTHORIZATION_HEADER, accessToken)
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .body(new Order(ingredients))
                .post(TestConstants.ENDPOINT_ORDERS);
    }

    @Step("Получение заказов авторизованного пользователя")
    public static Response getUserOrdersWithAuthorization(@Param(mode = HIDDEN) String accessToken) {
        return RestAssured
                .given().log().all()
                .header(TestConstants.AUTHORIZATION_HEADER, accessToken)
                .get(TestConstants.ENDPOINT_ORDERS);
    }

    @Step("Получение заказов неавторизованного пользователя")
    public static Response getUserOrdersWithoutAuthorization() {
        return RestAssured
                .given().log().all()
                .get(TestConstants.ENDPOINT_ORDERS);
    }

}