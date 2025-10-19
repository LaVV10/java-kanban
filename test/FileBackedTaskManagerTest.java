import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private static final String FILE_NAME = "task-manager-test.csv";

    @Override
    protected FileBackedTaskManager createTaskManager() {
        File file;
        try {
            file = new File(FILE_NAME);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать файл для тестирования", e);
        }
        return new FileBackedTaskManager(file);
    }

    @Test
    void testSavingAndLoading() throws IOException {
        // Создаем менеджер и добавляем задачу
        Task task = new Task("Test Task", "Description", Status.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofMinutes(60));
        taskManager.addTask(task);

        // Загружаем из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(new File(FILE_NAME));

        // Проверяем, что количество задач совпадает
        assertEquals(taskManager.getAllTasks().size(), loadedManager.getAllTasks().size());

        // Проверяем содержимое первой задачи
        Task originalTask = taskManager.getAllTasks().get(0);
        Task loadedTask = loadedManager.getTask(originalTask.getTaskId());

        assertNotNull(loadedTask);
        assertEquals(originalTask.getTaskId(), loadedTask.getTaskId());
        assertEquals(originalTask.getTaskName(), loadedTask.getTaskName());
        assertEquals(originalTask.getTaskStatus(), loadedTask.getTaskStatus());
        assertEquals(originalTask.getTaskDescription(), loadedTask.getTaskDescription());
        assertEquals(originalTask.getStartTime(), loadedTask.getStartTime());
        assertEquals(originalTask.getDuration(), loadedTask.getDuration());
    }
}

