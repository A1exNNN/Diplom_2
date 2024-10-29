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

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@Epic("Тесты для получения заказов пользователем")
public class OrderRetrievalTest extends BaseTest {

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
    @DisplayName("Получение заказов авторизованным пользователем")
    public void getUserOrdersWithAuthorization() {
        // Создаю заказ
        String[] ingredients = ApiSteps.getIngredients();
        Response createOrderResponse = ApiSteps.createOrder(ingredients, accessToken);
        checkResponseStatus(createOrderResponse, HTTP_OK);

        // Отправляю запрос на получение заказов
        Response response = ApiSteps.getUserOrdersWithAuthorization(accessToken);

        // Проверка - код ответа должен быть 200
        checkResponseStatus(response, HTTP_OK);

        // Проверяю наличие заказа в списке заказов
        assertFalse(TestConstants.ORDERS_LIST_EMPTY_MESSAGE, response.jsonPath().getList(TestConstants.FIELD_ORDERS).isEmpty());
    }

    @Test
    @DisplayName("Получение заказов неавторизованным пользователем")
    public void getUserOrdersWithoutAuthorization() {
        // Создаю заказ
        String[] ingredients = ApiSteps.getIngredients();
        Response createOrderResponse = ApiSteps.createOrder(ingredients, accessToken);
        checkResponseStatus(createOrderResponse, HTTP_OK);

        // Отправляю запрос на получение заказов
        Response response = ApiSteps.getUserOrdersWithoutAuthorization();

        // Проверка - код ответа должен быть 401
        checkResponseStatus(response, HTTP_UNAUTHORIZED);

        // Проверяю наличие текста сообщения об ошибке
        String errorMessage = response.jsonPath().getString(TestConstants.MESSAGE_FIELD);
        assertEquals(TestConstants.MESSAGE_UNAUTHORIZED, errorMessage);
    }
}