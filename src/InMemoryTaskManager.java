import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    protected InMemoryHistoryManager historyManager;
    protected Map<Long, Task> tasks;
    protected Map<Long, SubTask> subTasks;
    protected Map<Long, Epic> epics;

    // Коллекция для хранения задач
    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = new InMemoryHistoryManager();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Метод для получения всех обычных задач
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Метод для получения всех эпиков
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    // получение списка подзадач определенного эпика
    @Override
    public List<SubTask> getEpicSubTasks(long epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            return epic.getSubTasks();
        }
        return new ArrayList<>(); // Если эпик не найден, возвращаем пустой список
    }

    // Удаление всех задач
    @Override
    public void clearAllTasks() {
        tasks.clear();
    }

    // Удаление всех эпиков
    @Override
    public void clearAllEpic() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void clearAllSubTasks() {
        for (Epic epic : getAllEpics()) {
            epic.clearSubTasks();
        }
        subTasks.clear();
    }

    // Получение задачи по идентификатору
    @Override
    public Task getTask(long id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpic(long id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public SubTask getSubTask(long id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            historyManager.add(subTask);
        }
        return subTask;
    }

    // Метод для создания задачи
    @Override
    public void addTask(Task task) {
        long id = Task.getNewId(); // Берём следующий свободный идентификатор
        task.setTaskId(id); // Присваиваем идентификатор задаче
        tasks.put(id, task); // Добавляем задачу в словарь
        historyManager.add(task);
    }

    @Override
    public void addEpic(Epic epic) {
        long id = Task.getNewId(); // Берём следующий свободный идентификатор
        epic.setTaskId(id); // Присваиваем идентификатор задаче
        epics.put(id, epic); // Добавляем задачу в словарь
        historyManager.add(epic);
    }

    @Override
    public void addSubTask(SubTask subTask) {
        long id = Task.getNewId();
        subTask.setTaskId(id);
        long epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);     // Находим эпик по идентификатору
        if (epic != null) {
            subTasks.put(id, subTask);
            epic.addSubTask(subTask); // Добавляем подзадачу в список подзадач эпика
            historyManager.add(subTask);
        } else {
            System.out.println("Эпик не найден");
        }
    }

    // Метод для обновления задачи
    @Override
    public void updateTask(Task updatedTask, long id) {

        // Проверяем, существует ли задача с таким идентификатором
        if (tasks.containsKey(id)) {
            updatedTask.setTaskId(id);
            tasks.put(id, updatedTask); // Обновляем задачу в словаре задач
        } else {
            System.out.println("Задача не найдена");
        }
    }

    @Override
    public void updateSubTask(SubTask updatedSubTask, long id) {
        if (subTasks.containsKey(id)) {
            SubTask oldSubTask = subTasks.get(id);
            if (oldSubTask != null) {
                Epic epic = epics.get(oldSubTask.getEpicId());
                epic.deleteSubTask(oldSubTask);
                subTasks.put(id, updatedSubTask); // Обновляем подзадачу
                epic.addSubTask(updatedSubTask);
            }
        } else {
            System.out.println("Подзадача не найдена");
        }
    }

    @Override
    public void updateEpic(Epic updatedEpic, long id) {

        if (epics.containsKey(id)) {
            Epic existingEpic = epics.get(id);
            // Обновляем только разрешенные поля: name и description
            existingEpic.setTaskName(updatedEpic.getTaskName());
            existingEpic.setTaskDescription(updatedEpic.getTaskDescription());
        } else {
            System.out.println("Эпик не найден");
        }
    }

    // Удаление подзадачи по идентификатору
    @Override
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
    @Override
    public void deleteTask(long id) {
        if (tasks.remove(id) == null) {
            System.out.println("Задача с указанным идентификатором не найдена");
        }
    }

    @Override
    public void deleteEpic(long id) {
        Epic epic = epics.remove(id);
        if (epic == null) {
            System.out.println("Задача с указанным идентификатором не найдена");
        } else {
            List<SubTask> tasksToDelete = epic.getSubTasks();
            for (SubTask task : tasksToDelete) {
                subTasks.remove(task.getTaskId()); // удаляем подзадачу по её идентификатору
            }
            epic.clearSubTasks();
            System.out.println("Эпик успешно удалён");
        }
    }
}
