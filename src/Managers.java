import java.io.File;

public class Managers {
    private static final FileBackedTaskManager TASK_MANAGER = loadTaskManager();
    private static final HistoryManager HISTORY_MANAGER = new InMemoryHistoryManager();

    private static FileBackedTaskManager loadTaskManager() {
        File file = new File("tasks.csv");
        if (file.exists()) {
            return FileBackedTaskManager.loadFromFile(file);
        } else {
            return new FileBackedTaskManager(file);
        }
    }

    public static TaskManager getDefault() {
        return TASK_MANAGER;
    }

    public static HistoryManager getDefaultHistory() {
        return HISTORY_MANAGER;
    }
}
