package org.example.recapprojecttodo_appbackend.controller;

import org.example.recapprojecttodo_appbackend.dto.TodoDTO;
import org.example.recapprojecttodo_appbackend.models.Todo;
import org.example.recapprojecttodo_appbackend.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todo")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<Todo> getAllTodos() {
        return todoService.findAll();
    }

    @GetMapping("/{id}")
    public Todo getTodoById(@PathVariable String id) {
        return todoService.findById(id);
    }

    @GetMapping("/undo")
    public Todo undoTodos() {
        return todoService.undoAction();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Todo createTodo(@RequestBody TodoDTO todoDTO) {
        return todoService.createTodo(todoDTO);
    }

    @PutMapping("/{id}")
    public Todo modifyTodo(@PathVariable String id, @RequestBody TodoDTO todoDTO) {
        return todoService.updateTodo(id, todoDTO);
    }

    @DeleteMapping("/{id}")
    public Todo deleteTodo(@PathVariable String id) {
        return todoService.deleteTodoById(id);
    }
}
