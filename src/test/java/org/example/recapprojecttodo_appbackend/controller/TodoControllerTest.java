package org.example.recapprojecttodo_appbackend.controller;

import org.example.recapprojecttodo_appbackend.models.Todo;
import org.example.recapprojecttodo_appbackend.repository.TodoRepo;
import org.example.recapprojecttodo_appbackend.utils.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
class TodoControllerTest {

    @Autowired
    MockRestServiceServer mockServer;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TodoRepo todoRepo;

    @Nested
    class getTodoByIdTests {
        @Test
        @DisplayName("should return true when todo returned correctly")
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
        @DisplayName("should return true when todo not found")
        void getTodoById_shouldReturnTrue_whenNotFound()
                throws Exception {


            mockMvc.perform(get("/api/todo/1"))
                    .andExpect(status().isNotFound());
        }

    }

    @Nested
    class modifyTodoTests {
        @Test
        @DisplayName("should return true when modified correctly")
        void modifyTodo_shouldReturnTrue_whenModifyTodoById()
                throws Exception {
            Todo newTodo = new Todo("1", "test", Status.OPEN);
            todoRepo.save(newTodo);

            String expected = """
                        {
                            "description": "modified test",
                            "status": "DONE",
                            "id": "1"
                        }
                    """;

            mockMvc.perform(put("/api/todo/1").contentType(MediaType.APPLICATION_JSON).content(expected))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expected));

        }

        @Test
        @DisplayName("should return true when todo not found")
        void modifyTodo_shouldReturnTrue_whenTodoNotFound()
                throws Exception {

            String requestBody = """
                        {
                            "description": "modified test",
                            "status": "DONE",
                            "id": "1"
                        }
                    """;


            mockMvc.perform(put("/api/todo/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                    .andExpect(status().isNotFound());

        }

    }

    @Nested
    class deleteTodoByIdTests {
        @Test
        @DisplayName("should return true when deleted")
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

    @Nested
    class createExampleTodosTests {

        @Test
        @DisplayName("should return true when todos created")
        void createExampleTodos() throws Exception {
            String expectedJson = """
                    [
                        {
                            "description":"Do Homework",
                            "status":"OPEN"
                        },
                        {
                            "description":"Dump trash",
                            "status":"OPEN"
                        },
                        {
                            "description":"Practice Guitar",
                            "status":"IN_PROGRESS"
                        },
                        {
                            "description":"Swimming training",
                            "status":"DONE"
                        }
                    ]
                    """;

            mockServer.expect(ExpectedCount.manyTimes(), requestTo("https://api.openai.com/v1/responses"))
                    .andExpect(method(HttpMethod.POST))
                    .andRespond(request -> {
                        String body = request.getBody().toString();
                        return withSuccess("{\"received\":\"" + body + "\"}", MediaType.APPLICATION_JSON).createResponse(request);
                    });


            mockMvc.perform(post("/api/todo/createExampleTodos"))
                    .andExpect(status().isCreated())
                    .andExpect(content().json(expectedJson));

            mockServer.verify();
        }
    }

    @Nested
    class createTodoTests {
        @Test
        @DisplayName("should return true when todo created correctly")
        void createTodo_shouldReturnCreated_whenCreateTodo()
                throws Exception {

            String requestBody = """
                        {
                            "description": "this is a rwong text",
                            "status": "OPEN"
                        }
                    """;

            String expectedControllerResponse = """
                        {
                            "description": "This is a wrong text. (grammar checked by openAi)",
                            "status": "OPEN"
                        }
                    """;

            String expectedOpenAiResponse = """
                    {
                    "output": [
                                    {
                                      "type": "message",
                                      "id": "msg_67ccd2bf17f0819081ff3bb2cf6508e60bb6a6b452d3795b",
                                      "status": "completed",
                                      "role": "assistant",
                                      "content": [
                                        {
                                          "type": "output_text",
                                          "text": "This is a wrong text.",
                                          "annotations": []
                                        }
                                      ]
                                    }
                               ]
                    }
                    """;

            mockServer.expect(requestTo("https://api.openai.com/v1/responses"))
                    .andExpect(method(HttpMethod.POST))
                    .andRespond(withSuccess(expectedOpenAiResponse, MediaType.APPLICATION_JSON));

            mockMvc.perform(post("/api/todo").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(content().json(expectedControllerResponse))
                    .andExpect(jsonPath("$.id").isNotEmpty());

        }

        @Test
        @DisplayName("should return true when error thrown correctly with EMPTY body")
        void createTodo_shouldReturnError_whenCreateTodo()
                throws Exception {

            String given = """
                        {}
                    """;

            String expected = """
                        {
                            "error": "description: description must not be empty; status: status must not be empty ('OPEN', 'IN_PROGRESS' or 'DONE'); ",
                            "status": 400
                        }
                    """;

            mockMvc.perform(post("/api/todo").contentType(MediaType.APPLICATION_JSON).content(given))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(expected))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.timeStamp").isNotEmpty());

        }

        @Test
        @DisplayName("should return true when error thrown correctly with NO body")
        void createTodo_shouldReturnError_whenCreateTodoWithNoBody()
                throws Exception {

            String given = """
                    """;

            String expected = """
                            {
                                "error": "Request Body must not be empty",
                                "status": 400
                            }
                    """;

            mockMvc.perform(post("/api/todo").contentType(MediaType.APPLICATION_JSON).content(given))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(expected))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.timeStamp").isNotEmpty());

        }

    }

    @Nested
    class getAllTodosTest {
        @Test
        @DisplayName("Should return true when list correct")
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

    }

    @Nested
    class undoTodoTests {
        @Test
        @DisplayName("should return true when todo not possible")
        void undoTodo_shouldReturnTrue_whenUndoNotPossible()
                throws Exception {

            String requestBody = """
                        {
                            "error": "Undo not possible",
                            "status": 406
                        }
                    """;


            mockMvc.perform(get("/api/todo/undo"))
                    .andExpect(status().isNotAcceptable())
                    .andExpect(content().json(requestBody));

        }

    }
}