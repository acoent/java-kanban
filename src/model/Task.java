package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private final String description;
    private final String taskName;
    private int id;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String taskName, String description, Duration duration, LocalDateTime startTime) {
        this.taskName = taskName;
        this.description = description;
        status = Status.NEW;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String taskName, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.taskName = taskName;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return (startTime != null) ? startTime.plus(duration) : null;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getTaskName() {
        return taskName;
    }

    public Type getType() {
        return Type.TASK;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(description, task.description) && Objects.equals(taskName, task.taskName) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, taskName, id, status);
    }

    @Override
    public String toString() {
        return "Task{" + "taskName='" + taskName + '\'' + ", description='" + description + '\'' + ", id=" + id + ", status=" + status + '}';
    }
}
