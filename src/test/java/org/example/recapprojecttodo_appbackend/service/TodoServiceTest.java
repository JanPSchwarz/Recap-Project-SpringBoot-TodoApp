package org.example.recapprojecttodo_appbackend.service;

import org.example.recapprojecttodo_appbackend.exceptions.TodoNotFoundException;
import org.example.recapprojecttodo_appbackend.models.Todo;
import org.example.recapprojecttodo_appbackend.repository.TodoRepo;
import org.example.recapprojecttodo_appbackend.utils.IdService;
import org.example.recapprojecttodo_appbackend.utils.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TodoServiceTest {

    TodoRepo mockTodoRepo;
    IdService mockIdService;
    OpenAiService mockAIService;

    TodoService todoService;

    @BeforeEach
    void setUp() {
        mockTodoRepo = Mockito.mock(TodoRepo.class);
        mockIdService = Mockito.mock(IdService.class);
        mockAIService = Mockito.mock(OpenAiService.class);

        // ADDING ONE TODO_ BEFOREHAND
        Todo testTodo = new Todo("1", "This is a test", Status.OPEN);
        mockTodoRepo.save(testTodo);

        todoService = new TodoService(mockTodoRepo, mockIdService, mockAIService);
    }


    @Nested
    class createTodoTests {
        @Test
        void createTodo_shouldReturnTrue_whenTodoCreated() {
            String expectedId = "1";

            Todo expectedTodoDTO = new Todo("test", Status.OPEN);
            Todo expectedTodo = new Todo(expectedId, "test", Status.OPEN);

            when(mockAIService.checkGrammar(expectedTodo.description())).thenReturn("test");
            when(mockIdService.generateId()).thenReturn(expectedId);
            when(mockTodoRepo.save(expectedTodo)).thenReturn(expectedTodo);
            when(mockTodoRepo.findById(expectedId)).thenReturn(Optional.of(expectedTodo));

            Todo actualTodo = todoService.createTodo(expectedTodoDTO);

            assertEquals(expectedTodo, actualTodo);
            verify(mockTodoRepo).save(expectedTodo);
        }
    }

    @Nested
    class findByIdTests {
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

            assertThrows(TodoNotFoundException.class, () -> todoService.findById(notExistingId));
            verify(mockTodoRepo).findById(notExistingId);
        }
    }

    @Nested
    class findAllTests {
        @Test
        void findAll_shouldReturnTrue_whenCorrectListReturned() {
            Todo expectedTodo = new Todo("1", "This is a test", Status.OPEN);
            List<Todo> expectedList = List.of(expectedTodo);

            when(mockTodoRepo.findAll()).thenReturn(expectedList);

            List<Todo> actualList = todoService.findAll();

            assertEquals(expectedList, actualList);
            verify(mockTodoRepo).findAll();
        }
    }

    @Nested
    class updateTodoTests {
        @Test
        void updateTodo_shouldReturnTrue_whenTodoUpdated() {
            String idToUpdate = "1";
            Todo inputTodo = new Todo("test", Status.OPEN);
            Todo expectedTodo = new Todo(idToUpdate, "This is a test", Status.OPEN);

            when(mockTodoRepo.findById(idToUpdate)).thenReturn(Optional.of(expectedTodo));
            when(mockTodoRepo.save(expectedTodo)).thenReturn(expectedTodo);

            Todo actualUpdatedTodo = todoService.updateTodo(idToUpdate, inputTodo);

            assertEquals(expectedTodo, actualUpdatedTodo);
        }

        @Test
        void updateTodo_shouldReturnTrue_whenTodoNotFound() {
            String notExistingId = "2";
            Todo inputTodo = new Todo("test", Status.OPEN);

            assertThrows(TodoNotFoundException.class, () -> todoService.updateTodo(notExistingId, inputTodo));
        }
    }

    @Nested
    class deleteTodoTests {
        @Test
        void deleteTodo_shouldReturnTrue_whenTodoDeleted() {
            String idToDelete = "1";
            Todo expectedDeletedTodo = new Todo(idToDelete, "This is a test", Status.OPEN);
            mockTodoRepo.save(expectedDeletedTodo);

            when(mockTodoRepo.findById(idToDelete)).thenReturn(Optional.of(expectedDeletedTodo));
            doNothing().when(mockTodoRepo).deleteById("1");

            Todo actualDeletedTodo = todoService.deleteTodoById(idToDelete);

            assertEquals(actualDeletedTodo, expectedDeletedTodo);
            verify(mockTodoRepo).findById(idToDelete);
            verify(mockTodoRepo).deleteById(idToDelete);
        }
    }

    @Nested
    class undoRedoTests {
        @Test
        void undoRedoAction_shouldReturnTrue_whenLastCreationWasRemoved() {
            Todo todoDto = new Todo("This is a new test", Status.OPEN);
            Todo todo = new Todo("1", "This is a new test", Status.OPEN);

            when(mockIdService.generateId()).thenReturn("1");
            when(mockTodoRepo.save(todo)).thenReturn(todo);
            when(mockTodoRepo.findById("1")).thenReturn(Optional.of(todo));
            doNothing().when(mockTodoRepo).deleteById("1");

            Todo createdTodo = todoService.createTodo(todoDto);
            Todo reversedTodo = todoService.undoRedoAction();

            assertEquals(createdTodo, reversedTodo);
            verify(mockTodoRepo).deleteById("1");
        }

        @Test
        void undoRedoAction_shouldReturnTrue_whenLastDeletionWasRemoved() {
            Todo todo = new Todo("1", "This is a new test", Status.OPEN);

            when(mockTodoRepo.findById("1")).thenReturn(Optional.of(todo));
            doNothing().when(mockTodoRepo).deleteById("1");

            Todo deletedTodo = todoService.deleteTodoById("1");
            Todo reversedTodo = todoService.undoRedoAction();

            assertEquals(deletedTodo, reversedTodo);
            verify(mockTodoRepo).deleteById("1");
            verify(mockTodoRepo).save(todo);
        }
    }

    @Nested
    class setLastActionTests {
        @Test
        @DisplayName("should return true when invalid argument error thrown")
        void setLastAction() {
            Todo newTodo = new Todo("1", "This is a new test", Status.OPEN);
            assertThrows(IllegalArgumentException.class, () -> todoService.setLastAction("test", null));
            assertThrows(IllegalArgumentException.class, () -> todoService.setLastAction("", newTodo));
        }

        @Test
        @DisplayName("should return true when setting was successfully")
        void setLastAction_shouldReturnTrue_whenSettingSuccessfully() {
            Todo newTodo = new Todo("1", "This is a new test", Status.OPEN);
            assertTrue(todoService.setLastAction("1", newTodo));

        }
    }


}