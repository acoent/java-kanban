package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private static int availableId = 0;

    //task
    public void taskUpdate(int id, Task task) {
        tasks.put(id, task);
    }

    public boolean isValidTaskId(int id) {
        return tasks.containsKey(id);
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void addTask(Task newTask) {
        int id = generateId();
        newTask.setId(id);
        tasks.put(id, newTask);
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }
    public void setTaskStatus(int id, Status status) {
        tasks.get(id).setStatus(status);
    }
    //epic
    public void epicUpdate(int id, Epic epic) {
        epics.put(id, epic);
    }

    public boolean isValidEpicId(int id) {
        return epics.containsKey(id);
    }

    public void addEpic(Epic newEpic) {
        int id = generateId();
        newEpic.setId(id);
        epics.put(id, newEpic);
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void removeEpic(int id) {
        for (int subId : (epics.get(id).getSubtaskIds())) {
            subtasks.remove(subId);
        }
        epics.remove(id);
    }

    public void removeAllEpics() {
        epics.clear();
    }
    //subtask
    public void subtaskUpdate(int id, Subtask subtask) {
        subtasks.put(id, subtask);
        updateEpicStatus(epics.get(subtask.getParentEpicId()));
    }

    public boolean isValidSubtaskId(int id) {
        return subtasks.containsKey(id);
    }

    public ArrayList<Subtask> getSubtasksByParentId(int parentId) {
        ArrayList<Subtask> subtasksById = new ArrayList<>();
        for (Integer id : epics.get(parentId).getSubtaskIds()) {
            subtasksById.add(subtasks.get(id));
        }
        return subtasksById;
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void addSubtask(Subtask newSubtask) {
        int id = generateId();
        newSubtask.setId(id);
        subtasks.put(id, newSubtask);
        epics.get(newSubtask.getParentEpicId()).addSubtask(id);
        updateEpicStatus(epics.get(newSubtask.getParentEpicId()));

    }
    public void setSubtaskStatus(int id, Status status) {
        subtasks.get(id).setStatus(status);
        updateEpicStatus(epics.get(subtasks.get(id).getParentEpicId()));
    }
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void removeSubtask(int id) {
        int idToUpdate = subtasks.get(id).getParentEpicId();
        subtasks.remove(id);
        updateEpicStatus(epics.get(idToUpdate));

    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    private void updateEpicStatus(Epic epic) {
        boolean done = true;
        boolean checkNew = true;
        for (int id : epic.getSubtaskIds()) {
            Status status = subtasks.get(id).getStatus();
            if (status == Status.IN_PROGRESS) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
            if (status != Status.DONE) {
                done = false;
            } else{
                checkNew = false;
            }
        }
        if (done) {
            epic.setStatus(Status.DONE);
        } else if (checkNew){
            epic.setStatus(Status.NEW);
        } else{
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
//all

    private int generateId() {
        while (tasks.containsKey(availableId) || subtasks.containsKey(availableId) || epics.containsKey(availableId)) {
            availableId++;
        }
        return availableId++;
    }

    public void removeAllTasks() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }
}