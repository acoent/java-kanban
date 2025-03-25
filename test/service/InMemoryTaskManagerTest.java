package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;
    private Task task;
    private Epic epic;
    private Subtask subtask1;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        task = new Task("Task1", "Task Description", Duration.ZERO, null);
        taskManager.addTask(task);
        epic = new Epic("Epic1", "Epic Description");
        taskManager.addEpic(epic);
        subtask1 = new Subtask(epic.getId(), "Subtask1", "Subtask Description1", Duration.ZERO, null);
        Subtask subtask2 = new Subtask(epic.getId(), "Subtask2", "Subtask Description2", Duration.ZERO, null);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
    }

    @AfterEach
    void tearDown() {
        taskManager.removeAllTasks();
    }

    @Test
    void testAddAndGetTask() {
        Task requestedTask = taskManager.getTaskById(task.getId());
        assertNotNull(requestedTask);
        assertEquals(task.getTaskName(), requestedTask.getTaskName());
    }

    @Test
    void testUpdateTaskStatus() {
        taskManager.setTaskStatus(task.getId(), Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(task.getId()).getStatus());
    }

    @Test
    void testAddAndGetEpic() {
        Epic requestedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(requestedEpic);
        assertEquals(epic.getTaskName(), requestedEpic.getTaskName());
    }

    @Test
    void testRemoveTask() {
        taskManager.removeTask(task.getId());
        assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    void testSubtaskParentLink() {
        assertEquals(epic.getId(), subtask1.getParentEpicId());
    }

    @Test
    void testGetTasks() {
        List<Task> tasks = taskManager.getTasks();
        assertEquals(1, tasks.size());
    }

    @Test
    void testGetEpics() {
        List<Epic> epics = taskManager.getEpics();
        assertEquals(1, epics.size());
    }

    @Test
    void testGetSubtasks() {
        List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(2, subtasks.size());
    }

    @Test
    void testInMemoryTaskManagerAddsAndFindsTasks() {
        Task task = new Task("Task", "Description", Duration.ZERO, null);
        Epic epic = new Epic("Epic", "Description");
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        assertEquals(task, taskManager.getTaskById(task.getId()));
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    void testTaskManagerHandlesCustomAndGeneratedIds() {
        Task taskWithCustomId = new Task("Custom Task", "Description", Duration.ZERO, null);
        taskManager.addTask(taskWithCustomId);
        Task generatedTask = new Task("Generated Task", "Description", Duration.ZERO, null);
        taskManager.addTask(generatedTask);
        int customId = taskWithCustomId.getId();
        int generatedId = generatedTask.getId();
        assertEquals(taskWithCustomId, taskManager.getTaskById(customId));
        assertNotEquals(customId, generatedId);
    }


    @Test
    void testTaskImmutabilityOnAdd() {
        Task task = new Task("Task", "Description", Duration.ZERO, null);
        taskManager.addTask(task);
        Task fetchedTask = taskManager.getTaskById(task.getId());
        assertEquals("Task", fetchedTask.getTaskName());
        assertEquals("Description", fetchedTask.getDescription());
        assertEquals(Status.NEW, fetchedTask.getStatus());
    }

    @Test
    void testRemoveEpicAndSubtasks() {
        taskManager.removeEpic(epic.getId());
        assertNull(taskManager.getEpicById(epic.getId()));
        assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    public void testChangingTaskIdViaSetterBreaksManagerMapping() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = new Task("Test Task", "Description", Duration.ZERO, null);
        task.setId(2);
        manager.addTask(task);
        assertEquals(0, task.getId());
    }

    @Test
    void testAddAndGetEntity() {
        Task requestedTask = taskManager.getTaskById(task.getId());
        assertNotNull(requestedTask);
        assertEquals(task.getTaskName(), requestedTask.getTaskName());
        Epic requestedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(requestedEpic);
        assertEquals(epic.getTaskName(), requestedEpic.getTaskName());
        Subtask requestedSubtask1 = taskManager.getSubtaskById(subtask1.getId());
        assertNotNull(requestedSubtask1);
        assertEquals(subtask1.getTaskName(), requestedSubtask1.getTaskName());
        Subtask requestedSubtask2 = taskManager.getSubtaskById(subtask1.getId() + 1);
        assertNotNull(requestedSubtask2);
        assertEquals("Subtask2", requestedSubtask2.getTaskName());
    }

    @Test
    void testNoTimeOverlapBetweenSubtasks() {
        LocalDateTime start = subtask1.getStartTime();
        if (start == null) {
            start = LocalDateTime.now();
            subtask1.setStartTime(start);
            subtask1.setDuration(Duration.ofMinutes(30));
            taskManager.addSubtask(subtask1);
        }
        Subtask overlappingSubtask = new Subtask(epic.getId(), "Overlapping subtask", "Description", Duration.ofMinutes(30), start.plusMinutes(15));
        assertThrows(IllegalArgumentException.class, () -> taskManager.addSubtask(overlappingSubtask));
    }

}