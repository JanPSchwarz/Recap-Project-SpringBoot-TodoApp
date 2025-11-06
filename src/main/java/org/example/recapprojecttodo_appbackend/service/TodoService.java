package org.example.recapprojecttodo_appbackend.service;

import org.example.recapprojecttodo_appbackend.dto.TodoDTO;
import org.example.recapprojecttodo_appbackend.exceptions.TodoNotFoundException;
import org.example.recapprojecttodo_appbackend.models.Todo;
import org.example.recapprojecttodo_appbackend.repository.TodoRepo;
import org.example.recapprojecttodo_appbackend.utils.IdService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {
    private final TodoRepo todoRepo;
    private final IdService idService;

    private String lastAction;
    private Todo lastUsedTodo;

    public TodoService(TodoRepo todoRepo, IdService idService) {
        this.todoRepo = todoRepo;
        this.idService = idService;
    }

    void setLastAction(String action, Todo todo) {
        lastAction = action;
        lastUsedTodo = todo;
    }

    public List<Todo> findAll() {
        return todoRepo.findAll();
    }

    public Todo createTodo(TodoDTO todoDTO) {
        String newId = idService.generateId();
        Todo createdTodo = Todo.builder().description(todoDTO.description()).status(todoDTO.status()).id(newId).build();

        todoRepo.save(createdTodo);

        setLastAction("createTodo", createdTodo);

        return findById(newId);
    }

    public Todo findById(String id) throws TodoNotFoundException {
        return todoRepo.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
    }

    public Todo updateTodo(String id, TodoDTO todoDTO) {
        Todo outdatedTodoDto = findById(id);

        Todo updatedTodo = outdatedTodoDto.withDescription(todoDTO.description()).withStatus(todoDTO.status());

        todoRepo.save(updatedTodo);

        setLastAction("updateTodo", outdatedTodoDto);

        return findById(id);
    }

    public Todo deleteTodoById(String id) {
        Todo deletedTodo = findById(id);
        todoRepo.deleteById(id);

        setLastAction("deleteTodo", deletedTodo);

        return deletedTodo;
    }

    public Todo undoAction() {
        if (lastAction == null) {
            System.err.println("Undo: invalid action");
            return null;
        }

        switch (lastAction) {
            case "createTodo" -> {
                deleteTodoById(lastUsedTodo.id());
            }
            case "updateTodo" -> {
                TodoDTO todoDTO = new TodoDTO(lastUsedTodo.description(), lastUsedTodo.status());
                updateTodo(lastUsedTodo.id(), todoDTO);
            }
            case "deleteTodo" -> {
                // SKIPPING CREATE METHOD TO KEEP ID OF CURRENT
                todoRepo.save(lastUsedTodo);
                // NORMALLY CALLED IN METHODS
                setLastAction("createTodo", lastUsedTodo);
            }

            default -> System.out.println("Invalid action");
        }

        return lastUsedTodo;
    }
}
