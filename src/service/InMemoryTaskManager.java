package service;

import exception.TimeIntersectionException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Subtask> subtasks;
    protected final HashMap<Integer, Epic> epics;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int availableId = 0;
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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
        if (hasTimeOverlap(newTask)) throw new TimeIntersectionException();
        int id = generateId();
        newTask.setId(id);
        tasks.put(id, newTask);
        if (newTask.getStartTime() != null) {
            prioritizedTasks.add(newTask);
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new IllegalArgumentException("Задача с таким ID не найдена");
        }
        Task snapshot = new Task(task.getTaskName(), task.getDescription(), task.getStatus(), task.getDuration(), task.getStartTime());
        snapshot.setId(task.getId());
        historyManager.add(snapshot);
        return snapshot;
    }

    @Override
    public void removeTask(int id) {
        Task removed = tasks.remove(id);
        if (removed != null) {
            prioritizedTasks.remove(removed);
        }
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
            throw new IllegalArgumentException("Эпик с таким ID не найден");
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
        Epic epic = epics.get(parentId);
        return epic == null ? new ArrayList<>() : epic.getSubtaskIds().stream().map(subtasks::get).filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void addSubtask(Subtask newSubtask) {
        if (hasTimeOverlap(newSubtask)) {
            throw new TimeIntersectionException();
        }
        int id = generateId();
        newSubtask.setId(id);
        subtasks.put(id, newSubtask);
        if (newSubtask.getStartTime() != null) {
            prioritizedTasks.add(newSubtask);
        }
        Epic parentEpic = epics.get(newSubtask.getParentEpicId());
        if (parentEpic != null) {
            parentEpic.addSubtask(id);
            updateEpicStatus(parentEpic);
            updateEpicTime(parentEpic);
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
            throw new IllegalArgumentException("Подзадача с таким ID не найдена");
        }
        Subtask snapshot = new Subtask(subtask.getParentEpicId(), subtask.getTaskName(), subtask.getDescription(), subtask.getStatus(), subtask.getDuration(), subtask.getStartTime());
        snapshot.setId(subtask.getId());
        historyManager.add(snapshot);
        return snapshot;
    }


    @Override
    public void removeSubtask(int id) {
        Subtask removed = subtasks.remove(id);
        if (removed != null) {
            prioritizedTasks.remove(removed);
        }
        assert removed != null;
        Epic parentEpic = epics.get(removed.getParentEpicId());
        if (parentEpic != null) {
            parentEpic.getSubtaskIds().remove(Integer.valueOf(id));
            updateEpicStatus(parentEpic);
            updateEpicTime(parentEpic);
        }
    }

    @Override
    public void removeAllSubtasks() {
        epics.values().forEach(epic -> {
            epic.removeAllSubtasks();
            updateEpicStatus(epic);
            updateEpicTime(epic);
        });
        subtasks.clear();
    }

    private void updateEpicTime(Epic epic) {
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            epic.setEndTime(null);
            return;
        }

        Optional<LocalDateTime> earliestStart = epic.getSubtaskIds().stream().map(this::getSubtaskById).filter(Objects::nonNull).map(Subtask::getStartTime).filter(Objects::nonNull).min(LocalDateTime::compareTo);

        Optional<LocalDateTime> latestEnd = epic.getSubtaskIds().stream().map(this::getSubtaskById).filter(Objects::nonNull).map(Subtask::getEndTime).filter(Objects::nonNull).max(LocalDateTime::compareTo);

        if (earliestStart.isPresent() && latestEnd.isPresent()) {
            epic.setStartTime(earliestStart.get());
            epic.setDuration(Duration.between(earliestStart.get(), latestEnd.get()));
            epic.setEndTime(latestEnd.get());
        } else {
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            epic.setEndTime(null);
        }
    }


    private void updateEpicStatus(Epic epic) {
        if (epic == null) {
            return;
        }
        List<Subtask> subtasksList = epic.getSubtaskIds().stream().map(subtasks::get).filter(Objects::nonNull).toList();

        if (subtasksList.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allDone = subtasksList.stream().allMatch(subtask -> subtask.getStatus() == Status.DONE);
        boolean allNew = subtasksList.stream().allMatch(subtask -> subtask.getStatus() == Status.NEW);

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    protected int generateId() {
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

    private boolean hasTimeOverlap(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getEndTime() == null) {
            return false;
        }
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        allTasks.addAll(subtasks.values());
        return allTasks.stream().filter(existingTask -> existingTask.getStartTime() != null && existingTask.getEndTime() != null).anyMatch(existingTask -> {
            LocalDateTime newStart = newTask.getStartTime();
            LocalDateTime newEnd = newTask.getEndTime();
            LocalDateTime existingStart = existingTask.getStartTime();
            LocalDateTime existingEnd = existingTask.getEndTime();
            return !(newEnd.isBefore(existingStart) || newStart.isAfter(existingEnd));
        });
    }

}
