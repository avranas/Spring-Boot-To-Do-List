package com.example.avranas.springBootToDoList;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TodoController {

  private final TodoRepository todoRepository;

  public TodoController(TodoRepository todoRepository) {
    this.todoRepository = todoRepository;
  }

  @GetMapping("/todos")
  public Iterable<Todo> findAllTodos() {
    Iterable<Todo> todos = this.todoRepository.findAll();
    return todos;
  }

  @GetMapping("/todos/{id}")
  public Optional<Todo> getTodo(@PathVariable Integer id) {
    final Optional<Todo> gotTodo = this.todoRepository.findById(id);
    if (gotTodo.isEmpty()) {
      throw new NotFoundException("Todo was not found");
    }
    return gotTodo;
  }

  @PostMapping("/todos")
  public Todo addOneTodo(@RequestBody Todo todo) {
    final LocalDate newDate = java.time.LocalDate.now();
    todo.setCreatedAt(newDate);
    Todo savedTodo = this.todoRepository.save(todo);
    return savedTodo;
  }

  @PatchMapping("/todos/{id}")
  public @ResponseBody void updateTodo(@PathVariable Integer id, @RequestBody Map<String, String> fields) {
    final Optional<Todo> gotTodo = this.todoRepository.findById(id);
    if (gotTodo.isEmpty()) {
      throw new NotFoundException("Todo was not found");
    }
    // Map key is field name, v is value
    fields.forEach((k, v) -> {
      switch (k) {
        case "content":
          gotTodo.get().setContent(v);
          break;
        default:
          break;
      }
    });
    if (gotTodo != null) {
      Todo todo = gotTodo.get();
      final LocalDate newDate = java.time.LocalDate.now();
      todo.setUpdatedAt(newDate);
      this.todoRepository.save(todo);
    }
  }

  @DeleteMapping("/todos/{id}")
  public Optional<Todo> deleteTodo(@PathVariable Integer id) {
    final Optional<Todo> gotTodo = this.todoRepository.findById(id);
    if (gotTodo.isEmpty()) {
      throw new NotFoundException("Todo was not found");
    }
    this.todoRepository.deleteById(id);
    return gotTodo;
  }

}
