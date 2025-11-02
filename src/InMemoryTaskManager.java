import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected InMemoryHistoryManager historyManager;
    protected Map<Long, Task> tasks;
    protected Map<Long, SubTask> subTasks;
    protected Map<Long, Epic> epics;
    private final Set<Task> prioritizedTasks = new TreeSet<>();

    // Коллекция для хранения задач
    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = new InMemoryHistoryManager();
    }

    /**
     * Проверяет, пересекается ли новая задача с любой из существующих.
     */
    @Override
    public boolean hasOverlapWithExisting(Task newTask) {
        List<Task> prioritized = getPrioritizedTasks();
        return prioritized.stream()
                .anyMatch(task -> Task.isOverlapping(newTask, task));
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    // Метод для добавления задачи в TreeSet, если startTime задан
    protected void addPrioritizedTask(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    // Метод для удаления задачи из TreeSet
    protected void removePrioritizedTask(Task task) {
        prioritizedTasks.remove(task);
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
        if (hasOverlapWithExisting(task)) {
            throw new ManagerLoadException("Нельзя добавить задачу — она пересекается с другой");
        }

        long id = Task.getNewId();
        task.setTaskId(id);
        tasks.put(id, task);
        historyManager.add(task);
        addPrioritizedTask(task); // Добавляем в TreeSet, если startTime задан
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
        if (hasOverlapWithExisting(subTask)) {
            throw new ManagerLoadException("Нельзя добавить подзадачу — она пересекается с другой");
        }

        long id = Task.getNewId();
        subTask.setTaskId(id);
        long epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            subTasks.put(id, subTask);
            epic.addSubTask(subTask);
            addPrioritizedTask(subTask);
            historyManager.add(subTask);
        } else {
            System.out.println("Эпик не найден");
        }
    }

    // Метод для обновления задачи
    @Override
    public void updateTask(Task updatedTask, long id) {
        if (tasks.containsKey(id)) {
            Task oldTask = tasks.get(id);

            if (hasOverlapWithExisting(updatedTask)) {
                throw new ManagerLoadException("Нельзя обновить задачу — она пересекается с другой");
            }

            removePrioritizedTask(oldTask);
            tasks.put(id, updatedTask);
            addPrioritizedTask(updatedTask);
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
                if (epic != null) {
                    if (hasOverlapWithExisting(updatedSubTask)) {
                        throw new ManagerLoadException("Нельзя обновить подзадачу — она пересекается с другой");
                    }

                    epic.deleteSubTask(oldSubTask);
                    removePrioritizedTask(oldSubTask);
                    subTasks.put(id, updatedSubTask);
                    epic.addSubTask(updatedSubTask);
                    addPrioritizedTask(updatedSubTask);
                }
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
                epic.deleteSubTask(subTask);
                removePrioritizedTask(subTask); // Удаляем из TreeSet
            } else {
                System.out.println("Эпик не найден");
            }
        }
    }

    // Удаление задачи по идентификатору
    @Override
    public void deleteTask(long id) {
        Task task = tasks.remove(id);
        if (task != null) {
            historyManager.remove(id);
            removePrioritizedTask(task); // Удаляем из TreeSet
        } else {
            System.out.println("Задача с указанным идентификатором не найдена");
        }
    }

    @Override
    public void deleteEpic(long id) {
        Epic epic = epics.remove(id);
        if (epic == null) {
            System.out.println("Задача с указанным идентификатором не найдена");
        } else {
            epic.getSubTasks().stream()
                    .map(SubTask::getTaskId)
                    .forEach(subTasks::remove); // удаляем подзадачи по их идентификаторам
            epic.clearSubTasks();
            System.out.println("Эпик успешно удалён");
        }
    }
}
