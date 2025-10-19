import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void testAddAndGetEpic() {
        Epic epic = new Epic("Test Epic", "Description");
        taskManager.addEpic(epic);

        Epic retrievedEpic = taskManager.getEpic(epic.getTaskId());
        assertNotNull(retrievedEpic);
        assertEquals(epic, retrievedEpic);
    }

    @Test
    void testAddAndGetSubTask() {
        Epic epic = new Epic("Test Epic", "Description");
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask("Test Subtask", "Description", Status.NEW, epic.getTaskId(), LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofMinutes(60));
        taskManager.addSubTask(subTask);

        SubTask retrievedSubTask = taskManager.getSubTask(subTask.getTaskId());
        assertNotNull(retrievedSubTask);
        assertEquals(subTask, retrievedSubTask);
    }
}
