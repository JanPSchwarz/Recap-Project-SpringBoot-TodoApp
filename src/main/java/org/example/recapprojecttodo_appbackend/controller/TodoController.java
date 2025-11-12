package org.example.recapprojecttodo_appbackend.controller;

import jakarta.validation.Valid;
import org.example.recapprojecttodo_appbackend.models.Todo;
import org.example.recapprojecttodo_appbackend.service.TodoService;
import org.example.recapprojecttodo_appbackend.utils.Status;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @PostMapping("/createExampleTodos")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Todo> createExampleTodos() {
        Todo exampleTodo1 = new Todo("Do Homework", Status.OPEN);
        Todo exampleTodo2 = new Todo("Dump trash", Status.OPEN);
        Todo exampleTodo3 = new Todo("Practice Guitar", Status.IN_PROGRESS);
        Todo exampleTodo4 = new Todo("Swimming training", Status.DONE);

        List<Todo> exampleTodoList = new ArrayList<>(List.of(exampleTodo1, exampleTodo2, exampleTodo3, exampleTodo4));

        for (Todo todo : exampleTodoList) {
            todoService.createTodo(todo);
        }

        return todoService.findAll();
    }

    @GetMapping("/{id}")
    public Todo getTodoById(@PathVariable String id) {
        return todoService.findById(id);
    }

    @GetMapping("/undo")
    public Todo undoTodos() {
        return todoService.undoRedoAction();
    }

    @GetMapping("/redo")
    public Todo redoTodos() {
        return todoService.undoRedoAction();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Todo createTodo(@Valid @RequestBody Todo todo) {
        return todoService.createTodo(todo);
    }

    @PutMapping("/{id}")
    public Todo modifyTodo(@PathVariable String id, @Valid @RequestBody Todo todo) {
        return todoService.updateTodo(id, todo);
    }

    @DeleteMapping("/deleteAll")
    public boolean deleteAllTodos() {
        return todoService.deleteAll();
    }

    @DeleteMapping("/{id}")
    public Todo deleteTodo(@PathVariable String id) {
        return todoService.deleteTodoById(id);
    }
}
