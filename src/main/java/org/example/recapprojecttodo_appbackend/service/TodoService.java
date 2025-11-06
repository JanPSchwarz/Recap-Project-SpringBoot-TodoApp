package org.example.recapprojecttodo_appbackend.service;

import org.example.recapprojecttodo_appbackend.dto.TodoDTO;
import org.example.recapprojecttodo_appbackend.models.Todo;
import org.example.recapprojecttodo_appbackend.repository.TodoRepo;
import org.example.recapprojecttodo_appbackend.utils.IdService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {
    private final TodoRepo todoRepo;
    private final IdService idService;

    public TodoService(TodoRepo todoRepo, IdService idService) {
        this.todoRepo = todoRepo;
        this.idService = idService;
    }

    public List<Todo> findAll() {
        return todoRepo.findAll();
    }

    public Todo createTodo(TodoDTO todoDTO) {
        String newId = idService.generateId();
        Todo newTodo = Todo.builder().description(todoDTO.description()).status(todoDTO.status()).id(newId).build();

        todoRepo.save(newTodo);

        return todoRepo.findById(newId).orElseThrow();
    }

    public Todo findById(String id) {
        return todoRepo.findById(id).orElseThrow();
    }

    public Todo updateTodo(String id, TodoDTO todoDTO) {
        Todo updatedTodo = todoRepo.findById(id).orElseThrow();

        updatedTodo = updatedTodo.withDescription(todoDTO.description()).withStatus(todoDTO.status());

        todoRepo.save(updatedTodo);

        return todoRepo.findById(id).orElseThrow();
    }

    public Todo deleteTodoById(String id) {
        Todo deletedTodo = todoRepo.findById(id).orElseThrow();
        todoRepo.deleteById(id);
        
        return deletedTodo;
    }
}
