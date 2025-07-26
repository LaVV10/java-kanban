import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    //экземпляры класса Task равны друг другу, если равен их id;
    @Test
    public void testEqualTasks() {
        // Создаем две задачи с одинаковыми идентификаторами
        Task task1 = new Task("Задача 1", "play", Status.IN_PROGRESS);
        Task task2 = new Task(task1.getTaskId(), "Другое название", "stop play", Status.NEW);
        // Проверяем, что задачи равны
        assertEquals(task1, task2, "Задачи с одинаковыми идентификаторами должны быть равны");
    }

    @Test
    public void testEpicsAreEqualById() {
        Epic epic1 = new Epic("Epiczad1", "Sdelai toto");
        Epic epic2 = new Epic(epic1.getTaskId(), "Epiczad2", "Sdelai eto");
        assertEquals(epic1, epic2, "Эпики с одинаковыми идентификаторами должны быть равны");
    }

    @Test
    public void testSubtasksAreEqualById() {
        SubTask subtask1 = new SubTask("Podzad1", "Sdelai toto", Status.NEW, 4);
        SubTask subtask2 = new SubTask(subtask1.getTaskId(), "Podzad2", "Sdelai eto", Status.NEW, 4);
        assertEquals(subtask1, subtask2, "Подзадачи с одинаковыми идентификаторами должны быть равны");
    }

    /*@Test
    public void testCannotAddSelfAsSubtask() {
        // Создаем экземпляр эпика
        Epic epic = new Epic("Epiczad1", "Sdelai toto");
        // Проверяем, что попытка добавить эпик в самого себя приводит к исключению
        assertThrows(IllegalArgumentException.class, () -> {
            epic.addSubTask((SubTask) epic); // приведение к Subtask для демонстрации
        }, "Эпик не должен быть добавлен в самого себя в виде подзадачи");
    }*/

}