import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    public void testHistoryPreservesPreviousVersions() {

        HistoryManager historyManager = new InMemoryHistoryManager();

        TaskManager taskManager = new InMemoryTaskManager();

        // Создаем задачу
        Task task = new Task("Task1", "Sdelai2", Status.NEW);

        taskManager.addTask(task);

        long taskNewId = task.getTaskId();
        // Добавляем задачу в менеджер
        historyManager.add(taskManager.getTask(taskNewId));

        Task task2 = new Task("Task1", "Sdelai3", Status.IN_PROGRESS);

        // Обновляем задачу и снова добавляем её в менеджер
        taskManager.updateTask(task2, taskNewId);

        historyManager.add(taskManager.getTask(taskNewId));

        // Проверяем, что сохранились обе версии задачи
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Должны быть две версии задачи");
    }
}