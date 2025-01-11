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

    public void printTasks() {
        for (Task task : tasks.values()) {
            System.out.println(task);
        }
    }

    public void addTask(String taskName, String description) {
        tasks.put(availableId, new Task(taskName, description, availableId));
        availableId++;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public Task getTaskById(int id) {
        return tasks.getOrDefault(id, null);
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    //epic
    public void epicUpdate(int id, Epic epic) {
        epics.put(id, epic);
    }

    public boolean isValidEpicId(int id) {
        return epics.containsKey(id);
    }

    public void printEpics() {
        for (Epic epic : epics.values()) {
            System.out.println(epic);
        }
    }

    public void addEpic(String taskName, String description) {
        epics.put(availableId, new Epic(taskName, description, availableId));
        availableId++;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void removeEpic(int id) {
        epics.remove(id);
    }

    public void removeAllEpics() {
        epics.clear();
    }

    //subtask
    public void subtaskUpdate(int id, Subtask subtask) {
        subtasks.put(id, subtask);
    }

    public boolean isValidSubtaskId(int id) {
        return subtasks.containsKey(id);
    }

    public void printSubtasksByParentId(int parentId) {
        epics.get(parentId).printSubtasks();
    }

    public void printSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            System.out.println(subtask.toString());
        }
    }

    public void addSubtask(String taskName, String description, int parentId) {
        subtasks.put(availableId, new Subtask(epics.get(parentId), taskName, description, availableId));
        epics.get(parentId).addSubtask(subtasks.get(availableId));
        availableId++;
        updateEpicStatus(epics.get(parentId));
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void removeSubtask(int id) {
        updateEpicStatus(subtasks.get(id).getParentEpic());
        subtasks.remove(id);
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    public void updateEpicStatus(Epic epic) {
        if (epic.getSubtasks() != null) {
            boolean done = true;
            boolean inProgress = true;
            for (Subtask sub : epic.getSubtasks()) {
                if (sub.getStatus() != Status.DONE) {
                    done = false;
                }
                if (sub.getStatus() != Status.IN_PROGRESS || sub.getStatus() != Status.DONE) {
                    inProgress = false;
                }
            }
            if (done) {
                epic.setStatus(Status.DONE);
            } else if (inProgress) {
                epic.setStatus(Status.IN_PROGRESS);
            } else {
                epic.setStatus(Status.NEW);
            }
        } else {
            epic.setStatus(Status.NEW);
        }
    }
    //all

    public void removeAllTasks() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }
}