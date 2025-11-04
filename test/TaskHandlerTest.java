import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

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

        // Отправляем POST /tasks → тестируем эндпоинт
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks"))
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                        .header("Content-Type", "application/json")
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        // Проверяем ответ
        assertEquals(200, response.statusCode(), "Ожидался статус 200 при добавлении");

        // Проверяем состояние через менеджер
        List<Task> allTasks = taskManager.getAllTasks();
        assertEquals(1, allTasks.size(), "Должна быть одна задача");

        Task saved = allTasks.get(0);
        assertNotNull(saved.getTaskId(), "ID должен быть присвоен");
        assertEquals("Add Task", saved.getTaskName());
        assertEquals(BASE_TIME.plusHours(1), saved.getStartTime());
        assertEquals(Duration.ofMinutes(45), saved.getDuration());
        assertEquals(BASE_TIME.plusHours(1).plusMinutes(45), saved.getEndTime());
    }

    @Test
    void shouldGetTaskById() throws IOException, InterruptedException {
        // Подготовка: добавляем задачу напрямую
        Task task = new Task(
                "Get Task",
                "Description",
                Status.NEW,
                BASE_TIME.plusHours(2),
                Duration.ofMinutes(30)
        );
        taskManager.addTask(task);

        // Выполняем GET /tasks/{id}
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks/" + task.getTaskId()))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        // Проверяем ответ
        assertEquals(200, response.statusCode());
        Task retrieved = gson.fromJson(response.body(), Task.class);

        // Проверяем поля
        assertEquals(task.getTaskId(), retrieved.getTaskId());
        assertEquals("Get Task", retrieved.getTaskName());
        assertEquals(BASE_TIME.plusHours(2), retrieved.getStartTime());
        assertEquals(Duration.ofMinutes(30), retrieved.getDuration());
    }


    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        // 1. Подготовка: добавляем задачу напрямую
        Task task = new Task("Original", "Desc", Status.NEW,
                BASE_TIME.plusHours(1), Duration.ofMinutes(30));
        taskManager.addTask(task);

        // 2. Формируем обновлённую версию
        Task updatedData = new Task("Updated", "New Desc", Status.DONE,
                BASE_TIME.plusHours(2), Duration.ofMinutes(60));
        updatedData.setTaskId(task.getTaskId());

        // 3. Отправляем POST → это upsert
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks"))
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(updatedData)))
                        .header("Content-Type", "application/json")
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode());

        // 4. Проверяем через менеджер
        Task saved = taskManager.getTask(task.getTaskId());
        assertNotNull(saved);
        assertEquals("Updated", saved.getTaskName());
        assertEquals("New Desc", saved.getTaskDescription());
        assertEquals(Status.DONE, saved.getTaskStatus());
        assertEquals(BASE_TIME.plusHours(2), saved.getStartTime());
        assertEquals(Duration.ofMinutes(60), saved.getDuration());
    }

    @Test
    void shouldGetAllTasks() throws IOException, InterruptedException {
        // Подготовка: добавляем напрямую
        Task task1 = new Task("Task 1", "Desc", Status.NEW,
                BASE_TIME.plusHours(1), Duration.ofMinutes(30));
        Task task2 = new Task("Task 2", "Desc", Status.IN_PROGRESS,
                BASE_TIME.plusHours(2), Duration.ofMinutes(60));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        // Выполняем GET /tasks
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode(), "Ожидался статус 200");

        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertNotNull(tasks);
        assertEquals(2, tasks.length, "Должно быть 2 задачи");

        // Проверяем содержимое
        assertEquals("Task 1", tasks[0].getTaskName());
        assertEquals("Task 2", tasks[1].getTaskName());
        assertNotNull(tasks[0].getStartTime());
        assertNotNull(tasks[0].getDuration());
    }

    @Test
    void shouldDeleteTask() throws IOException, InterruptedException {
        // Подготовка: добавляем напрямую
        Task task = new Task(
                "Delete Task",
                "Desc",
                Status.NEW,
                BASE_TIME.plusHours(5),
                Duration.ofMinutes(15)
        );
        taskManager.addTask(task);

        // Выполняем DELETE /tasks/{id}
        client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks/" + task.getTaskId()))
                        .DELETE()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        // Проверяем: задача удалена — через менеджер
        Task deleted = taskManager.getTask(task.getTaskId());
        assertNull(deleted, "Задача должна быть удалена");
    }
}
