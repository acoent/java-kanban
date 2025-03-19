import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.FileBackedTaskManager;
import service.TaskManager;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file = new File("data/kanban-list.csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        Task task1 = new Task("Task1", "Task1 Description");
        taskManager.addTask(task1);
        Task task2 = new Task("Task2", "Task2 Description");
        taskManager.addTask(task2);
        Epic epic = new Epic("Epic1", "Epic Description");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask(2, "Subtask1", "Subtask1 Description");
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask(2, "Subtask2", "Subtask2 Description");
        subtask2.setId(5);
        taskManager.addSubtask(subtask2);
        System.out.println("Initial tasks:");
        printAllTasks(taskManager);
        taskManager.setTaskStatus(1, Status.IN_PROGRESS);
        taskManager.updateSubtaskStatus(4, Status.DONE);
        taskManager.removeTask(2);
        taskManager.removeEpic(3);
        System.out.println("\nUpdated tasks after changes:");
        printAllTasks(taskManager);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        System.out.println("\nTasks loaded from file:");
        printAllTasks(loadedManager);
    }

    private static void printAllTasks(TaskManager taskManager) {
        for (Task task : taskManager.getTasks()) {
            System.out.println("Task: " + task);
        }
        for (Epic epic : taskManager.getEpics()) {
            System.out.println("Epic: " + epic);
        }
        for (Subtask subtask : taskManager.getSubtasks()) {
            System.out.println("Subtask: " + subtask);
        }
    }
}