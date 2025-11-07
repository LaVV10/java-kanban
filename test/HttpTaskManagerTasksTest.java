import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {

    private TaskManager manager;
    private HttpTaskServer taskServer;
    private HttpClient client;
    private Gson gson;

    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        client = HttpClient.newHttpClient();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        LocalDateTime baseTime = LocalDateTime.now().plusHours(1).truncatedTo(java.time.temporal.ChronoUnit.MINUTES);

        Task task = new Task("Test", "Desc", Status.NEW, baseTime, Duration.ofMinutes(30));
        String json = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals("Test", tasks.get(0).getTaskName());
    }

    @Test
    public void testAddSubTaskToEpic() throws IOException, InterruptedException {
        LocalDateTime baseTime = LocalDateTime.now().plusHours(1).truncatedTo(java.time.temporal.ChronoUnit.MINUTES);

        Epic epic = new Epic("Epic", "Desc");
        String epicJson = gson.toJson(epic);

        HttpRequest epicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> epicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, epicResponse.statusCode());

        Epic savedEpic = gson.fromJson(epicResponse.body(), Epic.class);
        long epicId = savedEpic.getTaskId();

        SubTask subTask = new SubTask("Sub", "Desc", Status.NEW, epicId, baseTime,
                Duration.ofMinutes(30));
        String subTaskJson = gson.toJson(subTask);

        HttpRequest subTaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(subTaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<SubTask> epicSubtasks = manager.getEpicSubTasks(epicId);
        assertEquals(1, epicSubtasks.size());
        assertEquals("Sub", epicSubtasks.get(0).getTaskName());
    }
}
