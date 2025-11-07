import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            // Обновленный заголовок с полями startTime и duration
            writer.write("id,type,name,status,description,start_time,duration,epic\n");

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

            writer.write("id_counter," + Task.getIdCounter() + "\n");

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

        // Добавляем startTime
        if (task.getStartTime() != null) {
            result.append(',').append(task.getStartTime().format(DATE_TIME_FORMATTER));
        } else {
            result.append(",");
        }

        // Добавляем duration
        if (task.getDuration() != null) {
            result.append(',').append(task.getDuration().toMinutes());
        } else {
            result.append(",");
        }

        // Для подзадач добавляем epic_id
        if (type == TaskType.SUBTASK) {
            result.append(',').append(((SubTask) task).getEpicId());
        }

        return result.toString();
    }

    private Task fromString(String value) {
        String[] fields = value.split(",", -1); // Сохраняем пустые значения
        long id = Long.parseLong(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        LocalDateTime startTime = null;
        if (!fields[5].trim().isEmpty()) {
            startTime = LocalDateTime.parse(fields[5], DATE_TIME_FORMATTER);
        }

        Duration duration = null;
        if (!fields[6].trim().isEmpty()) {
            duration = Duration.ofMinutes(Long.parseLong(fields[6]));
        }

        Task task;
        switch (type) {
            case TASK:
                task = new Task(name, description, status, startTime, duration);
                break;
            case EPIC:
                task = new Epic(name, description);
                break;
            case SUBTASK:
                long epicId = Long.parseLong(fields[7]);
                task = new SubTask(name, description, status, epicId, startTime, duration);
                break;
            default:
                throw new IllegalStateException("Неожиданный тип задачи: " + type);
        }

        task.setTaskId(id);

        return task;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            reader.readLine(); // Пропускаем заголовок

            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            // Сначала загружаем все задачи, кроме id_counter
            for (String value : lines) {
                if (value.startsWith("id_counter,")) {
                    long savedId = Long.parseLong(value.split(",")[1]);
                    Task.setIdCounter(savedId);
                    break;
                }

                Task task = manager.fromString(value);

                if (task instanceof SubTask) {
                    manager.subTasks.put(task.getTaskId(), (SubTask) task);
                } else if (task instanceof Epic) {
                    manager.epics.put(task.getTaskId(), (Epic) task);
                } else {
                    manager.tasks.put(task.getTaskId(), (Task) task);
                }
            }

            // Восстановить связи "подзадача → эпик"
            for (SubTask subTask : manager.subTasks.values()) {
                Epic epic = manager.epics.get(subTask.getEpicId());
                if (epic != null) {
                    epic.addSubTask(subTask); // Вызывает recalculate и checkStatus
                }
            }

        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка при загрузке файла", e);
        }

        return manager;
    }
}
