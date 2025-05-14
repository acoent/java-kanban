package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Status;
import model.Task;
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
public class HttpTaskTest {
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
    void testAddTask() throws IOException, InterruptedException {
        String taskJson = "{" + "\"name\": \"Task 1\"," + "\"description\": \"Test task\"," + "\"status\": \"NEW\"," + "\"duration\": 120," + "\"startTime\": \"20.04.2025 10:00:00\"" + "}";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks")).header("Content-Type", "application/json").header("Accept", "application/json").POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        List<Task> tasks = manager.getTasks();
        assertEquals(1, tasks.size());
        assertEquals("Task 1", tasks.getFirst().getTaskName());
        String expectedStartTime = "20.04.2025 10:00:00";
        String actualStartTime = tasks.getFirst().getStartTime().format(REQUEST_DATE_FORMATTER);
        assertEquals(expectedStartTime, actualStartTime);
    }

    @Test
    void testGetTasks() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.parse("20.04.2025 10:00:00", REQUEST_DATE_FORMATTER);
        Task task = new Task("Task 1", "Test task", Status.NEW, Duration.ofMinutes(120), startTime);
        manager.addTask(task);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks")).header("Accept", "application/json").GET().build();
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
        List<Task> tasks = gsonForIso.fromJson(response.body(), new com.google.gson.reflect.TypeToken<List<Task>>() {
        }.getType());
        assertEquals(1, tasks.size());
        assertEquals("Task 1", tasks.getFirst().getTaskName());
        String expectedIsoStartTime = "2025-04-20T10:00:00";
        String actualIsoStartTime = tasks.getFirst().getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        assertEquals(expectedIsoStartTime, actualIsoStartTime);
    }

    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.parse("20.04.2025 10:00:00", REQUEST_DATE_FORMATTER);
        Task task = new Task("Task to Delete", "Delete this", Status.NEW, Duration.ofMinutes(15), startTime);
        manager.addTask(task);
        int taskId = task.getId();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/" + taskId)).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getTasks().isEmpty());
    }

    @Test
    void testDeleteNonExistentTask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/9999")).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}
