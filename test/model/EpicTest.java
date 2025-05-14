package model;

import exception.TimeIntersectionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;
    private final TaskManager taskManager = new InMemoryTaskManager();

    @BeforeEach
    void setUp() {
        epic = new Epic("Epic1", "Description1");
        taskManager.addEpic(epic);
        subtask1 = new Subtask(epic.getId(), "sub1", "SubDescription1", Duration.ofMinutes(30), LocalDateTime.now());
        subtask2 = new Subtask(epic.getId(), "sub2", "SubDescription2", Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
    }

    @AfterEach
    void tearDown() {
        taskManager.removeAllTasks();
    }

    @Test
    void testAllNewStatus() {
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void testAllDoneStatus() {
        taskManager.updateSubtaskStatus(subtask1.getId(), Status.DONE);
        taskManager.updateSubtaskStatus(subtask2.getId(), Status.DONE);
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void testMixedDoneStatus() {
        taskManager.updateSubtaskStatus(subtask1.getId(), Status.DONE);
        taskManager.updateSubtaskStatus(subtask2.getId(), Status.NEW);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void testInProgressStatus() {
        taskManager.updateSubtaskStatus(subtask1.getId(), Status.IN_PROGRESS);
        taskManager.updateSubtaskStatus(subtask2.getId(), Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void testEmptyEpic() {
        Epic emptyEpic = new Epic("Empty Epic", "No subtasks");
        taskManager.addEpic(emptyEpic);
        assertEquals(Status.NEW, emptyEpic.getStatus());
    }

    @Test
    void testEpicCannotBeItsOwnSubtask() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask(epic.getId(), "Subtask", "Description", Duration.ZERO, null);
        taskManager.addSubtask(subtask);
        assertNotEquals(epic.getId(), subtask.getId());
    }

    @Test
    void testSubtaskIdRemovedFromEpic() {
        int subtaskId = subtask1.getId();
        taskManager.removeSubtask(subtaskId);
        assertFalse(epic.getSubtaskIds().contains(subtaskId));
    }

    @Test
    void testNoTimeOverlapBetweenSubtasks() {
        Subtask overlappingSubtask = new Subtask(epic.getId(), "Overlapping subtask", "Description", Duration.ofMinutes(30), subtask1.getStartTime());
        assertThrows(TimeIntersectionException.class, () -> taskManager.addSubtask(overlappingSubtask));
    }

    @Test
    void testFileHandlingException() {
        assertThrows(RuntimeException.class, () -> {
            throw new RuntimeException("Ошибка работы с файлом");
        });
    }

    @Test
    void testEpicStatusCalculation() {
        taskManager.updateSubtaskStatus(subtask1.getId(), Status.NEW);
        taskManager.updateSubtaskStatus(subtask2.getId(), Status.NEW);
        assertEquals(Status.NEW, epic.getStatus());
        taskManager.updateSubtaskStatus(subtask1.getId(), Status.DONE);
        taskManager.updateSubtaskStatus(subtask2.getId(), Status.DONE);
        assertEquals(Status.DONE, epic.getStatus());
        taskManager.updateSubtaskStatus(subtask1.getId(), Status.NEW);
        taskManager.updateSubtaskStatus(subtask2.getId(), Status.DONE);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

}
