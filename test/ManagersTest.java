import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    public void testGetDefaultReturnsNonNull() {
        // Проверяем, что метод getDefault() возвращает ненулевое значение
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Менеджер задач не должен быть null");
    }

    @Test
    public void testGetDefaultReturnsProperlyInitializedManager() {
        // Проверяем, что менеджер задач корректно инициализирован
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager.getHistory(), "История просмотров должна быть проинициализирована");
    }

    @Test
    public void testTaskManagerMethodsWork() {
        // Проверяем, что методы менеджера задач работают корректно
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task(1, "Задача 1", "play", Status.IN_PROGRESS);
        taskManager.addTask(task);
        assertEquals(task, taskManager.getTask(1), "Задача должна быть добавлена и получена корректно");
    }

}