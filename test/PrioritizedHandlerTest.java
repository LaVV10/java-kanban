import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PrioritizedHandlerTest extends BaseHttpTest {

    private static final LocalDateTime BASE_TIME = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES);

    @Test
    void shouldReturnPrioritizedTasksSortedByStartTime() throws IOException, InterruptedException {
        // Создаём задачи с фиксированным временем
        Task t1 = new Task("Early Task", "Desc", Status.NEW,
                BASE_TIME.plusHours(1), Duration.ofMinutes(30));

        Task t2 = new Task("Late Task", "Desc", Status.NEW,
                BASE_TIME.plusHours(3), Duration.ofMinutes(60));

        Task t3 = new Task("No Time Task", "Desc", Status.NEW,
                null, null); // Не должна попасть в prioritized

        // Отправляем
        createTask(t1);
        createTask(t2);
        createTask(t3); // Эта задача не имеет startTime → не попадёт в prioritized

        // Получаем приоритетный список
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/prioritized"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode(), "Ожидается статус 200 для /prioritized");

        Task[] prioritized = gson.fromJson(response.body(), Task[].class);
        assertNotNull(prioritized, "Список приоритетных задач не должен быть null");
        assertEquals(2, prioritized.length, "Ожидаются только задачи с startTime");

        // Проверяем порядок: t1 (раньше) → t2 (позже)
        assertTrue(
                prioritized[0].getStartTime().isBefore(prioritized[1].getStartTime()),
                "Задачи должны быть отсортированы по возрастанию startTime"
        );

        // Проверяем имена
        assertEquals("Early Task", prioritized[0].getTaskName(), "Первая задача — самая ранняя");
        assertEquals("Late Task", prioritized[1].getTaskName(), "Вторая задача — позже");
    }

    @Test
    void shouldReturnEmptyArrayIfNoTasksWithStartTime() throws IOException, InterruptedException {
        Task t1 = new Task("No Time 1", "Desc", Status.NEW, null, null);
        Task t2 = new Task("No Time 2", "Desc", Status.NEW, null, null);
        createTask(t1);
        createTask(t2);

        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/prioritized"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode());
        Task[] prioritized = gson.fromJson(response.body(), Task[].class);
        assertEquals(0, prioritized.length, "Если нет задач со startTime — массив должен быть пустым");
    }
}