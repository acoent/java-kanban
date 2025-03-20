import model.Epic;
import model.Subtask;
import model.Task;
import service.FileBackedTaskManager;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file = new File("data/kanban-list.csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        // Создание задач
        Task task1 = new Task("Task1", "Task1 Description");
        taskManager.addTask(task1);
        Task task2 = new Task("Task2", "Task2 Description");
        taskManager.addTask(task2);

        // Создание эпика
        Epic epic = new Epic("Epic1", "Epic Description");
        taskManager.addEpic(epic);

        // Создание подзадач
        Subtask subtask1 = new Subtask(epic.getId(), "Subtask1", "Subtask1 Description");
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask(epic.getId(), "Subtask2", "Subtask2 Description");
        taskManager.addSubtask(subtask2);

        // Вывод задач до сохранения
        System.out.println("Initial tasks:");
        printAllTasks(taskManager);

        // Создание нового менеджера из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        // Вывод загруженных задач
        System.out.println("\nTasks loaded from file:");
        printAllTasks(loadedManager);
    }


    private static void printAllTasks(FileBackedTaskManager taskManager) {
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
