import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    protected HashMap<Long, Task> tasks;
    protected HashMap<Long, SubTask> subTasks;
    protected HashMap<Long, Epic> epics;

    // Коллекция для хранения задач
    public Manager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
    }

    // Проверка наличия задач
    private boolean isAnyTasks() {
        return !tasks.isEmpty();
    }

    // Получение всех задач
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            allTasks.add(task); // Добавляем текущую задачу
            // Если задача — эпик, собираем его подзадачи
            if (task instanceof Epic) {
                Epic epic = (Epic) task;
                ArrayList<Task> subTasks = getEpicSubTasks(epic);
                allTasks.addAll(subTasks); // Добавляем подзадачи эпика
            }
        }
        return allTasks;
    }

    // получение списка подзадач определенного эпика
    public ArrayList<Task> getEpicSubTasks(Epic epic) {
        ArrayList<Task> resultSubTasks = new ArrayList<>();
        for (SubTask subTask : epic.getSubTasks()) {
            resultSubTasks.add(subTask);
        }
        return resultSubTasks;
    }

    // Удаление всех задач
    public void clearAllTasks() {
        if (isAnyTasks()) {
            tasks.clear();
        }
    }

    // удаление всех подзадач
    public void clearSubtasks() {
        for (SubTask subTask : subTasks.values()) {
            subTask.getEpic().deleteSubTask(subTask);
        }
        subTasks.clear();
    }

    // Получение задачи по идентификатору
    public Task getTask(Long id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else if (subTasks.containsKey(id)) {
            return subTasks.get(id);
        } else if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return null;
    }

    // Создание задачи
    public long addTask(Task o, Long id) {

        boolean newTask = false;
        if (id == null) {
            id = 0L;
        }
        if (id == 0) {
            id = TaskId.getNewId(); // увеличиваем id на единицу, если задача новая
            newTask = true;
        }
        o.setTaskId(id); // присваиваем задаче id
        if (o.getClass() == Task.class) {
            tasks.put(id, o);
        } else if (o.getClass() == SubTask.class) {
            subTasks.put(id, (SubTask) o);
            if (!newTask) {
                ((SubTask) o).getEpic().deleteSubTask(((SubTask) o));
            }
            ((SubTask) o).getEpic().setSubTask(((SubTask) o));
        } else if (o.getClass() == Epic.class) {
            epics.put(id, (Epic) o);
        }
        return id;
    }

    // Удаление задачи по идентификатору
    public void deleteTask(Long id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (subTasks.containsKey(id)) { // ищем id в подзадачах и если есть, то удаляем также из эпика
            Epic epic = subTasks.get(id).getEpic();
            epic.deleteSubTask(subTasks.get(id));
            subTasks.remove(id);
        } else if (epics.containsKey(id)) { // ищем id в эпиках и если есть, то удаляем также подзадачи
            for (SubTask subtask : epics.get(id).getSubTasks()) {
                subTasks.remove(subtask);
            }
            epics.remove(id);
        }
    }

}
