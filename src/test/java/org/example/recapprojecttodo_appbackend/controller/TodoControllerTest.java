package org.example.recapprojecttodo_appbackend.controller;

import org.example.recapprojecttodo_appbackend.models.Todo;
import org.example.recapprojecttodo_appbackend.repository.TodoRepo;
import org.example.recapprojecttodo_appbackend.utils.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepo todoRepo;

    @Test
    void getAllTodos_shouldReturnTrue_whenListReturnedCorrectly()
            throws Exception {
        Todo newTodo = new Todo("1", "test", Status.OPEN);
        todoRepo.save(newTodo);

        String expected = """
                [
                    {
                        "id": "1",
                        "description": "test",
                        "status": "OPEN"
                    }
                ]
                """;

        mockMvc.perform(get("/api/todo"))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void createTodo_shouldReturnCreated_whenCreateTodo()
            throws Exception {
        Todo newTodo = new Todo("1", "test", Status.OPEN);
        todoRepo.save(newTodo);

        String expected = """
                    {
                        "description": "test",
                        "status": "OPEN"
                    }
                """;

        mockMvc.perform(post("/api/todo").contentType(MediaType.APPLICATION_JSON).content(expected))
                .andExpect(status().isCreated())
                .andExpect(content().json(expected))
                .andExpect(jsonPath("$.id").isNotEmpty());

    }

    @Test
    void getTodoById_shouldReturnTrue_whenGetTodoById()
            throws Exception {
        Todo newTodo = new Todo("1", "test", Status.OPEN);
        todoRepo.save(newTodo);

        String expected = """
                [
                    {
                        "id": "1",
                        "description": "test",
                        "status": "OPEN"
                    }
                ]
                """;

        mockMvc.perform(get("/api/todo").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    void modifyTodo_shouldReturnTrue_whenModifyTodoById()
            throws Exception {
        Todo newTodo = new Todo("1", "test", Status.OPEN);
        todoRepo.save(newTodo);

        String expected = """
                    {
                        "description": "modified test",
                        "status": "DONE"
                    }
                """;

        mockMvc.perform(put("/api/todo/1").contentType(MediaType.APPLICATION_JSON).content(expected))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));

    }

    @Test
    void deleteTodoById_shouldReturnTrue_whenDeleteTodoById()
            throws Exception {
        Todo newTodo = new Todo("1", "test", Status.OPEN);
        todoRepo.save(newTodo);

        String expected = """
                    {
                        "id": "1",
                        "description": "test",
                        "status": "OPEN"
                    }
                """;

        mockMvc.perform(delete("/api/todo/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));

    }
}