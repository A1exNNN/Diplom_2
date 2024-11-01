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
import static org.junit.Assert.assertNotNull;

@Epic("Тесты для логина пользователей")
public class UserLoginTest extends BaseTest {

    private String testEmail;  // Email созданного пользователя
    private String password = TestConstants.PASSWORD;
    private String accessToken;  // Токен для удаления пользователя


    @Before
    public void setUp() {
        // Создаю пользователя
        testEmail = ApiSteps.generateUniqueEmail();
        User user = new User(testEmail, password, TestConstants.TEST_USER_NAME);
        ApiSteps.createUser(user);
        // Получаю accessToken для дальнейшего удаления пользователя
        accessToken = BaseTest.getAccessToken(testEmail, password);
    }

    @After
    public void tearDown() {
        // После прогона теста удаляю пользователя
        if (testEmail != null && accessToken != null) {
            deleteUserAfterTest(new UserCredentials(testEmail, password), accessToken);
        }
    }

    @Test
    @DisplayName("Успешный логин существующего пользователя")
    public void successfulUserLogin() {
        UserCredentials credentials = new UserCredentials(testEmail, password);

        // Выполняю запрос на логин
        Response response = ApiSteps.loginUser(credentials);

        // Проверка - код ответа должен быть 200
        checkResponseStatus(response, HTTP_OK);

        // Проверяем, что accessToken был возвращен
        String accessToken = response.jsonPath().getString(TestConstants.FIELD_ACCESS_TOKEN);
        assertNotNull(accessToken);
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    public void loginWithInvalidPassword() {
        UserCredentials credentials = new UserCredentials(testEmail, TestConstants.INVALID_PASSWORD);

        // Выполняю запрос на логин с неверным паролем
        Response response = ApiSteps.loginUser(credentials);

        // Проверка - код ответа должен быть 401
        checkResponseStatus(response, HTTP_UNAUTHORIZED);

        // Проверяю наличие текста сообщения об ошибке
        String errorMessage = response.jsonPath().getString(TestConstants.MESSAGE_FIELD);
        assertEquals(TestConstants.MESSAGE_INVALID_CREDENTIALS, errorMessage);
    }

    @Test
    @DisplayName("Логин с несуществующим пользователем")
    public void loginWithNonExistentUser() {
        UserCredentials credentials = new UserCredentials(TestConstants.NON_EXISTENT_EMAIL, password);

        // Выполняю запрос на логин с несуществующим пользователем
        Response response = ApiSteps.loginUser(credentials);

        // Проверка - код ответа должен быть 401
        checkResponseStatus(response, HTTP_UNAUTHORIZED);

        // Проверяю наличие текста сообщения об ошибке
        String errorMessage = response.jsonPath().getString(TestConstants.MESSAGE_FIELD);
        assertEquals(TestConstants.MESSAGE_INVALID_CREDENTIALS, errorMessage);
    }
}