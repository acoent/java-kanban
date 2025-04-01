package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.*;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpSubtaskTest {
    private HttpTaskServer taskServer;
    private TaskManager manager;
    private HttpClient client;

    private static final DateTimeFormatter REQUEST_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @BeforeAll
    void setUpClass() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        client = HttpClient.newHttpClient();
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
    void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Test epic");
        manager.addEpic(epic);
        String subtaskJson = "{" + "\"epicId\": " + epic.getId() + "," + "\"name\": \"Subtask 1\"," + "\"description\": \"Test subtask\"," + "\"status\": \"NEW\"," + "\"duration\": 60," + "\"startTime\": \"20.04.2025 10:00:00\"}";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/subtasks")).header("Content-Type", "application/json").header("Accept", "application/json").POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Subtask> subtasks = manager.getSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals("Subtask 1", subtasks.getFirst().getTaskName());
        String expectedStartTime = "20.04.2025 10:00:00";
        String actualStartTime = subtasks.getFirst().getStartTime().format(REQUEST_DATE_FORMATTER);
        assertEquals(expectedStartTime, actualStartTime);
    }

    @Test
    void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Test epic");
        manager.addEpic(epic);
        LocalDateTime startTime = LocalDateTime.parse("20.04.2025 10:00:00", REQUEST_DATE_FORMATTER);
        Subtask subtask = new Subtask(epic.getId(), "Subtask 1", "Test subtask", Status.NEW, Duration.ofMinutes(60), startTime);
        manager.addSubtask(subtask);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/subtasks")).header("Accept", "application/json").GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Gson gsonForIso = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new com.google.gson.TypeAdapter<LocalDateTime>() {
            public void write(com.google.gson.stream.JsonWriter out, LocalDateTime value) throws IOException {
                if (value == null) {
                    out.nullValue();
                    return;
                }
                out.value(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            public LocalDateTime read(com.google.gson.stream.JsonReader in) throws IOException {
                return LocalDateTime.parse(in.nextString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        }).registerTypeAdapter(Duration.class, new server.adapter.DurationAdapter()).create();
        List<Subtask> subtasks = gsonForIso.fromJson(response.body(), new com.google.gson.reflect.TypeToken<List<Subtask>>() {
        }.getType());
        assertEquals(1, subtasks.size());
        assertEquals("Subtask 1", subtasks.getFirst().getTaskName());
        String expectedIsoStartTime = "2025-04-20T10:00:00";
        String actualIsoStartTime = subtasks.getFirst().getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        assertEquals(expectedIsoStartTime, actualIsoStartTime);
    }

    @Test
    void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Test epic");
        manager.addEpic(epic);
        LocalDateTime startTime = LocalDateTime.parse("20.04.2025 10:00:00", REQUEST_DATE_FORMATTER);
        Subtask subtask = new Subtask(epic.getId(), "Subtask to Delete", "Delete this", Status.NEW, Duration.ofMinutes(15), startTime);
        manager.addSubtask(subtask);
        int subtaskId = subtask.getId();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/subtasks/" + subtaskId)).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    void testDeleteNonExistentSubtask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/subtasks/9999")).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}
