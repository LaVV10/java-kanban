import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class EpicHandlerTest extends BaseHttpTest {

    @Test
    void shouldAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("New", "Desc");
        String json = gson.toJson(epic);

        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/epics"))
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .header("Content-Type", "application/json")
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode());
        Epic saved = gson.fromJson(response.body(), Epic.class);
        assertNotNull(saved.getTaskId());
    }

    @Test
    void shouldGetEpicById() throws IOException, InterruptedException {
        Epic saved = createEpic(new Epic("Get", "Desc"));

        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/epics/" + saved.getTaskId()))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode());
        Epic retrieved = gson.fromJson(response.body(), Epic.class);
        assertEquals(saved.getTaskId(), retrieved.getTaskId());
    }

    @Test
    void shouldDeleteEpic() throws IOException, InterruptedException {
        Epic saved = createEpic(new Epic("Del", "Desc"));

        client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/epics/" + saved.getTaskId()))
                        .DELETE()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        HttpResponse<String> get = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/epics/" + saved.getTaskId()))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(404, get.statusCode());
    }
}
