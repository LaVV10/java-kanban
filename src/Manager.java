import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Manager {

    private Map<Long, Task> tasks;
    private Map<Long, SubTask> subTasks;
    private Map<Long, Epic> epics;

    // Коллекция для хранения задач
    public Manager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
    }

    // Метод для получения всех обычных задач
    public Map<Long, Task> getAllRegularTasks() {
        return tasks;
    }

    // Метод для получения всех эпиков
    public Map<Long, Epic> getAllEpics() {
        return epics;
    }

    public Map<Long, SubTask> getAllSubTasks() {
        return subTasks;
    }

    // получение списка подзадач определенного эпика
    public List<SubTask> getEpicSubTasks(long epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            return epic.getSubTasks();
        }
        return new ArrayList<>(); // Если эпик не найден, возвращаем пустой список
    }

    // Удаление всех задач
    public void clearAllTasks() {
            tasks.clear();
    }

    public void clearAllEpic() {
        epics.clear();
    }

    public void clearAllSubTasks() {
        subTasks.clear();
    }

    // удаление всех подзадач эпика
    public void clearSubTasksOfEpic(long epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.clearSubTasks(); // Вызываем метод очистки подзадач у эпика
        }
    }

    // Получение задачи по идентификатору
    public Task getTask(long id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        return null;
    }

    public Epic getEpic(long id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return null;
    }

    public SubTask getSubTask(long id) {
        if (subTasks.containsKey(id)) {
            return subTasks.get(id);
        }
        return null;
    }

    // Метод для создания задачи
    public void addTask(Task task) {
        long id = TaskId.getNewId(); // Берём следующий свободный идентификатор
        task.setTaskId(id); // Присваиваем идентификатор задаче
        tasks.put(id, task); // Добавляем задачу в словарь
    }
    public void addEpic(Epic epic) {
        long id = TaskId.getNewId(); // Берём следующий свободный идентификатор
        epic.setTaskId(id); // Присваиваем идентификатор задаче
        epics.put(id, epic); // Добавляем задачу в словарь
    }

    public void addSubTask(SubTask subTask) {
        long epicId = subTask.getEpicId(); // Получаем идентификатор эпика
        Epic epic = epics.get(epicId);     // Находим эпик по идентификатору

        if (epic != null) {
            subTasks.put(subTask.getTaskId(), subTask); // Добавляем подзадачу в словарь подзадач
            epic.addSubTask(subTask);                   // Добавляем подзадачу в список подзадач эпика
        } else {
            System.out.println("Эпик с указанным идентификатором не найден");
        }
    }

    // Метод для обновления задачи
    public void updateTask(Task updatedTask) {
        long taskId = updatedTask.getTaskId(); // Получаем идентификатор задачи

        // Проверяем, существует ли задача с таким идентификатором
        if (tasks.containsKey(taskId)) {
            tasks.put(taskId, updatedTask); // Обновляем задачу в словаре задач
        } else {
            System.out.println("Задача с указанным идентификатором не найдена");
        }
    }

    public void updateSubTask(SubTask updatedSubTask) {
        long subTaskId = updatedSubTask.getTaskId();
        SubTask oldSubTask = subTasks.get(subTaskId);
        if (oldSubTask != null) {
            subTasks.put(subTaskId, updatedSubTask); // Обновляем подзадачу
            Epic epic = epics.get(oldSubTask.getEpicId()); // Находим эпик
            if (epic != null) {
                epic.checkEpicStatus(); // Пересчитываем статус эпика
            }
        }
    }

    public void updateEpic(Epic updatedTask) {
        long taskId = updatedTask.getTaskId();
        if (epics.containsKey(taskId)) {
            epics.put(taskId, updatedTask);
        } else {
            System.out.println("Задача с указанным идентификатором не найдена");
        }
    }

    // Удаление подзадачи по идентификатору
    public void removeSubTaskFromEpic(long epicId, SubTask subTask) {
        Epic epic = epics.get(epicId); // Находим эпик по идентификатору
        if (epic != null) {
            epic.deleteSubTask(subTask); // Удаляем подзадачу из эпика
        }
    }
    // Удаление задачи по идентификатору
    public void deleteTask(long id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Задача с указанным идентификатором не найдена");
        }
    }

    public void deleteEpic(long id) {
        if (epics.containsKey(id)) {
            epics.remove(id);
        } else {
            System.out.println("Задача с указанным идентификатором не найдена");
        }
    }
}
