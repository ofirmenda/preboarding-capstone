package com.example.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class TaskController {
   private final List<Task> tasks = new ArrayList<>(); 
   Long counter = (long) 0;

  @GetMapping("/tasks")
  public List<Task> getTasks(@RequestParam(required = false) Boolean done) {
    if (done == null) {
      return tasks;
    }
    return tasks.stream()
        .filter(task -> Objects.equals(task.done(), done))
        .toList();
  }

  @GetMapping("/tasks/{id}")
  public Task getTask(@PathVariable long id) {
    Task result = tasks.stream()
        .filter(name -> name.id()== id)
        .findFirst()
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    return result;
  }

  @PostMapping("/tasks")
  @ResponseStatus(HttpStatus.CREATED)
  public Task createTask(@RequestBody Task task) {
    if (task.title() == null || task.title().isBlank()) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title required");
    }
    Task newTask = new Task(counter++,task.title(),task.done() != null ? task.done() : false);
    tasks.add(newTask);
    return newTask;
  }

  @PutMapping("/tasks/{id}")
  public Task updateTask(@PathVariable long id, @RequestBody Task task) {
    if (task.title() == null || task.title().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title required");
    }
    for (int i = 0; i < tasks.size(); i++) {
      Task existing = tasks.get(i);
      if (existing.id() == id) {
        Boolean updatedDone = task.done() != null ? task.done() : existing.done();
        Task updatedTask = new Task(id, task.title(), updatedDone);
        tasks.set(i, updatedTask);
        return updatedTask;
      }
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
  }

  @DeleteMapping("/tasks/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void task(@PathVariable long id) {
    tasks.removeIf(task -> task.id()== id);
  }

}
