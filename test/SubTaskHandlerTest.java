import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SubTaskHandlerTest extends BaseHttpTest {

    private static final LocalDateTime BASE_TIME = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES);

    @Test
    void shouldAddSubTask() throws IOException, InterruptedException {
        // Создаём эпик
        Epic epic = new Epic("Epic for Sub", "Desc");
        Epic savedEpic = createEpic(epic);

        // Создаём подзадачу с конкретным временем
        SubTask subTask = new SubTask(
                "SubTask",
                "Desc",
                Status.NEW,
                savedEpic.getTaskId(),
                BASE_TIME.plusHours(1),     // startTime
                java.time.Duration.ofMinutes(60)  // duration
        );

        SubTask savedSubTask = createSubTask(subTask);

        assertNotNull(savedSubTask.getTaskId());
        assertEquals(savedEpic.getTaskId(), savedSubTask.getEpicId());
        assertEquals(BASE_TIME.plusHours(1), savedSubTask.getStartTime());
        assertEquals(java.time.Duration.ofMinutes(60), savedSubTask.getDuration());
        assertEquals(BASE_TIME.plusHours(2), savedSubTask.getEndTime());
    }

    @Test
    void shouldGetSubTaskById() throws IOException, InterruptedException {
        // Создаём эпик
        Epic epic = new Epic("Epic for Get", "Desc");
        Epic savedEpic = createEpic(epic);

        // Создаём подзадачу с временем
        SubTask subTask = new SubTask(
                "Get Sub",
                "Desc",
                Status.NEW,
                savedEpic.getTaskId(),
                BASE_TIME.plusHours(2),
                java.time.Duration.ofMinutes(30)
        );

        SubTask savedSubTask = createSubTask(subTask);

        // Получаем по ID
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/subtasks/" + savedSubTask.getTaskId()))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode());
        SubTask retrieved = gson.fromJson(response.body(), SubTask.class);

        assertEquals(savedSubTask.getTaskId(), retrieved.getTaskId());
        assertEquals(BASE_TIME.plusHours(2), retrieved.getStartTime());
        assertEquals(java.time.Duration.ofMinutes(30), retrieved.getDuration());
    }

    @Test
    void shouldDeleteSubTask() throws IOException, InterruptedException {
        // Создаём эпик
        Epic epic = new Epic("Epic for Del", "Desc");
        Epic savedEpic = createEpic(epic);

        // Создаём подзадачу с временем
        SubTask subTask = new SubTask(
                "Del Sub",
                "Desc",
                Status.NEW,
                savedEpic.getTaskId(),
                BASE_TIME.plusHours(3),
                java.time.Duration.ofMinutes(45)
        );

        SubTask savedSubTask = createSubTask(subTask);

        // Удаляем
        client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/subtasks/" + savedSubTask.getTaskId()))
                        .DELETE()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        // Проверяем, что не существует
        HttpResponse<String> get = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/subtasks/" + savedSubTask.getTaskId()))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(404, get.statusCode());
    }
}
