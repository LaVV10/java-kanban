import org.junit.jupiter.api.BeforeEach;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    protected abstract T createTaskManager();

    // Тесты общих методов TaskManager

    @org.junit.jupiter.api.Test
    void testAddAndGetTask() {
        Task task = new Task("Test Task", "Description", Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));
        taskManager.addTask(task);

        Task retrievedTask = taskManager.getTask(task.getTaskId());
        assertNotNull(retrievedTask);
        assertEquals(task, retrievedTask);
    }

    @org.junit.jupiter.api.Test
    void testUpdateTask() {
        Task task = new Task("Test Task", "Description", Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));
        taskManager.addTask(task);

        Task updatedTask = new Task("Updated Task", "New Description", Status.DONE,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(60));
        updatedTask.setTaskId(task.getTaskId());
        taskManager.updateTask(updatedTask, task.getTaskId());

        Task retrievedTask = taskManager.getTask(task.getTaskId());
        assertEquals(updatedTask, retrievedTask);
    }

    @org.junit.jupiter.api.Test
    void testDeleteTask() {
        Task task = new Task("Test Task", "Description", Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));
        taskManager.addTask(task);

        taskManager.deleteTask(task.getTaskId());
        assertNull(taskManager.getTask(task.getTaskId()));
    }

    @org.junit.jupiter.api.Test
    void testHistory() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));
        Task task2 = new Task("Task 2", "Description 2", Status.DONE,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(60));

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getTask(task1.getTaskId());
        taskManager.getTask(task2.getTaskId());

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }
    @org.junit.jupiter.api.Test
    void testGetAllTasks() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));
        Task task2 = new Task("Task 2", "Description 2", Status.DONE,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(60));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        List<Task> allTasks = taskManager.getAllTasks();
        assertEquals(2, allTasks.size());
        assertTrue(allTasks.contains(task1));
        assertTrue(allTasks.contains(task2));
    }

    @org.junit.jupiter.api.Test
    void testClearAllTasks() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));
        Task task2 = new Task("Task 2", "Description 2", Status.DONE,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(60));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.clearAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty());
    }
}
