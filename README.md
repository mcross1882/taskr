taskr
=====

A simple API for tracking the progress of your events. Taskr decouples your event management
so multiple resources can interact with it without duplicating code across servers.

### API Overview

```
PUT /task/new
```

Generate a new task for storing events. A task will live for 24 hours then it is automatically deleted.

```json
{
    "id": "c8c9cdc3-c1e1-4f83-b471-06bf49e13406",
    "events": [],
    "currentEvent": 0
}
```

```
DELETE /task/:id/delete
```

Delete a task. A json blob with a 404 message will be returned if the task id does not exist.

```json
{
    "status": 200,
    "message": "Successfully deleted task c8c9cdc3-c1e1-4f83-b471-06bf49e13406"
}
```

```
GET /task/:id/progress
```

Get the current progress of the task.

```json
{
    "event": {
        "name": "Event A",
        "description": "1st event"
    },
    "current_event": 2,
    "remainingEvents": 6,
    "total_events": 8,
    "progress": 0.25
}
```

```
PUT /task/:id/event/new

Query Parameters
name        - The name of the event
description - A simple description of the event
```

Add a new event to a given task. This endpoint requires `name` and `description` be passed in as
query parameters.

```json
{
    "id": "c8c9cdc3-c1e1-4f83-b471-06bf49e13406",
    "events": [
        "name": "A new event",
        "description": "Something deep and technical"
    ],
    "currentEvent": 0
}
```

```
POST /task/:id/event/tick
```

Progress the task to its next event. If there are no new events the last event will be returned.
When executed this endpoint will increase the `currentEvent` property in the task by `1`.

```json
{
    "id": "c8c9cdc3-c1e1-4f83-b471-06bf49e13406",
    "events": [
        "name": "A new event",
        "description": "Something deep and technical"
    ],
    "currentEvent": 1
}
```

### Possible Error Responses

`404` Missing task. This will be returned if a incorrect task id is submitted or a task cannot be found.

```json
{
    "status": 404,
    "message": "Task c8c9cdc3-c1e1-4f83-b471-06bf49e13406 does not exist"
}
```