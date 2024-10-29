package user;

import base.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.User;
import models.UserCredentials;
import org.junit.After;
import org.junit.Test;
import utils.ApiSteps;
import utils.TestConstants;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Epic("Тесты для регистрации пользователей")
public class UserRegistrationTest extends BaseTest {

    private User testUser; // Созданный пользователь
    private String accessToken; // Токен для удаления пользователя

    @After
    public void tearDown() {
        // После прогона теста удаляю пользователя
        if (testUser != null && accessToken != null) {
            deleteUserAfterTest(new UserCredentials(testUser.getEmail(), testUser.getPassword()), accessToken);
        }
    }

    //Успешное создание уникального пользователя
    @Test
    @DisplayName("Создание уникального пользователя")
    public void createUniqueUser() {
        testUser = new User(ApiSteps.generateUniqueEmail(), TestConstants.PASSWORD, TestConstants.TEST_USER_NAME);

        // Отправляю запрос для создания пользователя
        Response response = ApiSteps.createUser(testUser);

        // Проверка - код ответа должен быть 200
        checkResponseStatus(response, HTTP_OK);

        // Получаю accessToken для дальнейшего удаления пользователя
        accessToken = BaseTest.getAccessToken(testUser.getEmail(), testUser.getPassword());

        // Проверяю поля ответов
        assertTrue(response.jsonPath().getBoolean(TestConstants.SUCCESS_FIELD));
        assertEquals(testUser.getEmail(), response.jsonPath().getString(TestConstants.FIELD_USER_EMAIL));
        assertEquals(testUser.getName(), response.jsonPath().getString(TestConstants.FIELD_USER_NAME));
    }

    @Test
    @DisplayName("Попытка регистрации не уникального пользователя")
    public void createAlreadyRegisteredUser() {
        testUser = new User(ApiSteps.generateUniqueEmail(), TestConstants.PASSWORD, TestConstants.TEST_USER_NAME);

        // Создаем нового пользователя
        Response response = ApiSteps.createUser(testUser);
        // Проверяю, создался ли пользователь
        checkResponseStatus(response, HTTP_OK);

        // Токен что бы удалить пользователя
        accessToken = BaseTest.getAccessToken(testUser.getEmail(), testUser.getPassword());

        // Пробую зарегистрировать еще раз существующего пользователя
        Response secondResponse = ApiSteps.createUser(testUser);

        // Проверка - код ответа должен быть 403
        checkResponseStatus(secondResponse, HTTP_FORBIDDEN);

        // Проверяю текст сообщения об ошибке
        String errorMessage = secondResponse.jsonPath().getString(TestConstants.MESSAGE_FIELD);
        assertEquals(TestConstants.MESSAGE_USER_EXISTS, errorMessage);
    }

    @Test
    @DisplayName("Попытка создания пользователя без поля name")
    public void createUserWithoutRequiredField() {
        // Пытаюсь создать пользователя с name=null
        User userWithoutName = new User(ApiSteps.generateUniqueEmail(), TestConstants.PASSWORD, null);

        // Отправляю запрос на создание пользователя
        Response response = ApiSteps.createUser(userWithoutName);

        // Проверка - код ответа должен быть 403
        checkResponseStatus(response, HTTP_FORBIDDEN);

        // Проверяю текст сообщения об ошибке
        String errorMessage = response.jsonPath().getString(TestConstants.MESSAGE_FIELD);
        assertEquals(TestConstants.MESSAGE_MISSING_FIELDS, errorMessage);
    }
}