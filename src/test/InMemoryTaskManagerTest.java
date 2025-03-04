package test;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

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
        task = new Task("Task1", "Task Description");
        taskManager.addTask(task);
        epic = new Epic("Epic1", "Epic Description");
        taskManager.addEpic(epic);
        subtask1 = new Subtask(epic.getId(), "Subtask1", "Subtask Description1");
        Subtask subtask2 = new Subtask(epic.getId(), "Subtask2", "Subtask Description2");
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
        Task task = new Task("Task", "Description");
        Epic epic = new Epic("Epic", "Description");
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        assertEquals(task, taskManager.getTaskById(task.getId()));
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    void testTaskManagerHandlesCustomAndGeneratedIds() {
        Task taskWithCustomId = new Task("Custom Task", "Description");
        taskManager.addTask(taskWithCustomId);
        Task generatedTask = new Task("Generated Task", "Description");
        taskManager.addTask(generatedTask);
        int customId = taskWithCustomId.getId();
        int generatedId = generatedTask.getId();
        assertEquals(taskWithCustomId, taskManager.getTaskById(customId));
        assertNotEquals(customId, generatedId);
    }


    @Test
    void testTaskImmutabilityOnAdd() {
        Task task = new Task("Task", "Description");
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
        Task task = new Task("Test Task", "Description");
        task.setId(2);
        manager.addTask(task);
        assertNull(manager.getTaskById(2));
    }

    @Test
    public void testChangingSubtaskStatusBySetterBypassesEpicStatusUpdate() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic Task", "Description");
        epic.setId(30);
        manager.addEpic(epic);
        Subtask subtask = new Subtask(epic.getId(), "Subtask", "Description", Status.NEW);
        subtask.setId(31);
        manager.addSubtask(subtask);
        assertEquals(Status.NEW, epic.getStatus());
        subtask.setStatus(Status.DONE);
        assertEquals(Status.NEW, epic.getStatus());
    }
}