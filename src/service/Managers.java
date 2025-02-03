package service;

public class Managers {
    public static TaskManager getDefaultInMemoryManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}