package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ManagersTest {
    @Test
    void testManagersInitialization() {
        TaskManager taskManager = Managers.getDefaultInMemoryManager();
        HistoryManager historyManager = Managers.getDefaultHistory();
        Assertions.assertNotNull(taskManager);
        Assertions.assertNotNull(historyManager);
    }
}
