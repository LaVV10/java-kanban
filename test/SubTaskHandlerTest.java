import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SubTaskHandlerTest extends BaseHttpTest {

    private static final LocalDateTime BASE_TIME = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES);

    @Test
    void shouldUpdateSubTaskWhenIdProvided() throws IOException, InterruptedException {
        // Подготовка: добавляем эпик и подзадачу
        Epic epic = new Epic("Epic", "Desc");
        taskManager.addEpic(epic);

        SubTask original = new SubTask(
                "Original",
                "Desc",
                Status.NEW,
                epic.getTaskId(),
                BASE_TIME.plusHours(1),
                java.time.Duration.ofMinutes(30)
        );
        taskManager.addSubTask(original);

        // Создаём обновлённую версию с тем же ID
        SubTask updatedData = new SubTask(
                "Updated Sub",
                "New Desc",
                Status.DONE,
                epic.getTaskId(), // тот же эпик
                BASE_TIME.plusHours(3),
                java.time.Duration.ofMinutes(90)
        );
        updatedData.setTaskId(original.getTaskId()); // устанавливаем ID → это update

        // Отправляем POST /subtasks → должен обновить
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/subtasks"))
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(updatedData)))
                        .header("Content-Type", "application/json")
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        // Проверяем ответ
        assertEquals(200, response.statusCode(), "Ожидался статус 200 при обновлении");

        // Проверяем состояние через менеджер
        SubTask saved = taskManager.getSubTask(original.getTaskId());
        assertNotNull(saved, "Подзадача должна существовать после обновления");

        assertEquals("Updated Sub", saved.getTaskName());
        assertEquals("New Desc", saved.getTaskDescription());
        assertEquals(Status.DONE, saved.getTaskStatus());
        assertEquals(BASE_TIME.plusHours(3), saved.getStartTime());
        assertEquals(java.time.Duration.ofMinutes(90), saved.getDuration());
        assertEquals(BASE_TIME.plusHours(4).plusMinutes(30), saved.getEndTime());
    }

    @Test
    void shouldGetAllSubTasks() throws IOException, InterruptedException {
        // Подготовка: добавляем эпик и подзадачи напрямую
        Epic epic = new Epic("Parent Epic", "Desc");
        taskManager.addEpic(epic);

        SubTask sub1 = new SubTask(
                "Sub 1",
                "Desc",
                Status.NEW,
                epic.getTaskId(),
                BASE_TIME.plusHours(1),
                java.time.Duration.ofMinutes(30)
        );

        SubTask sub2 = new SubTask(
                "Sub 2",
                "Desc",
                Status.DONE,
                epic.getTaskId(),
                BASE_TIME.plusHours(2),
                java.time.Duration.ofMinutes(60)
        );

        taskManager.addSubTask(sub1);
        taskManager.addSubTask(sub2);

        // Делаем GET /subtasks
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/subtasks"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        // Проверяем
        assertEquals(200, response.statusCode(), "Ожидался статус 200 для GET /subtasks");

        SubTask[] subTasks = gson.fromJson(response.body(), SubTask[].class);
        assertNotNull(subTasks);
        assertEquals(2, subTasks.length, "Должно быть 2 подзадачи");

        // Проверяем данные
        assertEquals("Sub 1", subTasks[0].getTaskName());
        assertEquals(BASE_TIME.plusHours(1), subTasks[0].getStartTime());
        assertEquals(java.time.Duration.ofMinutes(30), subTasks[0].getDuration());

        assertEquals("Sub 2", subTasks[1].getTaskName());
        assertEquals(BASE_TIME.plusHours(2), subTasks[1].getStartTime());
        assertEquals(java.time.Duration.ofMinutes(60), subTasks[1].getDuration());
    }

    @Test
    void shouldAddSubTask() throws IOException, InterruptedException {
        // 1. Подготовка: создаём эпик напрямую
        Epic epic = new Epic("Epic for Sub", "Desc");
        taskManager.addEpic(epic);

        // 2. Создаём подзадачу для отправки
        SubTask subTask = new SubTask(
                "SubTask",
                "Desc",
                Status.NEW,
                epic.getTaskId(),
                BASE_TIME.plusHours(1),
                java.time.Duration.ofMinutes(60)
        );

        // 3. Отправляем POST /subtasks → тестируем эндпоинт
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/subtasks"))
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                        .header("Content-Type", "application/json")
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        // 4. Проверяем HTTP-ответ
        assertEquals(200, response.statusCode(), "Ожидался статус 200 при добавлении подзадачи");

        // 5. Проверяем, что задача на самом деле добавлена в менеджере
        List<SubTask> allSubTasks = taskManager.getAllSubTasks();
        assertEquals(1, allSubTasks.size(), "Должна быть одна подзадача");

        SubTask saved = allSubTasks.get(0);
        assertNotNull(saved.getTaskId(), "ID должен быть присвоен");
        assertEquals("SubTask", saved.getTaskName());
        assertEquals("Desc", saved.getTaskDescription());
        assertEquals(Status.NEW, saved.getTaskStatus());
        assertEquals(epic.getTaskId(), saved.getEpicId());
        assertEquals(BASE_TIME.plusHours(1), saved.getStartTime());
        assertEquals(java.time.Duration.ofMinutes(60), saved.getDuration());
        assertEquals(BASE_TIME.plusHours(2), saved.getEndTime());
    }

    @Test
    void shouldGetSubTaskById() throws IOException, InterruptedException {
        // 1. Подготовка: добавляем эпик и подзадачу напрямую
        Epic epic = new Epic("Epic", "Desc");
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask(
                "Get Sub",
                "Desc",
                Status.NEW,
                epic.getTaskId(),
                BASE_TIME.plusHours(2),
                java.time.Duration.ofMinutes(30)
        );
        taskManager.addSubTask(subTask);

        // 2. Делаем GET /subtasks/{id}
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/subtasks/" + subTask.getTaskId()))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        // 3. Проверяем ответ
        assertEquals(200, response.statusCode());
        SubTask retrieved = gson.fromJson(response.body(), SubTask.class);

        // 4. Проверяем поля
        assertEquals(subTask.getTaskId(), retrieved.getTaskId());
        assertEquals("Get Sub", retrieved.getTaskName());
        assertEquals(BASE_TIME.plusHours(2), retrieved.getStartTime());
        assertEquals(java.time.Duration.ofMinutes(30), retrieved.getDuration());
    }

    @Test
    void shouldDeleteSubTask() throws IOException, InterruptedException {
        // 1. Подготовка: добавляем эпик и подзадачу напрямую
        Epic epic = new Epic("Epic", "Desc");
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask(
                "Del Sub",
                "Desc",
                Status.NEW,
                epic.getTaskId(),
                BASE_TIME.plusHours(3),
                java.time.Duration.ofMinutes(45)
        );
        taskManager.addSubTask(subTask);

        // 2. Удаляем через DELETE
        client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/subtasks/" + subTask.getTaskId()))
                        .DELETE()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        // 3. Проверяем, что подзадача удалена — через менеджер
        SubTask deleted = taskManager.getSubTask(subTask.getTaskId());
        assertNull(deleted, "Подзадача должна быть удалена");

        // 4. Проверяем, что подзадача исчезла из эпика
        Epic updatedEpic = taskManager.getEpic(epic.getTaskId());
        assertTrue(updatedEpic.getSubTasks().stream()
                        .noneMatch(st -> st.getTaskId() == subTask.getTaskId()),
                "Подзадача не должна быть в эпике");
    }
}