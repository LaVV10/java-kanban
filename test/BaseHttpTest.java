import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpTest {
    protected static final Gson gson = createGson();
    protected static final HttpClient client = HttpClient.newHttpClient();

    protected HttpTaskServer server;
    protected TaskManager taskManager;
    protected File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("test_tasks", ".csv");
        tempFile.deleteOnExit();

        taskManager = new FileBackedTaskManager(tempFile);
        server = new HttpTaskServer(taskManager);
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    protected Task createTask(Task task) throws IOException, InterruptedException {
        String json = gson.toJson(task);
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks"))
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .header("Content-Type", "application/json")
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        return gson.fromJson(response.body(), Task.class);
    }

    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting()
                .create();
    }

    protected void getTaskById(long id) throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks/" + id))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() == 404) {
            return;
        }

        gson.fromJson(response.body(), Task.class);
    }
}
