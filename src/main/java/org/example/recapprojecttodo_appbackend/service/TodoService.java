package org.example.recapprojecttodo_appbackend.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.recapprojecttodo_appbackend.exceptions.TodoNotFoundException;
import org.example.recapprojecttodo_appbackend.exceptions.UndoNotPossibleException;
import org.example.recapprojecttodo_appbackend.models.Todo;
import org.example.recapprojecttodo_appbackend.repository.TodoRepo;
import org.example.recapprojecttodo_appbackend.utils.IdService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {
    private final TodoRepo todoRepo;
    private final IdService idService;
    private final OpenAiService openAiService;

    private String lastAction;
    private Todo lastUsedTodo;

    public TodoService(TodoRepo todoRepo, IdService idService, OpenAiService openAiService) {
        this.todoRepo = todoRepo;
        this.idService = idService;
        this.openAiService = openAiService;
    }

    boolean setLastAction(@NotBlank String action, @NotNull Todo todo) {
        if (action.isEmpty() || todo == null) {
            throw new IllegalArgumentException("Invalid arguments: ");
        }

        lastAction = action;
        lastUsedTodo = todo;

        return true;
    }

    public List<Todo> findAll() {
        return todoRepo.findAll();
    }

    public Todo createTodo(Todo todo) {
        String newId = idService.generateId();

        String grammarCheckedDescription = openAiService.checkGrammar(todo.description());

        Todo createdTodo = Todo.builder().description(grammarCheckedDescription).status(todo.status()).id(newId).build();
        
        todoRepo.save(createdTodo);

        setLastAction("createTodo", createdTodo);

        return findById(newId);
    }

    public Todo findById(String id) throws TodoNotFoundException {
        return todoRepo.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
    }

    public Todo updateTodo(String id, Todo todo) {
        Todo outdatedTodo = findById(id);

        Todo updatedTodo = outdatedTodo.withDescription(todo.description()).withStatus(todo.status());

        todoRepo.save(updatedTodo);

        setLastAction("updateTodo", outdatedTodo);

        return findById(id);
    }

    public Todo deleteTodoById(String id) {
        Todo deletedTodo = findById(id);
        todoRepo.deleteById(id);

        setLastAction("deleteTodo", deletedTodo);

        return deletedTodo;
    }

    public Todo undoRedoAction() {
        if (lastAction == null) {
            throw new UndoNotPossibleException("Undo not possible");
        }

        switch (lastAction) {
            case "createTodo" -> {
                deleteTodoById(lastUsedTodo.id());
            }
            case "updateTodo" -> {
                Todo todo = new Todo(lastUsedTodo.description(), lastUsedTodo.status());
                updateTodo(lastUsedTodo.id(), todo);
            }
            case "deleteTodo" -> {
                // SKIPPING CREATE METHOD TO KEEP ID OF CURRENT
                todoRepo.save(lastUsedTodo);
                // NORMALLY CALLED IN METHODS
                setLastAction("createTodo", lastUsedTodo);
            }

            default -> throw new UndoNotPossibleException("Undo not possible");
        }

        return lastUsedTodo;
    }

    public boolean deleteAll() {
        todoRepo.deleteAll();
        return true;
    }
}
