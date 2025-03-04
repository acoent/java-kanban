package test;

import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;
    private final TaskManager taskManager = new InMemoryTaskManager();

    @BeforeEach
    void setUp() {
        epic = new Epic("Epic1", "Description1");
        taskManager.addEpic(epic);
        subtask1 = new Subtask(epic.getId(), "sub1", "SubDescription1");
        subtask2 = new Subtask(epic.getId(), "sub2", "SubDescription2");
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
    }

    @AfterEach
    void tearDown() {
        taskManager.removeAllTasks();
    }

    @Test
    void testAddEpics() {
        Epic newEpic = new Epic("newEpic", "newDescription");
        taskManager.addEpic(newEpic);
        Subtask subtask = new Subtask(newEpic.getId(), "sub", "SubDescription");
        taskManager.addSubtask(subtask);
        assertEquals(2, taskManager.getEpics().size());
        assertEquals(Status.NEW, newEpic.getStatus());
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
        Subtask subtask = new Subtask(epic.getId(), "Subtask", "Description");
        taskManager.addSubtask(subtask);
        Assertions.assertNotEquals(epic.getId(), subtask.getId());
    }

    @Test
    void testSubtaskIdRemovedFromEpic() {
        int subtaskId = subtask1.getId();

        // Удаляем подзадачу
        taskManager.removeSubtask(subtaskId);

        // Проверяем, что ID удаленной подзадачи больше нет в списке подзадач эпика
        Assertions.assertFalse(epic.getSubtaskIds().contains(subtaskId));
    }

}