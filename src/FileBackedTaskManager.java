import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void updateTask(Task task, long id) {
        super.updateTask(task, id);
        save();
    }

    @Override
    public void updateEpic(Epic epic, long id) {
        super.updateEpic(epic, id);
        save();
    }

    @Override
    public void updateSubTask(SubTask subtask, long id) {
        super.updateSubTask(subtask, id);
        save();
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public void clearAllSubTasks() {
        super.clearAllSubTasks();
        save();
    }

    @Override
    public void clearAllEpic() {
        super.clearAllEpic();
        save();
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(super.getAllTasks());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(super.getAllEpics());
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(super.getAllSubTasks());
    }

    @Override
    public void deleteTask(long id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(long id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void removeSubTask(long id) {
        super.removeSubTask(id);
        save();

    }

    @Override
    public List<SubTask> getEpicSubTasks(long epicId) {
        List<SubTask> subtasks = super.getEpicSubTasks(epicId);
        save();
        return subtasks;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subtask) {
        super.addSubTask(subtask);
        save();
    }

    @Override
    public List<Task> getHistory() {
        List<Task> taskHistory = super.getHistory();
        save();
        return taskHistory;
    }

    @Override
    public Task getTask(long id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(long id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTask(long id) {
        SubTask subtask = super.getSubTask(id);
        save();
        return subtask;
    }

    // Метод сохранения задач в файл
    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            // Заголовок таблицы
            writer.write("id,type,name,status,description,epic\n");

            // Сохраняем обычные задачи
            for (Map.Entry<Long, Task> entry : tasks.entrySet()) {
                writer.write(taskToString(entry.getValue()) + "\n");
            }

            // Сохраняем эпики
            for (Map.Entry<Long, Epic> entry : epics.entrySet()) {
                writer.write(taskToString(entry.getValue()) + "\n");
            }

            // Сохраняем подзадачи
            for (Map.Entry<Long, SubTask> entry : subTasks.entrySet()) {
                writer.write(taskToString(entry.getValue()) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла", e);
        }
    }

    // Метод преобразования задачи в строку
    private String taskToString(Task task) {
        StringBuilder result = new StringBuilder();
        TaskType type;

        if (task instanceof SubTask) {
            type = TaskType.SUBTASK;
        } else if (task instanceof Epic) {
            type = TaskType.EPIC;
        } else {
            type = TaskType.TASK;
        }

        result.append(task.getTaskId())
                .append(',')
                .append(type)
                .append(',')
                .append(task.getTaskName())
                .append(',')
                .append(task.getTaskStatus())
                .append(',')
                .append(task.getTaskDescription());

        if (type == TaskType.SUBTASK) {
            result.append(',').append(((SubTask) task).getEpicId());
        }

        return result.toString();
    }

    private Task fromString(String value) {
        String[] fields = value.split(",");
        long id = Long.parseLong(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case TASK:
                return new Task(id, name, description, status);
            case EPIC:
                return new Epic(id, name, description);
            case SUBTASK:
                long epicId = Long.parseLong(fields[5]);
                return new SubTask(id, name, description, status, epicId);
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Пропускаем первую строку (заголовок)
            reader.readLine();

            // Читаем строки с задачами
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = manager.fromString(line);
                if (task instanceof Epic) {
                    manager.epics.put(task.getTaskId(), (Epic) task);
                } else if (task instanceof SubTask) {
                    manager.subTasks.put(task.getTaskId(), (SubTask) task);
                } else if (task instanceof Task) {
                    manager.tasks.put(task.getTaskId(), (Task) task);
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка при загрузке файла", e);
        }

        return manager;
    }
}
