# java-kanban

📌 Overview

The Kanban Task Manager is a Java application for organizing and tracking tasks using a simplified Kanban methodology. It supports standard Tasks, Epics (large tasks), and Subtasks (associated with Epics). It includes task prioritization, history tracking, and an HTTP API for remote access and control.




📦 Architecture

Core Components

TaskManager Interface
Defines the common operations for managing tasks, epics, and subtasks.

InMemoryTaskManager
Implements TaskManager, storing all tasks in memory using hash maps and a TreeSet for prioritization.

FileBackedTaskManager
Extends InMemoryTaskManager with persistent storage using CSV files.

HistoryManager
Keeps a record of recently accessed tasks, implemented with a custom doubly-linked list.

HttpTaskServer
RESTful API server for interacting with tasks over HTTP.



📂 Data Models

Task

public class Task {
    int id;
    String title;
    String description;
    TaskStatus status;
    LocalDateTime startTime;
    Duration duration;
}

Epic

public class Epic extends Task {
    List<Integer> subtaskIds;
}

Subtask

public class Subtask extends Task {
    int epicId;
}

Enums

TaskType: TASK, EPIC, SUBTASK

TaskStatus: NEW, IN_PROGRESS, DONE





⏳ Prioritization

Tasks are stored in a TreeSet sorted by start time and ID to ensure consistent order and prevent overlapping schedules.




🕓 History Tracking

Each time a task is accessed, it is added to the history list.

The system prevents duplicate entries.

Implemented using a custom doubly-linked list with a hash map for fast lookup/removal.





🌐 HTTP API

Server

Implemented via HttpServer (Java built-in), listening on a predefined port.

Endpoints

Tasks

GET /tasks/task – Get all tasks

POST /tasks/task – Add or update a task

GET /tasks/task?id={id} – Get a specific task

DELETE /tasks/task?id={id} – Delete a specific task


Epics

GET /tasks/epic

POST /tasks/epic

GET /tasks/epic?id={id}

DELETE /tasks/epic?id={id}


Subtasks

GET /tasks/subtask

POST /tasks/subtask

GET /tasks/subtask?id={id}

DELETE /tasks/subtask?id={id}

GET /tasks/subtask/epic?id={epicId} – Get all subtasks of an epic


Other

GET /tasks – Get all prioritized tasks

GET /tasks/history – Get access history





🧪 Testing

Unit tests verify:

Task creation and updates

Epic/subtask relationships and status propagation

History list behavior

Prioritization rules

HTTP API behavior (GET, POST, DELETE)