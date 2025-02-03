package test;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    private Task task;
    private final TaskManager taskManager = new InMemoryTaskManager();

    @BeforeEach
    void setUp() {
        task = new Task("Task", "TaskDescription");
        taskManager.addTask(task);
    }

    @Test
    void testGetTaskName() {
        assertEquals("Task", task.getTaskName());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals("TaskDescription", task.getDescription());
    }

    @Test
    void testTasksEqualityById() {
        Task task1 = new Task("Task", "Description");
        Task task2 = new Task("Task", "Description");
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2);
    }

    @Test
    void testStatus() {
        taskManager.setTaskStatus(task.getId(), Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, task.getStatus());
        taskManager.setTaskStatus(task.getId(), Status.DONE);
        assertEquals(Status.DONE, task.getStatus());
    }

    @Test
    void testToString() {
        String expected = "Task{taskName='Task', description='TaskDescription', id=" + task.getId() + ", status=NEW}";
        assertEquals(expected, task.toString());
    }

    @Test
    void testTaskImmutabilityOnAdd() {
        Task task = new Task("Task", "Description");
        taskManager.addTask(task);

        Task fetchedTask = taskManager.getTaskById(task.getId());

        assertEquals("Task", fetchedTask.getTaskName());
        assertEquals("Description", fetchedTask.getDescription());
    }
}