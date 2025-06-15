import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    private HashMap<Long, Task> tasks; // Коллекция для хранения задач
    public Manager() {
        tasks = new HashMap<>();
    }

    // Проверка наличия задач
    private boolean isAnyTasks() {
        return !tasks.isEmpty();
    }

    // Получение всех задач
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        if (isAnyTasks()) {
            for (Long taskId : tasks.keySet()) {
                allTasks.add(tasks.get(taskId));
            }
        }
        return allTasks;
    }

    // Удаление всех задач
    public void clearAllTasks() {
        if (isAnyTasks()) {
            tasks.clear();
        }
    }

    // Получение задачи по идентификатору
    public Task getTaskById(long id) {
        return tasks.get(id);
    }

    // Создание задачи
    public void addTask(Task newTask) {
        if (newTask == null) {
            System.out.println("Ошибка добавления задачи");
        } else {
            newTask.setTaskId(TaskId.getNewId()); // Присвоить новый ID
            if (newTask instanceof SubTask) { //если это подзадача, то добавить к эпику
                long epicId = ((SubTask) newTask).getEpicId(); //найти нужный эпик
                Epic epic = (Epic) tasks.get(epicId);
                epic.setSubTasks(newTask.getTaskId()); //добавить подзадачу к эпику
            }
            tasks.put(newTask.getTaskId(), newTask); //добавить в список задач
        }
    }

    // Обновление задачи
    public void updateTask(Task newTask) {
        if (newTask == null) { //если задача пустая
            System.out.println("Ошибка обновления задачи");
        } else {
            Task task = tasks.get(newTask.getTaskId());
            Status newStatus = newTask.getTaskStatus();
            if (task == null) { //если задача не найдена в списке
                System.out.println("Ошибка обновления задачи");
            } else {
                task.setTaskStatus(newStatus); //установить новый статус
                task.setTaskName(newTask.getTaskName());
                task.setTaskDescription(newTask.getTaskDescription());
                if (newTask instanceof SubTask) { //если это подзадача
                    long epicId = ((SubTask) newTask).getEpicId(); //найти id эпика
                    Epic epic = (Epic) tasks.get(epicId);
                    if (isAllSubTaskInEpicDone(epic)) {
                        //если все подзадачи готовы, то эпик тоже готов
                        tasks.get(epicId).setTaskStatus(Status.DONE);
                    } else { //если есть задачи в процессе выполнения, то эпик на выполнении
                        tasks.get(epicId).setTaskStatus(Status.IN_PROGRESS);
                    }
                }
            }
        }
    }

    // Проверить готовность подзадач эпика
    private boolean isAllSubTaskInEpicDone(Epic epicId) {
        ArrayList<SubTask> subTasks = getSubTasks(epicId.getTaskId());
        for (Task subTask : subTasks) {
            //если нет выполненных подзадач, то вернуть false
            if (!subTask.getTaskStatus().equals(Status.DONE)) {
                return false;
            }
        }
        return true;
    }

    // Получение списка всех подзадач определённого эпика.
    public ArrayList<SubTask> getSubTasks(long epicId) {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        if (tasks.get(epicId) instanceof Epic epic) {
            for (Long taskId : epic.getSubTasks()) {
                subTasks.add((SubTask) tasks.get(taskId));
            }
        }
        return subTasks;
    }

    // Удаление задачи по идентификатору
    public void removeTask(long newTaskId) {
        Task task = tasks.get(newTaskId);
        if (task == null) {
            System.out.println("Задачи с таким ID не найдено");
        }
        // Удаление подзадач если переданный ID является эпиком
        if (tasks.get(newTaskId) instanceof Epic) {
            ArrayList<SubTask> subTasks = getSubTasks(newTaskId); //список подзадач эпика
            for (Task subTask : subTasks) {
                tasks.remove(subTask.getTaskId()); //удалить задачу
            }
        }
        // Удаление подзадачи из эпика
        if (tasks.get(newTaskId) instanceof SubTask) {
            SubTask newSubTask = (SubTask) tasks.get(newTaskId);
            long epicId = newSubTask.getEpicId();
            Epic epic = (Epic) tasks.get(epicId);
            epic.getSubTasks().remove(newTaskId);
        }
        tasks.remove(newTaskId); //удалить задачу
    }
}
