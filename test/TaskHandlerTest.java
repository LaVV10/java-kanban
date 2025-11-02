import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TaskHandlerTest extends BaseHttpTest {

    private static final LocalDateTime BASE_TIME = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES);

    @Test
    void shouldAddTask() throws IOException, InterruptedException {
        Task task = new Task(
                "Add Task",
                "Description",
                Status.NEW,
                BASE_TIME.plusHours(1),
                Duration.ofMinutes(45)
        );

        Task saved = createTask(task);

        assertNotNull(saved.getTaskId(), "ID задачи должен быть присвоен");
        assertEquals("Add Task", saved.getTaskName());
        assertEquals(BASE_TIME.plusHours(1), saved.getStartTime());
        assertEquals(Duration.ofMinutes(45), saved.getDuration());
        assertEquals(BASE_TIME.plusHours(1).plusMinutes(45), saved.getEndTime());
    }

    @Test
    void shouldGetTaskById() throws IOException, InterruptedException {
        Task saved = createTask(new Task(
                "Get Task",
                "Description",
                Status.NEW,
                BASE_TIME.plusHours(2),
                Duration.ofMinutes(30)
        ));

        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks/" + saved.getTaskId()))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode(), "Ожидался статус 200 при получении задачи");

        Task retrieved = gson.fromJson(response.body(), Task.class);
        assertNotNull(retrieved, "Полученная задача не должна быть null");
        assertEquals(saved.getTaskId(), retrieved.getTaskId());
        assertEquals("Get Task", retrieved.getTaskName());
        assertEquals(BASE_TIME.plusHours(2), retrieved.getStartTime());
        assertEquals(Duration.ofMinutes(30), retrieved.getDuration());
    }

    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        Task original = new Task(
                "Original",
                "Desc",
                Status.NEW,
                BASE_TIME.plusHours(1),
                Duration.ofMinutes(30)
        );
        Task saved = createTask(original);

        Task updateData = new Task(
                "Updated Task",
                "Updated Desc",
                Status.DONE,
                BASE_TIME.plusHours(3),
                Duration.ofMinutes(90)
        );
        updateData.setTaskId(saved.getTaskId());

        // Отправляем на сервер → это вызовет update
        Task updated = createTask(updateData);

        assertEquals("Updated Task", updated.getTaskName());
        assertEquals("Updated Desc", updated.getTaskDescription());
        assertEquals(Status.DONE, updated.getTaskStatus());
        assertEquals(BASE_TIME.plusHours(3), updated.getStartTime());
        assertEquals(Duration.ofMinutes(90), updated.getDuration());
        assertEquals(BASE_TIME.plusHours(4).plusMinutes(30), updated.getEndTime());
    }

    @Test
    void shouldGetAllTasks() throws IOException, InterruptedException {
        createTask(new Task("Task 1", "Desc", Status.NEW,
                BASE_TIME.plusHours(1), Duration.ofMinutes(30)));
        createTask(new Task("Task 2", "Desc", Status.IN_PROGRESS,
                BASE_TIME.plusHours(2), Duration.ofMinutes(60)));

        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode(), "Ожидался статус 200 для GET /tasks");
        Task[] tasks = gson.fromJson(response.body(), Task[].class);

        assertNotNull(tasks, "Список задач не должен быть null");
        assertTrue(tasks.length >= 2, "Должно быть как минимум 2 задачи");

        // Проверим первую
        assertTrue(tasks[0].getTaskName().startsWith("Task"));
        assertNotNull(tasks[0].getStartTime());
        assertNotNull(tasks[0].getDuration());
    }

    @Test
    void shouldDeleteTask() throws IOException, InterruptedException {
        Task saved = createTask(new Task(
                "Delete Task",
                "Desc",
                Status.NEW,
                BASE_TIME.plusHours(5),
                Duration.ofMinutes(15)
        ));

        client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks/" + saved.getTaskId()))
                        .DELETE()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        HttpResponse<String> get = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks/" + saved.getTaskId()))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(404, get.statusCode(), "После удаления задача должна быть недоступна");
    }
}
