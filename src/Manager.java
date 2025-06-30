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
    public List<Task> getAllRegularTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Метод для получения всех эпиков
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
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

    // Удаление всех эпиков
    public void clearAllEpic() {
        epics.clear();
        subTasks.clear();
    }

    public void clearAllSubTasks() {
        for (Epic epic : getAllEpics()) {
            epic.clearSubTasks();
        }
        subTasks.clear();
    }

    // Получение задачи по идентификатору
    public Task getTask(long id) {
            return tasks.get(id);
    }

    public Epic getEpic(long id) {
            return epics.get(id);
    }

    public SubTask getSubTask(long id) {
            return subTasks.get(id);
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
        long id = TaskId.getNewId();
        subTask.setTaskId(id);
        long epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);     // Находим эпик по идентификатору
        if (epic != null) {
            subTasks.put(id, subTask);
            epic.addSubTask(subTask); // Добавляем подзадачу в список подзадач эпика
        } else {
            System.out.println("Эпик не найден");
        }
    }

    // Метод для обновления задачи
    public void updateTask(Task updatedTask) {
        long taskId = updatedTask.getTaskId(); // Получаем идентификатор задачи

        // Проверяем, существует ли задача с таким идентификатором
        if (tasks.containsKey(taskId)) {
            tasks.put(taskId, updatedTask); // Обновляем задачу в словаре задач
        } else {
            System.out.println("Задача не найдена");
        }
    }

    public void updateSubTask(SubTask updatedSubTask) {
        long subTaskId = updatedSubTask.getTaskId();
        if (subTasks.containsKey(subTaskId)) {
            SubTask oldSubTask = subTasks.get(subTaskId);
            if (oldSubTask != null) {
                Epic epic = epics.get(oldSubTask.getEpicId());
                epic.deleteSubTask(oldSubTask);
                subTasks.put(subTaskId, updatedSubTask); // Обновляем подзадачу
                epic.addSubTask(updatedSubTask);
            }
        } else {
            System.out.println("Подзадача не найдена");
        }
    }

    public void updateEpic(Epic updatedEpic) {
        long taskEpicId = updatedEpic.getTaskId();
        if (epics.containsKey(taskEpicId)) {
            Epic existingEpic = epics.get(taskEpicId);
            // Обновляем только разрешенные поля: name и description
            existingEpic.setTaskName(updatedEpic.getTaskName());
            existingEpic.setTaskDescription(updatedEpic.getTaskDescription());
        } else {
            System.out.println("Эпик не найден");
        }
    }

    // Удаление подзадачи по идентификатору
    public void removeSubTask(long id) {
        SubTask subTask = subTasks.remove(id);
        if (subTask == null) {
            System.out.println("Задача с указанным идентификатором не найдена");
        } else {
            long epicId = subTask.getEpicId();
            Epic epic = epics.get(epicId);
            if (epic != null) {
                epic.deleteSubTask(subTask); // Удаляем подзадачу из списка подзадач эпика
            } else {
                System.out.println("Эпик не найден");
            }
        }
    }

    // Удаление задачи по идентификатору
    public void deleteTask(long id) {
        if (tasks.remove(id) == null) {
            System.out.println("Задача с указанным идентификатором не найдена");
        }
    }

    public void deleteEpic(long id) {
        if (epics.remove(id) == null) {
            System.out.println("Задача с указанным идентификатором не найдена");
        }
    }
}
