package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    private TaskManager taskManager;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        epic = new Epic("Epic1", "Epic Description");
        taskManager.addEpic(epic);
        subtask = new Subtask(epic.getId(), "Subtask1", "Subtask Description", Duration.ZERO, null);
        taskManager.addSubtask(subtask);
    }

    @AfterEach
    void tearDown() {
        taskManager.removeAllTasks();
    }

    @Test
    void testSubtaskCreation() {
        assertEquals(epic.getId(), subtask.getParentEpicId());
        assertEquals("Subtask1", subtask.getTaskName());
        assertEquals("Subtask Description", subtask.getDescription());
        assertEquals(Status.NEW, subtask.getStatus());
    }

    @Test
    void testUpdateSubtaskStatus() {
        taskManager.updateSubtaskStatus(subtask.getId(), Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, subtask.getStatus());
        taskManager.updateSubtaskStatus(subtask.getId(), Status.DONE);
        assertEquals(Status.DONE, subtask.getStatus());
    }

    @Test
    void testEpicStatusAfterSubtaskUpdate() {
        taskManager.updateSubtaskStatus(subtask.getId(), Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
        taskManager.updateSubtaskStatus(subtask.getId(), Status.DONE);
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void testSubtaskBelongsToCorrectEpic() {
        Epic anotherEpic = new Epic("Another Epic", "Another Description");
        taskManager.addEpic(anotherEpic);
        assertNotEquals(anotherEpic.getId(), subtask.getParentEpicId());
    }

    @Test
    void testSubtasksEqualityById() {
        Subtask subtask1 = new Subtask(epic.getId(), "Subtask", "Description", Duration.ZERO, null);
        Subtask subtask2 = new Subtask(epic.getId(), "Subtask", "Description", Duration.ZERO, null);
        subtask1.setId(2);
        subtask2.setId(2);
        assertEquals(subtask1, subtask2);
    }

    @Test
    void testSubtaskToString() {
        String expected = "Subtask{subtaskName='Subtask1', description='Subtask Description', id=1, status=NEW, parentEpicID=0}";
        assertEquals(expected, subtask.toString());
    }

    @Test
    void testSubtaskCannotBeItsOwnEpic() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask(epic.getId(), "Subtask", "Description", Duration.ZERO, null);
        taskManager.addSubtask(subtask);
        assertNotEquals(subtask.getId(), epic.getId());
    }

    @Test
    void testSubtaskIdAfterRemoval() {
        Subtask subtask = new Subtask(epic.getId(), "Subtask", "Description", Duration.ZERO, null);
        taskManager.addSubtask(subtask);
        int subtaskId = subtask.getId();
        taskManager.removeSubtask(subtaskId);
        assertFalse(taskManager.isValidSubtaskId(subtaskId));
    }
}
