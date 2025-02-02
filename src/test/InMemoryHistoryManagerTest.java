package test;

import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.InMemoryHistoryManager;
import service.Managers;
import service.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        taskManager = Managers.getDefaultInMemoryManager();
    }

    @Test
    void shouldAddTaskToHistory() {
        Task task = new Task("Task", "Test Task");
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        Assertions.assertFalse(history.isEmpty());
        assertEquals(task, history.getFirst());
    }


    @Test
    void testHistoryLimit() {
        for (int i = 0; i < 15; i++) {
            Task task = new Task("Task" + i, "Description" + i);
            task.setId(i);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size());
        assertEquals("Task5", history.getFirst().getTaskName());
    }

    @Test
    void testHistoryManagerSavesTaskVersions() {
        taskManager = Managers.getDefaultInMemoryManager();
        historyManager = ((service.InMemoryTaskManager) taskManager).getHistoryManager();
        Task task = new Task("Task", "Description");
        taskManager.addTask(task);
        Task task1 = taskManager.getTaskById(task.getId());
        task.setStatus(model.Status.IN_PROGRESS);
        taskManager.taskUpdate(task.getId(), task);
        Task task2 = taskManager.getTaskById(task.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(model.Status.NEW, history.get(0).getStatus());
        assertEquals(model.Status.IN_PROGRESS, history.get(1).getStatus());
    }

}
