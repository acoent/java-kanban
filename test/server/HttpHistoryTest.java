package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Epic;
import model.Status;
import model.Task;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpHistoryTest {
    private HttpTaskServer taskServer;
    private TaskManager manager;
    private HttpClient client;
    private Gson gsonForHistory;

    private static final DateTimeFormatter REQUEST_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    void setUpClass() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        client = HttpClient.newHttpClient();
        taskServer.start();
        gsonForHistory = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).registerTypeAdapter(Duration.class, new DurationAdapter()).create();
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
    void testGetHistory() throws IOException, InterruptedException {
        LocalDateTime startTime = LocalDateTime.parse("20.04.2025 10:00:00", REQUEST_DATE_FORMATTER);
        Task task1 = new Task("Task 1", "Test task", Status.NEW, Duration.ofMinutes(120), startTime);
        manager.addTask(task1);
        Epic epic1 = new Epic("Epic 1", "Epic epic", Status.NEW);
        manager.addEpic(epic1);
        sendGetRequest(BASE_URL + "/tasks/" + task1.getId());
        sendGetRequest(BASE_URL + "/epics/" + epic1.getId());
        HttpResponse<String> historyResponse = sendGetRequest(BASE_URL + "/history");
        assertEquals(200, historyResponse.statusCode());
        List history = gsonForHistory.fromJson(historyResponse.body(), List.class);
        assertNotNull(history, "История не должна быть null");
        assertFalse(history.isEmpty());
        boolean taskFound = history.stream().anyMatch(item -> ((Map<String, Object>) item).containsKey("id") && ((Double) ((Map<String, Object>) item).get("id")).intValue() == task1.getId());
        boolean epicFound = history.stream().anyMatch(item -> ((Map<String, Object>) item).containsKey("id") && ((Double) ((Map<String, Object>) item).get("id")).intValue() == epic1.getId());
        assertTrue(taskFound);
        assertTrue(epicFound);
        String expectedStartTime = "2025-04-20T10:00:00";
        assertEquals(expectedStartTime, task1.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    private HttpResponse<String> sendGetRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("Accept", "application/json").GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
