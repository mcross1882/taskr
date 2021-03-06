taskr
=====

A simple API for tracking the progress of your events. Taskr decouples your event management
so multiple resources can interact with it without duplicating code across servers.

### How does it work?

Lets assume we have the following
- `serverA` a backend server that processes jobs
- `serverB` a server that provides a web UI for users

`serverA` can create a `task` and then populate it with `events`. At the same time `serverB` can continuously
poll the current `task` progress. As `serverB` progresses through `events` updates will reflect immediately to
any servers polling the progress.

### API Overview

```
POST /task
```

Generate a new task for storing events. A task will live for 24 hours then it is automatically deleted.

```json
{
    "status": 200,
    "message": "Created new task",
    "data": {
        "id": "c8c9cdc3-c1e1-4f83-b471-06bf49e13406",
        "events": [],
        "current_event": 0
    }
}
```

---

```
DELETE /task/:id
```

Delete a task. A json blob with a 404 message will be returned if the task id does not exist.

```json
{
    "status": 200,
    "message": "Successfully deleted task c8c9cdc3-c1e1-4f83-b471-06bf49e13406",
    "data": null
}
```

---

```
GET /task/:id
```

Get the current progress of the task.

```json
{
    "status": 200,
    "message": "Found task",
    "data": {
        "event": {
            "name": "Event A",
            "description": "1st event"
        },
        "current_event": 2,
        "remaining_events": 6,
        "total_events": 8,
        "progress": 0.25
    }
}
```

---

```
POST /task/:id/event

Query Parameters
name        - The name of the event
description - A simple description of the event
```

Add a new event to a given task. This endpoint requires `name` and `description` be passed in as
query parameters.

```json
{
    "status": 200,
    "message": "Added event to task",
    "data": {
        "id": "c8c9cdc3-c1e1-4f83-b471-06bf49e13406",
        "events": [
            {
                "name": "A new event",
                "description": "Something deep and technical"
            }
        ],
        "current_event": 0
    }
}
```

---

```
POST /task/:id/tick
```

Progress the task to its next event. If there are no new events the last event will be returned.
When executed this endpoint will increase the `currentEvent` property in the task by `1`.

```json
{
    "status": 200,
    "message": "Moved to next event for task",
    "data": {
        "id": "c8c9cdc3-c1e1-4f83-b471-06bf49e13406",
        "events": [
            {
                "name": "A new event",
                "description": "Something deep and technical"
            }
        ],
        "current_event": 1
    }
}
```

### Possible Error Responses

`404` Missing task. This will be returned if a incorrect task id is submitted or a task cannot be found.

```json
{
    "status": 404,
    "message": "Task c8c9cdc3-c1e1-4f83-b471-06bf49e13406 does not exist",
    "data": null
}
```

---

`500` Internal server error. This will be returned if a critical error occurs in the server. To improve debugging
the first 10 lines of the stack trace will be provided as an array of objects in the response.

```json
{
    "status": 500,
    "message": "Something went really wrong",
    "errors": [
        {
            "className": "io.taskr.TaskRegistry",
            "fileName": "TaskRegistry.scala",
            "lineNumber": 50,
            "methodName": "findTask",
            "nativeMethod": false
        }
    ]
}
```
