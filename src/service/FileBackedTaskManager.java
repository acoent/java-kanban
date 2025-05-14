package service;

import exception.ManagerSaveException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;
    private static final String CSV_HEADER = "id,type,name,status,description,epic";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }


    public void save() throws ManagerSaveException {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write(CSV_HEADER + "\n");
            for (Task task : getTasks()) {
                writer.write(Converter.toString(task) + "\n");
            }
            for (Epic epic : getEpics()) {
                writer.write(Converter.toString(epic) + "\n");
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(Converter.toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            String line;
            reader.readLine();
            int id = 0;
            while ((line = reader.readLine()) != null) {
                Task task = Converter.fromString(line);
                int taskId = task.getId();
                if (taskId > id) {
                    id = taskId;
                }
                if (task instanceof Epic) {
                    manager.epics.put(taskId, (Epic) task);
                } else if (task instanceof Subtask) {
                    manager.subtasks.put(taskId, (Subtask) task);
                } else {
                    manager.tasks.put(taskId, task);
                }
            }
            manager.availableId = id + 1;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке данных из файла", e);
        }

        return manager;
    }

    @Override
    public void taskUpdate(int id, Task task) {
        super.taskUpdate(id, task);
        save();
    }

    @Override
    public void addTask(Task newTask) {
        super.addTask(newTask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void setTaskStatus(int id, Status status) {
        super.setTaskStatus(id, status);
        save();
    }

    @Override
    public void epicUpdate(int id, Epic epic) {
        super.epicUpdate(id, epic);
        save();
    }

    @Override
    public void addEpic(Epic newEpic) {
        super.addEpic(newEpic);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void subtaskUpdate(int id, Subtask subtask) {
        super.subtaskUpdate(id, subtask);
        save();
    }

    @Override
    public void addSubtask(Subtask newSubtask) {
        super.addSubtask(newSubtask);
        save();
    }

    @Override
    public void updateSubtaskStatus(int id, Status status) {
        super.updateSubtaskStatus(id, status);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }
}
