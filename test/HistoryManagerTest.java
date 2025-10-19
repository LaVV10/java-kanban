import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    @Test
    void testEmptyHistory() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void testAddAndRemove() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(60));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        assertEquals(2, taskManager.getHistory().size());

        taskManager.deleteTask(task1.getTaskId());
        assertEquals(1, taskManager.getHistory().size());
        assertFalse(taskManager.getHistory().contains(task1));
    }

    @Test
    void testDuplicatedTasks() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();
        Task task = new Task("Task", "Description", Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));

        manager.add(task);
        manager.add(task); // Дубликат

        assertEquals(1, manager.getHistory().size());
    }

    @Test
    void testRemoveFromMiddle() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(60));
        Task task3 = new Task("Task 3", "Description 3",
                Status.NEW, LocalDateTime.now().plusHours(2), Duration.ofMinutes(90));

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        taskManager.deleteTask(task2.getTaskId());

        assertEquals(2, taskManager.getHistory().size());
        assertEquals(task1, taskManager.getHistory().get(0));
        assertEquals(task3, taskManager.getHistory().get(1));
    }
}
