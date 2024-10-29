package order;

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

import static java.net.HttpURLConnection.*;
import static org.junit.Assert.*;

@Epic("Создание заказа")
public class OrderCreationTest extends BaseTest {

    private String accessToken; // Токен для авторизации пользователя
    private User testUser; // Пользователь для тестирования

    @Before
    public void setUp() {
        // Регистрирую пользователя
        testUser = new User(ApiSteps.generateUniqueEmail(), TestConstants.PASSWORD, TestConstants.TEST_USER_NAME);
        ApiSteps.createUser(testUser);

        // Авторизуюсь для получения токена
        accessToken = getAccessToken(testUser.getEmail(), testUser.getPassword());
    }

    @After
    public void tearDown() {
        // После прогона теста удаляю пользователя
        if (accessToken != null && testUser != null) {
            deleteUserAfterTest(new UserCredentials(testUser.getEmail(), testUser.getPassword()), accessToken);
        }
    }

    @Test
    @DisplayName("Успешное создание заказа")
    public void createOrderSuccessfully() {
        // Получаю ингредиенты для заказа
        String[] ingredients = ApiSteps.getIngredients();

        // Запрос на создание заказа
        Response response = ApiSteps.createOrder(ingredients, accessToken);

        // Проверка - код ответа должен быть 200
        checkResponseStatus(response, HTTP_OK);

        // Проверка успешного создания заказа
        assertTrue(response.jsonPath().getBoolean(TestConstants.SUCCESS_FIELD));
        assertNotNull(response.jsonPath().getString(TestConstants.FIELD_ORDER_NUMBER));
    }

    @Test
    @DisplayName("Попытка создания заказа без ингредиентов")
    public void createOrderWithoutIngredients() {
        // Пытаюсь создать заказ без добавления ингредиентов
        Response response = ApiSteps.createOrder(new String[]{}, accessToken);

        // Проверка - код ответа должен быть 400
        checkResponseStatus(response, HTTP_BAD_REQUEST);

        // Проверяю текст сообщения об ошибке
        String errorMessage = response.jsonPath().getString(TestConstants.MESSAGE_FIELD);
        assertEquals(TestConstants.MESSAGE_NO_INGREDIENTS, errorMessage);
    }

    @Test
    @DisplayName("Попытка создания заказа с невалидными ингредиентами")
    public void createOrderWithInvalidIngredients() {
        // Получаем валидные ингредиенты
        String[] validIngredients = ApiSteps.getIngredients();

        // В валидных ингредиентах меняю хеш, для того что бы они стали невалидными
        String[] invalidIngredients = validIngredients.clone();
        invalidIngredients[0] = invalidIngredients[0].replace("a", "z");

        // Пробую создать заказ с невалидными ингредиентами
        Response response = ApiSteps.createOrder(invalidIngredients, accessToken);

        // Проверка - код ответа должен быть 500
        checkResponseStatus(response, HTTP_INTERNAL_ERROR);

        // Проверяю наличие текста сообщения об ошибке
        String responseBody = response.getBody().asString();
        assertTrue(TestConstants.MESSAGE_INTERNAL_ERROR, responseBody.contains(TestConstants.MESSAGE_INTERNAL_ERROR));
    }
}