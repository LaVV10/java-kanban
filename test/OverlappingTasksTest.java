import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OverlappingTasksTest {

    @Test
    void testNonOverlappingTasks() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofMinutes(30));
        Task task2 = new Task("Task 2", "Description 2", Status.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 30), Duration.ofMinutes(30));

        assertFalse(Task.isOverlapping(task1, task2));
    }

    @Test
    void testOverlappingTasks() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofMinutes(60));
        Task task2 = new Task("Task 2", "Description 2", Status.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 30), Duration.ofMinutes(60));

        assertTrue(Task.isOverlapping(task1, task2));
    }

    @Test
    void testNestedTasks() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofMinutes(120));
        Task task2 = new Task("Task 2", "Description 2", Status.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 30), Duration.ofMinutes(30));

        assertTrue(Task.isOverlapping(task1, task2));
    }

    @Test
    void testOneTaskInsideAnother() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofMinutes(120));
        Task task2 = new Task("Task 2", "Description 2", Status.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 30), Duration.ofMinutes(30));

        assertTrue(Task.isOverlapping(task1, task2));
    }
}
