public class Main {

    public static void main(String[] args) {

        HistoryManager history = new InMemoryHistoryManager();

        TaskManager taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Задача 1", "Clean", Status.NEW);
        Epic epic1 = new Epic("epic", "Book");
        SubTask subtask1 = new SubTask("PodЗадача 1", "Check", Status.NEW, 2);

        taskManager.addTask(task1);
        taskManager.addEpic(epic1);
        taskManager.addSubTask(subtask1);

        taskManager.getTask(1); // смотрим задачу
        taskManager.getSubTask(3); // смотрим подзадачу
        taskManager.getEpic(2);

        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {


        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubTasks(epic.getTaskId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}

