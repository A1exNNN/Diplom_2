package utils;


public class TestConstants {
    // Базовая ссылка на сайт (URL)
    public static final String BASE_URI = "https://stellarburgers.nomoreparties.site";

    // Константы для данных JSON объектов
    public static final String SUCCESS_FIELD = "success";
    public static final String MESSAGE_FIELD = "message";
    public static final String FIELD_USER_EMAIL = "user.email";
    public static final String FIELD_USER_NAME = "user.name";
    public static final String FIELD_ACCESS_TOKEN = "accessToken";
    public static final String FIELD_INGREDIENTS = "data._id";
    public static final String FIELD_ORDER_NUMBER = "order.number";
    public static final String FIELD_ORDERS = "orders";


    // Текст сообщений об ошибках
    public static final String MESSAGE_USER_EXISTS = "User already exists";
    public static final String MESSAGE_MISSING_FIELDS = "Email, password and name are required fields";
    public static final String MESSAGE_INVALID_CREDENTIALS = "email or password are incorrect";
    public static final String MESSAGE_UNAUTHORIZED = "You should be authorised"; // Добавляем это сообщение
    public static final String MESSAGE_NO_INGREDIENTS = "Ingredient ids must be provided";
    public static final String MESSAGE_INTERNAL_ERROR = "Internal Server Error";
    public static final String ORDERS_LIST_EMPTY_MESSAGE = "Orders list should not be empty";

    // Данные, которые отправляются или получаются, являются форматированными в формате JSON
    public static final String CONTENT_TYPE_JSON = "application/json";

    // Ручки
    public static final String ENDPOINT_REGISTER = "/api/auth/register";
    public static final String ENDPOINT_LOGIN = "/api/auth/login";
    public static final String ENDPOINT_USER = "/api/auth/user";
    public static final String ENDPOINT_INGREDIENTS = "/api/ingredients";
    public static final String ENDPOINT_ORDERS = "/api/orders";


    // Заголовки
    public static final String AUTHORIZATION_HEADER = "Authorization";


    // Данные для тестирования
    public static final String PASSWORD = "password";
    public static final String INVALID_PASSWORD = "wrong_password";
    public static final String TEST_USER_NAME = "UniqueUser";
    public static final String UPDATED_USER_NAME = "UpdatedUser";
    public static final String NON_EXISTENT_EMAIL = "nonexistent_user@test.com";

    // Время ожидания для HTTP-запросов
    public static final int CONNECTION_TIMEOUT = 5000; // Ожидание соединения в мс
    public static final int SOCKET_TIMEOUT = 5000;     //  Ожидание ответа в мс

    // HTTP параметры
    public static final String HTTP_CONNECTION_TIMEOUT = "http.connection.timeout";
    public static final String HTTP_SOCKET_TIMEOUT = "http.socket.timeout";


    // Для создания уникальных email адресов
    public static final String UNIQUE_EMAIL_PREFIX = "unique_user_";
    public static final String EMAIL_DOMAIN = "@test.com";
}