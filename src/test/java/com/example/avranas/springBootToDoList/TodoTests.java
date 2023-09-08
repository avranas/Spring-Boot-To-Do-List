package com.example.avranas.springBootToDoList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TodoTests {

  private Gson gson = new GsonBuilder().serializeNulls().create();

  @BeforeEach
  public void beforeEach() throws Exception {
    final RequestBuilder request = MockMvcRequestBuilders.delete("/DELETE_ALL_TODOS_FOR_TESTS");
    mockMvc.perform(request).andReturn();
  }

  @Autowired
  private MockMvc mockMvc;

  // Assert that everything in a Todo object matches the JSON response
  // except the id. Those should usually be different
  void assertTodo(Map<?, ?> json, Todo todo) {
    assertEquals(todo.getContent(), json.get("content"));
    assertEquals(todo.getCreatedAt(), json.get("createdAt"));
    assertEquals(todo.getUpdatedAt(), json.get("updatedAt"));
  }

  void assertTodos(String response, Todo[] todos) {
    Map<?, ?>[] jsons = gson.fromJson(response, Map[].class);
    assertEquals(jsons.length, todos.length);
    for (int i = 0; i < jsons.length; i++) {
      assertTodo(jsons[i], todos[i]);
    }
  }

  void assertTodo(Todo todo1, Todo todo2) {
    assertEquals(todo1.getContent(), todo2.getContent());
    assertEquals(todo1.getCreatedAt(), todo2.getCreatedAt());
    assertEquals(todo1.getUpdatedAt(), todo2.getUpdatedAt());
  }

  Todo makeTodoWithServerResponse(String response) {
    return new Todo(gson.fromJson(response, Map.class));
  }

  @Test
  public void greetingShouldReturnAnEmptyArray() throws Exception {
    final RequestBuilder request = MockMvcRequestBuilders.get("/todos");
    final MvcResult result = mockMvc.perform(request).andReturn();
    assertEquals("[]", result.getResponse().getContentAsString());
    assertEquals(result.getResponse().getStatus(), 200);
  }

  // Makes 3 todos in the testing suite
  // Posts them on the server
  // Asserts the todos on the server are the same as the ones on the testing suite
  Todo[] postThreeNewTodos() throws Exception {
    final String[] content = { "Take out the trash", "Go to the gym", "Write more tests" };
    Todo[] todos = new Todo[content.length];
    for (int i = 0; i < content.length; i++) {
      // POST a new todo with local data
      final String str = content[i];
      final JSONObject requestBody = new JSONObject();
      requestBody.put("content", str);
      final RequestBuilder request = MockMvcRequestBuilders.post("/todos")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestBody.toString());
      final MvcResult result = mockMvc.perform(request).andReturn();
      // Make a local todo with local data
      final Todo localTodo = new Todo(str);
      final String responseStr = result.getResponse().getContentAsString();
      // Make a todo from the server response
      final Todo serverTodo = makeTodoWithServerResponse(responseStr);
      // Assert that the local todo and server todo have the same data
      assertTodo(serverTodo, localTodo);
      assertEquals(result.getResponse().getStatus(), 201);
      todos[i] = serverTodo;
    }
    return todos;
  }

  Todo postOneNewTodo() throws Exception {
    final JSONObject requestBody = new JSONObject();
    final String oldContent = "This is my new todo";
    final Todo localTodo = new Todo(oldContent);
    requestBody.put("content", oldContent);
    // POST a new todo
    RequestBuilder request = MockMvcRequestBuilders.post("/todos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody.toString());
    MvcResult result = mockMvc.perform(request).andReturn();
    final String responseString = result.getResponse().getContentAsString();
    // Make a todo from the server response
    final Todo serverTodo = makeTodoWithServerResponse(responseString);
    assertTodo(serverTodo, localTodo);
    assertEquals(result.getResponse().getStatus(), 201);
    return serverTodo;
  }

  @Test
  void itPostsNewTodosAndGetsAll() throws Exception {
    // POST 3 new todos to the server and return them
    Todo[] todos = postThreeNewTodos();
    final RequestBuilder request = MockMvcRequestBuilders.get("/todos");
    final MvcResult result = mockMvc.perform(request).andReturn();
    assertEquals(result.getResponse().getStatus(), 200);
    assertTodos(result.getResponse().getContentAsString(), todos);
  }

  @Test
  void itPostsNewTodosAndGetsById() throws Exception {
    // POST 3 new todos to the server and return them
    Todo[] todos = postThreeNewTodos();
    final Todo secondTodo = todos[1];
    // GET the todo from the server with the ID from the second todo
    final RequestBuilder request = MockMvcRequestBuilders.get("/todos/" + secondTodo.getId());
    final MvcResult result = mockMvc.perform(request).andReturn();
    final Todo serverTodo = makeTodoWithServerResponse(result.getResponse().getContentAsString());
    // Assert that response from the server is the same as the second todo
    assertTodo(secondTodo, serverTodo);
    assertEquals(result.getResponse().getStatus(), 200);
  }

  @Test
  void itUpdatesATodo() throws Exception {
    Todo serverTodo = postOneNewTodo();
    final Todo localTodo = serverTodo;
    final Integer id = serverTodo.getId();
    // Update localTodo
    final String oldContent = serverTodo.getContent();
    final String newContent = "Finish writing tests";
    localTodo.setContent(newContent);
    localTodo.setUpdatedAt(java.time.LocalDate.now());
    // Update serverTodo
    final JSONObject requestBody = new JSONObject();
    requestBody.put("content", newContent);
    final RequestBuilder request = MockMvcRequestBuilders.patch("/todos/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody.toString());
    final MvcResult result = mockMvc.perform(request).andReturn();
    serverTodo = makeTodoWithServerResponse(result.getResponse().getContentAsString());
    // Assert that both have been updated correctly
    assertTodo(localTodo, serverTodo);
    assertNotEquals(serverTodo.getContent(), oldContent);
    assertEquals(result.getResponse().getStatus(), 200);
  }

  @Test
  void itDeletesATodo() throws Exception {
    final Todo serverTodo = postOneNewTodo();
    final Todo localTodo = serverTodo;
    final Integer id = serverTodo.getId();
    RequestBuilder request = MockMvcRequestBuilders.delete("/todos/" + id);
    MvcResult result = mockMvc.perform(request).andReturn();
    final Todo deletedTodo = makeTodoWithServerResponse(result.getResponse().getContentAsString());
    // Assert that the request was a success and that the
    // deleted todo was returned by the server
    assertEquals(result.getResponse().getStatus(), 200);
    assertTodo(localTodo, deletedTodo);
    // Try getting the todo with the deleted ID. We should get a 404 error
    request = MockMvcRequestBuilders.get("/todos/" + deletedTodo.getId());
    result = mockMvc.perform(request).andReturn();
    assertEquals(result.getResponse().getStatus(), 404);
  }

  // Sad paths
  @Test
  void itGivesA404ErrorIfItCanNotFindTheTodo() throws Exception {
    RequestBuilder request = MockMvcRequestBuilders.delete("/todos/1");
    MvcResult result = mockMvc.perform(request).andReturn();
    assertEquals(result.getResponse().getStatus(), 404);
    request = MockMvcRequestBuilders.get("/todos/1");
    result = mockMvc.perform(request).andReturn();
    assertEquals(result.getResponse().getStatus(), 404);
    // Patch requires a request body
    JSONObject requestBody = new JSONObject();
    requestBody.put("content", "Go to the gym");
    request = MockMvcRequestBuilders.patch("/todos/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody.toString());
    result = mockMvc.perform(request).andReturn();
    assertEquals(result.getResponse().getStatus(), 404);
  }

  @Test
  void itGivesA400ErrorIfPatchDoesNotHaveAValidRequestBody() throws Exception {
    postOneNewTodo();
    // No request body
    RequestBuilder request = MockMvcRequestBuilders.patch("/todos/1");
    MvcResult result = mockMvc.perform(request).andReturn();
    assertEquals(result.getResponse().getStatus(), 400);
    // Request body without content property should respond with 200
    JSONObject requestBody = new JSONObject();
    requestBody.put("not_content", "Go to the gym");
    request = MockMvcRequestBuilders.patch("/todos/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody.toString());
    result = mockMvc.perform(request).andReturn();
    assertEquals(result.getResponse().getStatus(), 200);
  }
}