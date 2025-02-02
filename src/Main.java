import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.InMemoryTaskManager;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager TaskManager = new InMemoryTaskManager();

        Task task1 = new Task("Task1", "Description1");
        Task task2 = new Task("Task2", "Description2");
        Epic epic1 = new Epic("Epic1", "Description1");
        Epic epic2 = new Epic("Epic2", "Description2");
        Subtask subtask1 = new Subtask(2, "sub1", "SubDescription1");
        Subtask subtask2 = new Subtask(2, "sub2", "SubDescription2");
        Subtask subtask3 = new Subtask(3, "sub3", "SubDescription3");

        TaskManager.addTask(task1);
        TaskManager.addTask(task2);
        TaskManager.addEpic(epic1);
        TaskManager.addEpic(epic2);
        TaskManager.addSubtask(subtask1);
        TaskManager.addSubtask(subtask2);
        TaskManager.addSubtask(subtask3);

        System.out.println(TaskManager.getEpics());
        System.out.println(TaskManager.getTasks());
        System.out.println(TaskManager.getSubtasks());

        TaskManager.setTaskStatus(0, Status.IN_PROGRESS);
        TaskManager.setTaskStatus(1, Status.DONE);
        TaskManager.updateSubtaskStatus(4, Status.DONE);
        TaskManager.updateSubtaskStatus(5, Status.IN_PROGRESS);
        TaskManager.updateSubtaskStatus(6, Status.DONE);

        System.out.println(TaskManager.getEpics());
        System.out.println(TaskManager.getTasks());
        System.out.println(TaskManager.getSubtasks());

        TaskManager.removeTask(0);
        TaskManager.removeEpic(2);

        System.out.println(TaskManager.getEpics());
        System.out.println(TaskManager.getTasks());
        System.out.println(TaskManager.getSubtasks());
    }
}
