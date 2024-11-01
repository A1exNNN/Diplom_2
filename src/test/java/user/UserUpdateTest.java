package user;

import base.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.User;
import models.UserCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.ApiSteps;
import utils.TestConstants;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;

@Epic("Тесты для изменения данных пользователя")
public class UserUpdateTest extends BaseTest {

    private String accessToken; // Токен авторизованного пользователя
    private User testUser; // Созданный пользователь

    @Before
    public void setUp() {
        // Создаю пользователя и авторизуюсь с ним
        testUser = new User(ApiSteps.generateUniqueEmail(), TestConstants.PASSWORD, TestConstants.TEST_USER_NAME);
        ApiSteps.createUser(testUser);
        accessToken = getAccessToken(testUser.getEmail(), testUser.getPassword());
    }

    @After
    public void tearDown() {
        // После прогона теста удаляю пользователя
        if (testUser != null && accessToken != null) {
            deleteUserAfterTest(new UserCredentials(testUser.getEmail(), testUser.getPassword()), accessToken);
        }
    }

    @Test
    @DisplayName("Успешное обновление данных с авторизацией")
    public void updateUserDataWithAuthorization() {
        // Создаю пользователя
        User updatedUser = new User(ApiSteps.generateUniqueEmail(), TestConstants.PASSWORD, TestConstants.UPDATED_USER_NAME);

        // Отправляю запрос на обновление данных пользователя
        Response response = ApiSteps.updateUserWithAuthorization(accessToken, updatedUser);

        // Проверка - код ответа должен быть 200
        checkResponseStatus(response, HTTP_OK);

        // Проверяем, что данные обновлены
        assertEquals(updatedUser.getEmail(), response.jsonPath().getString(TestConstants.FIELD_USER_EMAIL));
        assertEquals(updatedUser.getName(), response.jsonPath().getString(TestConstants.FIELD_USER_NAME));
    }


    @Test
    @DisplayName("Попытка обновления данных без авторизации")
    public void updateUserDataWithoutAuthorization() {

        // Создаю пользователя
        User updatedUser = new User(ApiSteps.generateUniqueEmail(), TestConstants.PASSWORD, TestConstants.UPDATED_USER_NAME);

        // Отправляю запрос на обновление данных пользователя без авторизации
        Response response = ApiSteps.updateUserWithoutAuthorization(updatedUser);

        //  Проверка - код ответа должен быть 401
        checkResponseStatus(response, HTTP_UNAUTHORIZED);

        // Проверяю текст сообщения об ошибке
        String errorMessage = response.jsonPath().getString(TestConstants.MESSAGE_FIELD);
        assertEquals(TestConstants.MESSAGE_UNAUTHORIZED, errorMessage);
    }
}