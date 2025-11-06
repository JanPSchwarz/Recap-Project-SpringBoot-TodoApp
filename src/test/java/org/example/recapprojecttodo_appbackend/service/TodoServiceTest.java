package org.example.recapprojecttodo_appbackend.service;

import org.example.recapprojecttodo_appbackend.dto.TodoDTO;
import org.example.recapprojecttodo_appbackend.models.Todo;
import org.example.recapprojecttodo_appbackend.repository.TodoRepo;
import org.example.recapprojecttodo_appbackend.utils.IdService;
import org.example.recapprojecttodo_appbackend.utils.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TodoServiceTest {

    TodoRepo mockTodoRepo;
    IdService mockIdService;

    TodoService todoService;

    @BeforeEach
    void setUp() {
        mockTodoRepo = Mockito.mock(TodoRepo.class);
        mockIdService = Mockito.mock(IdService.class);

        // ADDING ONE TODO_ BEFOREHAND
        Todo testTodo = new Todo("1", "This is a test", Status.OPEN);
        mockTodoRepo.save(testTodo);

        todoService = new TodoService(mockTodoRepo, mockIdService);
    }


    @Test
    void findAll_shouldReturnTrue_whenCorrectListReturned() {
        Todo expectedTodo = new Todo("1", "This is a test", Status.OPEN);
        List<Todo> expectedList = List.of(expectedTodo);

        when(mockTodoRepo.findAll()).thenReturn(expectedList);

        List<Todo> actualList = todoService.findAll();

        assertEquals(expectedList, actualList);
        verify(mockTodoRepo).findAll();
    }

    @Test
    void createTodo_shouldReturnTrue_whenTodoCreated() {
        String expectedId = "1";

        TodoDTO expectedTodoDTO = new TodoDTO("test", Status.OPEN);
        Todo expectedTodo = new Todo(expectedId, "test", Status.OPEN);

        when(mockIdService.generateId()).thenReturn(expectedId);
        when(mockTodoRepo.save(expectedTodo)).thenReturn(expectedTodo);
        when(mockTodoRepo.findById(expectedId)).thenReturn(Optional.of(expectedTodo));

        Todo actualTodo = todoService.createTodo(expectedTodoDTO);

        assertEquals(expectedTodo, actualTodo);
        verify(mockTodoRepo).save(expectedTodo);
    }

    @Test
    void findById_shouldReturnTrue_whenTodoFound() {
        String findId = "1";
        Optional<Todo> expectedTodo = Optional.of(new Todo("1", "This is a test", Status.OPEN));

        when(mockTodoRepo.findById(findId)).thenReturn(expectedTodo);

        Optional<Todo> actualTodo = Optional.ofNullable(todoService.findById(findId));

        assertEquals(expectedTodo, actualTodo);
        verify(mockTodoRepo).findById(findId);
    }

    @Test
    void findById_shouldReturnTrue_whenTodoNotFound() {
        String notExistingId = "2";

        assertThrows(Exception.class, () -> todoService.findById(notExistingId));
        verify(mockTodoRepo).findById(notExistingId);
    }

    @Test
    void updateTodo_shouldReturnTrue_whenTodoUpdated() {
        String idToUpdate = "1";
        TodoDTO inputTodoDTO = new TodoDTO("test", Status.OPEN);
        Todo expectedTodo = new Todo(idToUpdate, "This is a test", Status.OPEN);

        when(mockTodoRepo.findById(idToUpdate)).thenReturn(Optional.of(expectedTodo));
        when(mockTodoRepo.save(expectedTodo)).thenReturn(expectedTodo);

        Todo actualUpdatedTodo = todoService.updateTodo(idToUpdate, inputTodoDTO);

        assertEquals(expectedTodo, actualUpdatedTodo);
    }

    @Test
    void updateTodo_shouldReturnTrue_whenTodoNotFound() {
        String notExistingId = "2";
        TodoDTO inputTodoDTO = new TodoDTO("test", Status.OPEN);

        assertThrows(Exception.class, () -> todoService.updateTodo(notExistingId, inputTodoDTO));
    }

    @Test
    void deleteTodo_shouldReturnTrue_whenTodoDeleted() {
        String idToDelete = "1";
        Todo expectedDeletedTodo = new Todo(idToDelete, "This is a test", Status.OPEN);
        mockTodoRepo.save(expectedDeletedTodo);

        when(mockTodoRepo.findById(idToDelete)).thenReturn(Optional.of(expectedDeletedTodo));

        Todo actualDeletedTodo = todoService.deleteTodoById(idToDelete);

        assertEquals(actualDeletedTodo, expectedDeletedTodo);
        verify(mockTodoRepo).findById(idToDelete);
        verify(mockTodoRepo).deleteById(idToDelete);
    }
}