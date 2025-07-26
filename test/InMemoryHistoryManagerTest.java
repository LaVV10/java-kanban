import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;
    TaskManager taskManager = new InMemoryTaskManager();

    @BeforeEach
    public void setup() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void testAddSingleTask() {
        Task task = new Task("Task One", "sdelai1", Status.NEW);
        taskManager.addTask(task);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    public void testAddMultipleTasks() {
        Task task1 = new Task("Task One", "sdelai1", Status.NEW);
        Task task2 = new Task("Task Two", "sdelai2", Status.IN_PROGRESS);
        Task task3 = new Task("Task Three", "sdelai3", Status.NEW);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
        assertEquals(task3, history.get(2));
    }

    @Test
    public void testRemoveTask() {
        Task task1 = new Task("Task One", "sdelai1", Status.NEW);
        Task task2 = new Task("Task Two", "sdelai2", Status.NEW);
        Task task3 = new Task("Task three", "sdelai1", Status.IN_PROGRESS);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task3.getTaskId());// удаляем вторую задачу
        taskManager.deleteTask(task3.getTaskId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    public void testDuplicateTaskAddition() {
        Task task1 = new Task("Task One", "sdelai1", Status.NEW);
        Task task2 = new Task("Task two", "sdelai2", Status.NEW);
        Task task3 = new Task("Task 3", "sdelai3", Status.NEW);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task2); // повторное добавление задачи

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size()); // количество задач не увеличилось
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1)); // вторая задача стала третьей
        assertEquals(task2, history.get(2)); // третья задача обновилась
    }
}
