package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import org.junit.jupiter.api.*;
import server.adapter.DurationAdapter;
import server.adapter.LocalDateTimeAdapter;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpEpicTest {
    private HttpTaskServer taskServer;
    private TaskManager manager;
    private HttpClient client;
    private Gson gson;

    @BeforeAll
    void setUpClass() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        client = HttpClient.newHttpClient();
        gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).registerTypeAdapter(Duration.class, new DurationAdapter()).create();
        taskServer.start();
    }

    @AfterAll
    void tearDownClass() {
        taskServer.stop();
    }

    @BeforeEach
    void setUp() {
        manager.removeAllTasks();
        manager.removeAllSubtasks();
        manager.removeAllEpics();
    }

    @Test
    void testAddEpic() throws IOException, InterruptedException {
        String epicJson = gson.toJson(new Epic("Epic 1", "Test epic"));
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/epics")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Epic> epics = manager.getEpics();
        assertEquals(1, epics.size());
        assertEquals("Epic 1", epics.getFirst().getTaskName());
    }

    @Test
    void testGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Test epic");
        manager.addEpic(epic);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/epics")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Epic> epics = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());
        assertEquals(1, epics.size());
        assertEquals("Epic 1", epics.getFirst().getTaskName());
    }

    @Test
    void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic to Delete", "Delete this epic");
        manager.addEpic(epic);
        int epicId = epic.getId();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/epics/" + epicId)).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getEpics().isEmpty());
    }
}
