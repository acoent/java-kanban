package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask1;

    @BeforeEach
    void setUp(){
        taskManager = createTaskManager();
        task = new Task("Task1", "Task Description", Duration.ZERO, null);
        taskManager.addTask(task);
        epic = new Epic("Epic1", "Epic Description");
        taskManager.addEpic(epic);
        subtask1 = new Subtask(epic.getId(), "Subtask1", "Subtask Description1", Duration.ZERO, null);
        Subtask subtask2 = new Subtask(epic.getId(), "Subtask2", "Subtask Description2", Duration.ZERO, null);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
    }

    protected abstract T createTaskManager();

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
    void testRemoveEpicAndSubtasks() {
        taskManager.removeEpic(epic.getId());
        assertNull(taskManager.getEpicById(epic.getId()));
        assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    public void testChangingTaskIdViaSetterBreaksManagerMapping() {
        Task task = new Task("Test Task", "Description", Duration.ZERO, null);
        task.setId(2);
        taskManager.addTask(task);
        assertEquals(0, task.getId());
    }
}
