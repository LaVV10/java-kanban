import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    // Метод для получения всех обычных задач
    List<Task> getAllTasks();

    // Метод для получения всех эпиков
    List<Epic> getAllEpics();

    List<SubTask> getAllSubTasks();

    // получение списка подзадач определенного эпика
    List<SubTask> getEpicSubTasks(long epicId);

    // Удаление всех задач
    void clearAllTasks();

    // Удаление всех эпиков
    void clearAllEpic();

    void clearAllSubTasks();

    // Получение задачи по идентификатору
    Task getTask(long id);

    Epic getEpic(long id);

    SubTask getSubTask(long id);

    // Метод для создания задачи
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTask(SubTask subTask);

    // Метод для обновления задачи
    void updateTask(Task updatedTask, long id);

    void updateSubTask(SubTask updatedSubTask, long id);

    void updateEpic(Epic updatedEpic, long id);

    // Удаление подзадачи по идентификатору
    void removeSubTask(long id);

    // Удаление задачи по идентификатору
    void deleteTask(long id);

    void deleteEpic(long id);
}