package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;

public interface TaskManager {
    void taskUpdate(int id, Task task);

    boolean isValidTaskId(int id);

    ArrayList<Task> getTasks();

    void addTask(Task newTask);

    Task getTaskById(int id);

    void removeTask(int id);

    void setTaskStatus(int id, Status status);

    void epicUpdate(int id, Epic epic);

    boolean isValidEpicId(int id);

    void addEpic(Epic newEpic);

    ArrayList<Epic> getEpics();

    Epic getEpicById(int id);

    void removeEpic(int id);

    void removeAllEpics();

    void subtaskUpdate(int id, Subtask subtask);

    boolean isValidSubtaskId(int id);

    ArrayList<Subtask> getSubtasksByParentId(int parentId);

    ArrayList<Subtask> getSubtasks();

    void addSubtask(Subtask newSubtask);

    void updateSubtaskStatus(int id, Status status);

    Subtask getSubtaskById(int id);

    void removeSubtask(int id);

    void removeAllSubtasks();

    void removeAllTasks();

}
