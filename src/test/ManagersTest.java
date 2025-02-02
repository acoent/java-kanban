package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;

public class ManagersTest {
    @Test
    void testManagersInitialization() {
        TaskManager taskManager = Managers.getDefaultInMemoryManager();
        HistoryManager historyManager = Managers.getDefaultHistory();
        Assertions.assertNotNull(taskManager);
        Assertions.assertNotNull(historyManager);
    }
}
