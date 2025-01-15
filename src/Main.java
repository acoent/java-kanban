import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Task1", "Description1");
        Task task2 = new Task("Task2", "Description2");
        Epic epic1 = new Epic("Epic1", "Description1");
        Epic epic2 = new Epic("Epic2", "Description2");
        Subtask subtask1 = new Subtask(2, "sub1", "SubDescription1");
        Subtask subtask2 = new Subtask(2, "sub2", "SubDescription2");
        Subtask subtask3 = new Subtask(3, "sub3", "SubDescription3");

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getSubtasks());

        taskManager.setTaskStatus(0, Status.IN_PROGRESS);
        taskManager.setTaskStatus(1, Status.DONE);
        taskManager.updateSubtaskStatus(4, Status.DONE);
        taskManager.updateSubtaskStatus(5, Status.IN_PROGRESS);
        taskManager.updateSubtaskStatus(6, Status.DONE);

        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getSubtasks());

        taskManager.removeTask(0);
        taskManager.removeEpic(2);

        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getSubtasks());
    }
}
