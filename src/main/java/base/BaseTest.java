package base;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.response.Response;
import models.UserCredentials;
import org.junit.BeforeClass;
import utils.ApiSteps;
import utils.TestConstants;

// Это основной класс для всех тестов.
// Настраиваю основной адрес для RestAssured и общие способы работы.
public class BaseTest {

    // Метод запускается один раз перед тестами
    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = TestConstants.BASE_URI;
        // Устанавливаю время ожидания для всех тестов
        RestAssured.config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        // Время ожидания соединения 5 сек
                        .setParam(TestConstants.HTTP_CONNECTION_TIMEOUT, TestConstants.CONNECTION_TIMEOUT)
                        // Время ожидания ответа 5 сек
                        .setParam(TestConstants.HTTP_SOCKET_TIMEOUT, TestConstants.SOCKET_TIMEOUT));
    }

    // Метод для получения пользователем доступа accessToken
    public static String getAccessToken(String email, String password) {
        UserCredentials credentials = new UserCredentials(email, password);

        Response response = RestAssured
                .given().log().all()
                .contentType(TestConstants.CONTENT_TYPE_JSON)
                .body(credentials)
                .post(TestConstants.ENDPOINT_LOGIN);

        return response
                .then()
                .extract()
                .path(TestConstants.FIELD_ACCESS_TOKEN);
    }

    // Проверка статуса ответа
    protected void checkResponseStatus(Response response, int expectedStatus) {
        response.then().statusCode(expectedStatus);
    }

    // Удаление созданного пользователя
    protected void deleteUserAfterTest(UserCredentials credentials, String accessToken) {
        if (accessToken != null) {
            ApiSteps.deleteUser(accessToken);
        }
    }
}