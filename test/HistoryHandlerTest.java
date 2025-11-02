import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryHandlerTest extends BaseHttpTest {

    private static final LocalDateTime BASE_TIME = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES);

    @Test
    void shouldReturnHistory() throws IOException, InterruptedException {
        // Создаём задачу с конкретным временем
        Task task = createTask(new Task(
                "Task with Time",
                "Desc",
                Status.NEW,
                BASE_TIME.plusHours(1),
                Duration.ofMinutes(60)
        ));

        // Получаем задачу → добавляем в историю
        getTaskById(task.getTaskId());

        // Запрашиваем историю
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/history"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode(), "Ожидается статус 200 для /history");

        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertNotNull(history, "История не должна быть null");
        assertTrue(history.length >= 1, "История должна содержать хотя бы одну задачу");

        Task retrieved = history[0];
        assertEquals(task.getTaskId(), retrieved.getTaskId());
        assertEquals("Task with Time", retrieved.getTaskName());
        assertEquals(BASE_TIME.plusHours(1), retrieved.getStartTime());
        assertEquals(Duration.ofMinutes(60), retrieved.getDuration());
        assertEquals(BASE_TIME.plusHours(2), retrieved.getEndTime());
    }
}


