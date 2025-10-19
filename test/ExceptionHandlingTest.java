import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExceptionHandlingTest {

    @Test
    void testOverlapExceptionOnAdd() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Task 1", "Description 1", Status.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofMinutes(60));

        manager.addTask(task1);

        Task task2 = new Task("Task 2", "Description 2", Status.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 30), Duration.ofMinutes(60));

        assertThrows(ManagerLoadException.class, () -> {
            manager.addTask(task2);
        }, "Должно быть выброшено исключение при добавлении пересекающихся задач");
    }

    @Test
    void testNoExceptionOnNonOverlap() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task1 = new Task("Task 1", "Description 1", Status.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofMinutes(30));
        Task task2 = new Task("Task 2", "Description 2", Status.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 30), Duration.ofMinutes(30));

        assertDoesNotThrow(() -> {
            manager.addTask(task1);
            manager.addTask(task2);
        });
    }
}
