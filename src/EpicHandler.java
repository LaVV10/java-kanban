import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            if (!path.startsWith("/epics")) {
                sendNotFound(exchange);
                return;
            }

            switch (parts.length) {
                case 2 -> {
                    switch (method) {
                        case "GET":
                            handleGetEpics(exchange);
                            break;
                        case "POST":
                            handleAddOrUpdateEpic(exchange);
                            break;
                        default:
                            sendError(exchange, "Unsupported method: " + method);
                    }
                }
                case 3 -> {
                    long id = Long.parseLong(parts[2]);
                    switch (method) {
                        case "GET" -> handleGetEpicById(exchange);
                        case "DELETE" -> handleDeleteEpic(exchange);
                        default -> sendError(exchange, "Unsupported method: " + method);
                    }
                }
                case 4 -> {
                    if ("subtasks".equals(parts[3])) {
                        long epicId = Long.parseLong(parts[2]);
                        switch (method) {
                            case "GET" -> handleGetSubTasksByEpicId(exchange, epicId);
                            case "POST" -> handleAddOrUpdateSubTaskToEpic(exchange, epicId);
                            default -> sendNotFound(exchange);
                        }
                    } else {
                        sendNotFound(exchange);
                    }
                }
                default -> sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            try {
                sendError(exchange, "Invalid ID format");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (Exception e) {
            try {
                sendError(exchange, e.getMessage());
            } catch (IOException ex) {
                System.out.println("Failed to send error response: " + ex.getMessage());
            }
        }
    }

    private void handleGetSubTasksByEpicId(HttpExchange exchange, long epicId) throws IOException {
        List<SubTask> subTasks = taskManager.getEpicSubTasks(epicId);
        if (subTasks.isEmpty()) {
            sendText(exchange, "[]", 200);
        } else {
            sendSubTasks(exchange, subTasks);
        }
    }

    private void handleAddOrUpdateSubTaskToEpic(HttpExchange exchange, long epicId) throws IOException {
        try {
            SubTask subTask = parseSubTask(exchange);

            if (subTask.getEpicId() != epicId) {
                sendError(exchange, "Epic ID in path and in task body do not match");
                return;
            }

            if (subTask.getTaskId() == null || subTask.getTaskId() == 0) {
                taskManager.addSubTask(subTask);
                sendTask(exchange, subTask);
            } else {
                SubTask existing = taskManager.getSubTask(subTask.getTaskId());
                if (existing == null || existing.getEpicId() != epicId) {
                    sendNotFound(exchange);
                    return;
                }
                taskManager.updateSubTask(subTask, subTask.getTaskId());
                sendTask(exchange, subTask);
            }
        } catch (TaskOverlapException e) {
            sendHasOverlaps(exchange);
        } catch (Exception e) {
            sendError(exchange, "Invalid subtask data: " + e.getMessage());
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getAllEpics();
        sendEpics(exchange, epics);
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        long id = getIdFromPath(exchange);
        Epic epic = taskManager.getEpic(id);
        if (epic == null) {
            sendNotFound(exchange);
        } else {
            sendTask(exchange, epic);
        }
    }

    private void handleAddOrUpdateEpic(HttpExchange exchange) throws IOException {
        try {
            Epic epic = parseEpic(exchange);
            if (epic.getTaskId() == null || epic.getTaskId() == 0) {
                // Новый эпик
                taskManager.addEpic(epic);
                sendTask(exchange, epic);
            } else {
                // Обновление
                Epic existing = taskManager.getEpic(epic.getTaskId());
                if (existing == null) {
                    sendNotFound(exchange);
                    return;
                }
                epic.setTaskStatus(existing.getTaskStatus()); // Статус управляется подзадачами
                taskManager.updateEpic(epic, epic.getTaskId());
                sendTask(exchange, epic);
            }
        } catch (Exception e) {
            sendError(exchange, "Invalid epic data: " + e.getMessage());
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        long id = getIdFromPath(exchange);
        taskManager.deleteEpic(id);
        sendText(exchange, "Epic deleted", 201);
    }

    private long getIdFromPath(HttpExchange exchange) {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        return Long.parseLong(parts[2]);
    }
}
