package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Status;
import model.Task;
import org.junit.jupiter.api.*;
import server.adapter.DurationAdapter;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PrioritizedHandlerTest {
    private HttpTaskServer taskServer;
    private TaskManager manager;
    private HttpClient client;
    private Gson gsonForIso;
    private static final DateTimeFormatter REQUEST_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @BeforeAll
    void setUpClass() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        client = HttpClient.newHttpClient();
        gsonForIso = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new com.google.gson.TypeAdapter<LocalDateTime>() {
            @Override
            public void write(com.google.gson.stream.JsonWriter out, LocalDateTime value) throws IOException {
                if (value == null) {
                    out.nullValue();
                    return;
                }
                out.value(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }

            @Override
            public LocalDateTime read(com.google.gson.stream.JsonReader in) throws IOException {
                return LocalDateTime.parse(in.nextString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        }).registerTypeAdapter(Duration.class, new DurationAdapter()).create();
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
    void testGetPrioritizedTasks() throws IOException, InterruptedException {
        LocalDateTime startTime1 = LocalDateTime.parse("20.04.2025 09:00:00", REQUEST_DATE_FORMATTER);
        LocalDateTime startTime2 = LocalDateTime.parse("20.04.2025 10:00:00", REQUEST_DATE_FORMATTER);
        Task task1 = new Task("Task 1", "Описание 1", Status.NEW, Duration.ofMinutes(60), startTime2);
        Task task2 = new Task("Task 2", "Описание 2", Status.NEW, Duration.ofMinutes(30), startTime1);
        manager.addTask(task1);
        manager.addTask(task2);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/prioritized")).header("Accept", "application/json").GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> prioritizedTasks = gsonForIso.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertNotNull(prioritizedTasks, "Ответ не должен быть null.");
        assertEquals(2, prioritizedTasks.size());
        assertEquals(task2.getId(), prioritizedTasks.get(0).getId());
        assertEquals(task1.getId(), prioritizedTasks.get(1).getId());
    }

    @Test
    void testPostMethodNotAllowed() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/prioritized")).header("Accept", "application/json").POST(HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
    }

    @Test
    void testDeleteMethodNotAllowed() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/prioritized")).header("Accept", "application/json").DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
    }

    @Test
    void testUnknownPathReturns400() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/prioritized/extra")).header("Accept", "application/json").GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
        assertEquals("Неизвестный запрос.", response.body());
    }
}
