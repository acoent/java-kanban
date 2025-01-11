import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("""
                    Выберите действие:
                    1 - Добавить задачу.
                    2 - Добавить эпик.
                    3 - Добавить подзадачу.
                    4 - Получение списка задач.
                    5 - Получение списка эпиков.
                    6 - Получение списка подзадач.
                    7 - Получение задачи по ID.
                    8 - Получение эпика по ID.
                    9 - Получение подзадачи по ID.
                    10 - Получение подзадач по ID эпика.
                    11 - Обновить задачу по ID.
                    12 - Обновить эпик по ID.
                    13 - Обновить подзадачу по ID.
                    14 - Удаление всех задач.
                    15 - Удаление всех эпиков.
                    16 - Удаление всех подзадач.
                    17 - Удаление задач по ID.
                    18 - Удаление эпиков по ID.
                    19 - Удаление подзадач по ID.
                    20 - Удаление всех задач, подзадач и эпиков.
                    21 - Завершить работу.""");
            String command = scanner.nextLine();
            switch (command) {
                case "1"://Добавить задачу.
                    System.out.println("Введите название задачи:");
                    String tName = scanner.nextLine();
                    System.out.print("Введите описание задачи:");
                    taskManager.addTask(tName, scanner.nextLine());
                    break;
                case "2"://Добавить эпик.
                    System.out.println("Введите название эпика:");
                    String eName = scanner.nextLine();
                    System.out.print("Введите описание эпика:");
                    taskManager.addEpic(eName, scanner.nextLine());
                    break;
                case "3"://Добавить подзадачу.
                    System.out.println("Введите название подзадачи:");
                    String sName = scanner.nextLine();
                    System.out.print("Введите описание подзадачи:");
                    String sDescription = scanner.nextLine();
                    System.out.println("Введите ID родителя подзадачи:");
                    int parentId = Integer.parseInt(scanner.nextLine());
                    if (taskManager.isValidEpicId(parentId)) {
                        taskManager.addSubtask(sName, sDescription, parentId);
                    } else {
                        System.out.println("Указан неверный ID.");
                    }
                    break;
                case "4"://Получение списка задач.
                    if (!taskManager.getSubtasks().isEmpty()) {
                        taskManager.printTasks();
                    } else {
                        System.out.println("Задач не найдено.");
                    }
                    break;
                case "5"://Получение списка эпиков.
                    if (!taskManager.getEpics().isEmpty()) {
                        taskManager.printEpics();
                    } else {
                        System.out.println("Эпиков не найденно.");
                    }
                    break;
                case "6"://Получение списка подзадач.
                    if (!taskManager.getSubtasks().isEmpty()) {
                        taskManager.printSubtasks();
                    } else {
                        System.out.println("Подзадач не найденно.");
                    }
                    break;
                case "7"://Получение задачи по ID.
                    System.out.println("Введите ID задачи:");
                    Task taskById = taskManager.getTaskById(Integer.parseInt(scanner.nextLine()));
                    System.out.println("Задача с указанным ID: " + (taskById != null ? taskById : "не найдена."));
                    break;
                case "8"://Получение эпика по ID.
                    System.out.println("Введите ID эпика:");
                    Epic epicById = taskManager.getEpicById(Integer.parseInt(scanner.nextLine()));
                    System.out.println("Эпик с указанным ID: " + (epicById != null ? epicById : "не найден."));
                    break;
                case "9"://Получение подзадачи по ID.
                    System.out.println("Введите ID подзадачи:");
                    Subtask subtaskById = taskManager.getSubtaskById(Integer.parseInt(scanner.nextLine()));
                    System.out.println("Подзадача с указанным ID: " + (subtaskById != null ? subtaskById : "не найдена."));
                    break;
                case "10"://Получение подзадач по ID эпика.
                    System.out.println("Введите ID эпика для получения подзадач:");
                    int id = Integer.parseInt(scanner.nextLine());
                    if (taskManager.isValidEpicId(id)) {
                        System.out.println("Подзадачи для эпика " + id);
                        taskManager.printSubtasksByParentId(id);
                    } else {
                        System.out.println("Эпик с указанным ID не найден.");
                    }
                    break;
                case "11"://Обновить задачу по ID.
                    System.out.println("Введите ID задачи для обновления:");
                    int taskUpdateId = Integer.parseInt(scanner.nextLine());
                    if (taskManager.isValidTaskId(taskUpdateId)) {
                        System.out.println("Введите название задачи:");
                        String tUpdName = scanner.nextLine();
                        System.out.print("Введите описание задачи:");
                        String tUpdDescription = scanner.nextLine();
                        taskManager.taskUpdate(taskUpdateId, new Task(tUpdName, tUpdDescription, taskUpdateId));
                    } else {
                        System.out.println("Указан неверный ID.");
                    }
                    break;
                case "12"://Обновить эпик по ID.
                    System.out.println("Введите ID эпика для обновления:");
                    int epicUpdateId = Integer.parseInt(scanner.nextLine());
                    if (taskManager.isValidEpicId(epicUpdateId)) {
                        System.out.println("Введите название эпика:");
                        String eUpdName = scanner.nextLine();
                        System.out.print("Введите описание эпика:");
                        String eUpdDescription = scanner.nextLine();
                        taskManager.epicUpdate(epicUpdateId, new Epic(eUpdName, eUpdDescription, epicUpdateId));
                    } else {
                        System.out.println("Указан неверный ID.");
                    }
                    break;
                case "13"://Обновить подзадачу по ID.
                    System.out.println("Введите ID подзадачи для обновления:");
                    int subUpdateId = Integer.parseInt(scanner.nextLine());
                    if (taskManager.isValidSubtaskId(subUpdateId)) {
                        System.out.println("Введите название подзадачи:");
                        String sUpdName = scanner.nextLine();
                        System.out.print("Введите описание подзадачи:");
                        String sUpdDescription = scanner.nextLine();
                        System.out.println("Введите ID родителя:");
                        int uParentId = Integer.parseInt(scanner.nextLine());
                        if (taskManager.isValidEpicId(uParentId)) {
                            taskManager.subtaskUpdate(subUpdateId, new Subtask(taskManager.getEpicById(uParentId), sUpdName, sUpdDescription, subUpdateId));
                        } else {
                            System.out.println("Указан неверный ID.");
                        }
                    } else {
                        System.out.println("Указан неверный ID.");
                    }
                    break;
                case "14"://Удаление всех задач.
                    taskManager.removeAllTasks();
                    System.out.println("Все задачи удалены.");
                    break;
                case "15"://Удаление всех эпиков.
                    taskManager.removeAllEpics();
                    System.out.println("Все эпики удалены.");
                    break;
                case "16"://Удаление всех подзадач.
                    taskManager.removeAllSubtasks();
                    System.out.println("Все подзадачи удалены.");
                    break;
                case "17"://Удаление задач по ID.
                    System.out.println("Введите ID задачи для удаления:");
                    int taskIdToRemove = Integer.parseInt(scanner.nextLine());
                    if (taskManager.isValidTaskId(taskIdToRemove)) {
                        taskManager.removeTask(taskIdToRemove);
                    } else {
                        System.out.println("Указан неверный ID.");
                    }
                    break;
                case "18"://Удаление эпиков по ID.
                    System.out.println("Введите ID эпика для удаления:");
                    int epicIdToRemove = Integer.parseInt(scanner.nextLine());
                    if (taskManager.isValidEpicId(epicIdToRemove)) {
                        taskManager.removeEpic(epicIdToRemove);
                    } else {
                        System.out.println("Указан неверный ID.");
                    }
                    break;
                case "19"://Удаление подзадач по ID.
                    System.out.println("Введите ID подзадачи для удаления:");
                    int subIdToRemove = Integer.parseInt(scanner.nextLine());
                    if (taskManager.isValidSubtaskId(subIdToRemove)) {
                        taskManager.removeSubtask(subIdToRemove);
                    } else {
                        System.out.println("Указан неверный ID.");
                    }
                    break;
                case "20"://Удаление всех задач, подзадач и эпиков.
                    taskManager.removeAllTasks();
                    System.out.println("Все задачи, подзадачи и эпики удалены.");
                    break;
                case "21"://Завершить работу.
                    return;
                default:
                    System.out.println("Неверная комманда");
            }
        }
    }
}
