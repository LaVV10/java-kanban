import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    if (path.equals("/epics")) {
                        handleGetEpics(exchange);
                    } else {
                        handleGetEpicById(exchange);
                    }
                    break;
                case "POST":
                    handleAddEpic(exchange);
                    break;
                case "DELETE":
                    handleDeleteEpic(exchange);
                    break;
                default:
                    sendError(exchange, "Unsupported method: " + method);
                    break;
            }
        } catch (Exception e) {
            try {
                sendError(exchange, e.getMessage());
            } catch (IOException ex) {
                System.out.println("Failed to send error response: " + ex.getMessage());
            }
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

    private void handleAddEpic(HttpExchange exchange) throws IOException {
        try {
            Epic epic = parseEpic(exchange);
            taskManager.addEpic(epic);
            sendTask(exchange, epic);
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
