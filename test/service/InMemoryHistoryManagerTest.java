package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Task1", "Task1desc");
        task1.setId(1);
        task2 = new Task("Task2", "Task2desc");
        task2.setId(2);
    }

    @Test
    void shouldAddTaskToHistory() {
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertFalse(history.isEmpty());
        assertEquals(1, history.size());
        assertEquals(task1, history.getFirst());
    }

    @Test
    void shouldReplaceSameTaskIfStatusUnchanged() {
        historyManager.add(task1);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.getFirst());
    }

    @Test
    void shouldKeepCorrectOrderWhenAddingMultipleTasks() {
        historyManager.add(task1);
        historyManager.add(task2);
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    void shouldMoveTaskToEndIfReAddedWithSameStatus() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1));
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.getFirst());
    }

    @Test
    void shouldRemoveAllVersionsOfTask() {
        historyManager.add(task1);
        task1.setStatus(Status.IN_PROGRESS);
        historyManager.add(task1);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void shouldHandleRemovingNonExistentTask() {
        historyManager.add(task1);
        historyManager.remove(99);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.getFirst());
    }

    @Test
    void shouldHandleEmptyHistory() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void shouldCorrectlyHandleHistoryLimit() {
        List<Task> addedTasks = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Task task = new Task("Task" + i, "Description" + i);
            task.setId(i);
            historyManager.add(task);
            addedTasks.add(task);
        }
        List<Task> history = historyManager.getHistory();
        assertEquals(4, history.size());
        for (Task task : addedTasks) {
            historyManager.remove(task.getId());
        }
        history = historyManager.getHistory();
        assertTrue(history.isEmpty());
        for (int i = 0; i < 4; i++) {
            Task task = new Task("Task" + i, "Description" + i);
            task.setId(i + 10);
            historyManager.add(task);
        }
        history = historyManager.getHistory();
        assertEquals(4, history.size());
        assertEquals("Task0", history.getFirst().getTaskName());
    }
}