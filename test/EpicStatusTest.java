import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicStatusTest {

    @Test
    void testEpicStatusAllNew() {

        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Test Epic", "Description");
        taskManager.addEpic(epic);
        SubTask subTask1 = new SubTask("Subtask 1", "Description 1", Status.NEW, epic.getTaskId(),
                LocalDateTime.now(), Duration.ofMinutes(30));
        SubTask subTask2 = new SubTask("Subtask 2", "Description 2", Status.NEW, epic.getTaskId(),
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(60));

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        assertEquals(Status.NEW, epic.getTaskStatus());
    }

    @Test
    void testEpicStatusAllDone() {

        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Test Epic", "Description");
        taskManager.addEpic(epic);
        SubTask subTask1 = new SubTask("Subtask 1", "Description 1", Status.DONE, 1,
                LocalDateTime.now(), Duration.ofMinutes(30));
        SubTask subTask2 = new SubTask("Subtask 2", "Description 2", Status.DONE, 1,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(60));

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        assertEquals(Status.DONE, epic.getTaskStatus());
    }

    @Test
    void testEpicStatusMixed() {

        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Test Epic", "Description");
        taskManager.addEpic(epic);
        SubTask subTask1 = new SubTask("Subtask 1", "Description 1", Status.NEW, 1,
                LocalDateTime.now(), Duration.ofMinutes(30));
        SubTask subTask2 = new SubTask("Subtask 2", "Description 2", Status.DONE, 1,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(60));

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        assertEquals(Status.IN_PROGRESS, epic.getTaskStatus());
    }

    @Test
    void testEpicStatusInProgress() {

        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Test Epic", "Description");
        taskManager.addEpic(epic);
        SubTask subTask1 = new SubTask("Subtask 1", "Description 1", Status.IN_PROGRESS, 1,
                LocalDateTime.now(), Duration.ofMinutes(30));
        SubTask subTask2 = new SubTask("Subtask 2", "Description 2", Status.NEW, 1,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(60));

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        assertEquals(Status.IN_PROGRESS, epic.getTaskStatus());
    }
}
