package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("task_manager", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }


    @Test
    void shouldSaveAndLoadEmptyFile() {
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedManager.getTasks().isEmpty());
        assertTrue(loadedManager.getEpics().isEmpty());
        assertTrue(loadedManager.getSubtasks().isEmpty());
    }

    @Test
    void shouldSaveAndLoadMultipleTasks() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);
        Epic epic1 = new Epic("Epic 1", "Epic Description", Status.NEW);
        Subtask subtask1 = new Subtask(epic1.getId(), "Subtask 1", "Subtask Desc", Status.DONE);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> tasks = loadedManager.getTasks();
        List<Epic> epics = loadedManager.getEpics();
        List<Subtask> subtasks = loadedManager.getSubtasks();
        assertEquals(2, tasks.size());
        assertEquals(1, epics.size());
        assertEquals(1, subtasks.size());
        assertEquals("Task 1", tasks.get(0).getTaskName());
        assertEquals(Status.NEW, tasks.get(0).getStatus());
        assertEquals("Task 2", tasks.get(1).getTaskName());
        assertEquals(Status.IN_PROGRESS, tasks.get(1).getStatus());
        assertEquals("Epic 1", epics.get(0).getTaskName());
        assertEquals(Status.NEW, epics.get(0).getStatus());
        assertEquals("Subtask 1", subtasks.get(0).getTaskName());
        assertEquals(Status.DONE, subtasks.get(0).getStatus());
    }

}