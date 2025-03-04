package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private int availableId = 0;
    private HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    @Override
    public void taskUpdate(int id, Task task) {
        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public boolean isValidTaskId(int id) {
        return tasks.containsKey(id);
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void addTask(Task newTask) {
        int id = generateId();
        newTask.setId(id);
        tasks.put(id, newTask);
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            return null;
        }
        Task snapshot = new Task(task.getTaskName(), task.getDescription(), task.getStatus());
        snapshot.setId(task.getId());
        historyManager.add(snapshot);
        return snapshot;
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void setTaskStatus(int id, Status status) {
        tasks.get(id).setStatus(status);
    }

    @Override
    public void epicUpdate(int id, Epic epic) {
        epic.setId(id);
        epics.put(id, epic);
    }

    @Override
    public boolean isValidEpicId(int id) {
        return epics.containsKey(id);
    }

    @Override
    public void addEpic(Epic newEpic) {
        int id = generateId();
        newEpic.setId(id);
        epics.put(id, newEpic);
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return null;
        }
        Epic snapshot = new Epic(epic.getTaskName(), epic.getDescription(), epic.getStatus(), epic.getSubtaskIds());
        snapshot.setId(epic.getId());
        historyManager.add(snapshot);
        return snapshot;
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (int subId : epic.getSubtaskIds()) {
                subtasks.remove(subId);
            }
            epics.remove(id);
        }
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        removeAllSubtasks();
    }

    @Override
    public void subtaskUpdate(int id, Subtask subtask) {
        subtask.setId(id);
        subtasks.put(id, subtask);
        updateEpicStatus(epics.get(subtask.getParentEpicId()));
    }

    @Override
    public boolean isValidSubtaskId(int id) {
        return subtasks.containsKey(id);
    }

    @Override
    public ArrayList<Subtask> getSubtasksByParentId(int parentId) {
        ArrayList<Subtask> subtasksById = new ArrayList<>();
        Epic epic = epics.get(parentId);
        if (epic != null) {
            for (Integer id : epic.getSubtaskIds()) {
                subtasksById.add(subtasks.get(id));
            }
        }
        return new ArrayList<>(subtasksById);
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void addSubtask(Subtask newSubtask) {
        int id = generateId();
        if (newSubtask.getParentEpicId().equals(id)) {
            throw new IllegalArgumentException("Subtask cannot be its own epic");
        }
        newSubtask.setId(id);
        subtasks.put(id, newSubtask);
        Epic parentEpic = epics.get(newSubtask.getParentEpicId());
        if (parentEpic != null) {
            parentEpic.addSubtask(id);
            updateEpicStatus(parentEpic);
        }
    }

    @Override
    public void updateSubtaskStatus(int id, Status status) {
        setSubtaskStatus(id, status);
    }

    private void setSubtaskStatus(int id, Status status) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            subtask.setStatus(status);
            updateEpicStatus(epics.get(subtask.getParentEpicId()));
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return null;
        }
        Subtask snapshot = new Subtask(subtask.getParentEpicId(), subtask.getTaskName(), subtask.getDescription(), subtask.getStatus());
        snapshot.setId(subtask.getId());
        historyManager.add(snapshot);
        return snapshot;
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            int parentId = subtask.getParentEpicId();
            subtasks.remove(id);
            subtask.setId(-1);
            Epic parentEpic = epics.get(parentId);
            if (parentEpic != null) {
                parentEpic.getSubtaskIds().remove(Integer.valueOf(id));
                updateEpicStatus(parentEpic);
            }
        }
    }

    @Override
    public void removeAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            Epic epicToUpdate = epics.get(subtask.getParentEpicId());
            if (epicToUpdate != null) {
                epicToUpdate.removeAllSubtasks();
                updateEpicStatus(epicToUpdate);
            }
        }
        subtasks.clear();
    }

    private void updateEpicStatus(Epic epic) {
        if (epic == null) {
            return;
        }
        boolean done = true;
        boolean checkNew = true;
        for (int id : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(id);
            if (subtask == null) continue;
            Status status = subtask.getStatus();
            if (status == Status.IN_PROGRESS) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
            if (status != Status.DONE) {
                done = false;
            } else {
                checkNew = false;
            }
        }
        if (done && !epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(Status.DONE);
        } else if (checkNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private int generateId() {
        while (tasks.containsKey(availableId) || subtasks.containsKey(availableId) || epics.containsKey(availableId)) {
            availableId++;
        }
        return availableId++;
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
