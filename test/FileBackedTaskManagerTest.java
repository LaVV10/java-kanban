import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    @Test
    public void testSavingAndLoading() throws IOException {
        // Создаем временный файл
        File tempFile = Files.createTempFile("task-manager-test", ".csv").toFile();
        tempFile.deleteOnExit(); // Файл удалится после окончания теста

        // Создаем новый менеджер
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        // Добавляем задачи
        Task task1 = new Task(1L, "Task1", "Описание задачи 1", Status.NEW);
        Epic epic1 = new Epic(2L, "Epic1", "Описание эпика 1");
        SubTask subTask1 = new SubTask(3L, "SubTask1", "Описание подзадачи 1", Status.DONE, 2L);

        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubTask(subTask1);

        // Проверяем, что файл имеет положительный размер
        assertTrue(tempFile.length() > 0);

        // Загружаем данные снова
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем, что данные совпадают
        assertEquals(manager.getTask(1L), loadedManager.getTask(1L));
        assertEquals(manager.getEpic(2L), loadedManager.getEpic(2L));
        assertEquals(manager.getSubTask(3L), loadedManager.getSubTask(3L));
    }
}
