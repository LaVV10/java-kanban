import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicHandlerTest extends BaseHttpTest {

    @Test
    void shouldGetSubTasksByEpicId() throws IOException, InterruptedException {
        // Подготовка: фиксируем время для воспроизводимости
        LocalDateTime now = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MINUTES);

        // Создаём эпик и добавляем напрямую
        Epic epic = new Epic("Parent Epic", "Desc");
        taskManager.addEpic(epic);

        // Создаём подзадачи с временем
        SubTask sub1 = new SubTask(
                "Sub 1",
                "Desc",
                Status.NEW,
                epic.getTaskId(),
                now.plusHours(1),
                java.time.Duration.ofMinutes(30)
        );

        SubTask sub2 = new SubTask(
                "Sub 2",
                "Desc",
                Status.DONE,
                epic.getTaskId(),
                now.plusHours(2),
                java.time.Duration.ofMinutes(60)
        );

        taskManager.addSubTask(sub1);
        taskManager.addSubTask(sub2);

        // Выполняем GET /epics/{id}/subtasks
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/epics/" + epic.getTaskId() + "/subtasks"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        // Проверяем HTTP-статус
        assertEquals(200, response.statusCode(), "Ожидался статус 200");

        // Десериализуем ответ
        SubTask[] subTasks = gson.fromJson(response.body(), SubTask[].class);
        assertNotNull(subTasks, "Ответ с подзадачами не должен быть null");
        assertEquals(2, subTasks.length, "Ожидаются 2 подзадачи");

        // Проверяем поля первой подзадачи
        assertEquals("Sub 1", subTasks[0].getTaskName(), "Имя первой подзадачи");
        assertEquals(Status.NEW, subTasks[0].getTaskStatus(), "Статус первой подзадачи");
        assertEquals(now.plusHours(1), subTasks[0].getStartTime(), "Время начала первой подзадачи");
        assertEquals(java.time.Duration.ofMinutes(30), subTasks[0].getDuration(), "Длительность первой подзадачи");
        assertEquals(now.plusHours(1).plusMinutes(30), subTasks[0].getEndTime(), "Время окончания первой подзадачи");

        // Проверяем поля второй подзадачи
        assertEquals("Sub 2", subTasks[1].getTaskName(), "Имя второй подзадачи");
        assertEquals(Status.DONE, subTasks[1].getTaskStatus(), "Статус второй подзадачи");
        assertEquals(now.plusHours(2), subTasks[1].getStartTime(), "Время начала второй подзадачи");
        assertEquals(java.time.Duration.ofMinutes(60), subTasks[1].getDuration(), "Длительность второй подзадачи");
        assertEquals(now.plusHours(3), subTasks[1].getEndTime(), "Время окончания второй подзадачи");
    }

    @Test
    void shouldGetAllEpics() throws IOException, InterruptedException {
        // Подготовка: добавляем эпики напрямую
        Epic epic1 = new Epic("Epic 1", "Desc 1");
        Epic epic2 = new Epic("Epic 2", "Desc 2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        // Выполняем GET /epics
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/epics"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        // Проверяем ответ
        assertEquals(200, response.statusCode(), "Ожидался статус 200 для GET /epics");

        Epic[] epics = gson.fromJson(response.body(), Epic[].class);
        assertNotNull(epics, "Список эпиков не должен быть null");
        assertEquals(2, epics.length, "Должно быть 2 эпика");

        // Проверяем содержимое
        assertEquals("Epic 1", epics[0].getTaskName());
        assertEquals("Epic 2", epics[1].getTaskName());
    }

    @Test
    void shouldAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("New", "Desc");
        String json = gson.toJson(epic);

        // 1. Отправляем POST через HTTP
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/epics"))
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .header("Content-Type", "application/json")
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        // 2. Проверяем HTTP-ответ (формат, статус)
        assertEquals(200, response.statusCode());

        // 3. Проверяем, что задача на самом деле добавлена в менеджере
        List<Epic> allEpics = taskManager.getAllEpics();
        assertEquals(1, allEpics.size(), "Должен быть один эпик");

        Epic saved = allEpics.get(0);
        assertNotNull(saved.getTaskId(), "ID должен быть присвоен");
        assertEquals("New", saved.getTaskName());
        assertEquals("Desc", saved.getTaskDescription());
        assertEquals(Status.NEW, saved.getTaskStatus());
    }

    @Test
    void shouldGetEpicById() throws IOException, InterruptedException {
        // 1. Подготовка: добавляем напрямую
        Epic epic = new Epic("GetTest", "Desc");
        taskManager.addEpic(epic);

        // 2. Делаем GET
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/epics/" + epic.getTaskId()))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        // 3. Проверяем ответ
        assertEquals(200, response.statusCode());

        Epic retrieved = gson.fromJson(response.body(), Epic.class);
        assertEquals(epic.getTaskId(), retrieved.getTaskId());
        assertEquals("GetTest", retrieved.getTaskName());
        assertEquals("Desc", retrieved.getTaskDescription());
    }

    @Test
    void shouldDeleteEpic() throws IOException, InterruptedException {
        // 1. Подготовка: добавляем эпик напрямую
        Epic epic = new Epic("ToDelete", "Desc");
        taskManager.addEpic(epic);

        long epicId = epic.getTaskId();
        assertNotNull(epicId);

        // 2. Выполняем DELETE запрос
        client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/epics/" + epicId))
                        .DELETE()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        // 3. Проверяем: эпик удалён из менеджера
        Epic deleted = taskManager.getEpic(epicId);
        assertNull(deleted, "Эпик должен быть удалён");
    }
}
