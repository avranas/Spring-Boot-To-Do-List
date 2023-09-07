package com.example.avranas.springBootToDoList;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
  void assertTodo(Map<?,?> json, Todo todo) {
    assertEquals(todo.getContent(), json.get("content"));
    assertEquals(todo.getCreatedAt(), json.get("createdAt"));
    assertEquals(todo.getUpdatedAt(), json.get("updatedAt"));
  }

  void assertTodo(String response, Todo todo) {
    Map<?,?> json = gson.fromJson(response, Map.class);
    assertTodo(json, todo);
  }

  void assertTodos(String response, Todo[] todos) {
    Map<?,?>[] jsons = gson.fromJson(response, Map[].class);
    assertEquals(jsons.length, todos.length);
    for (int i = 0; i < jsons.length; i++) {
      assertTodo(jsons[i], todos[i]);
    }
  }

  @Test
  public void greetingShouldReturnAnEmptyArray() throws Exception {
    final RequestBuilder request = MockMvcRequestBuilders.get("/todos");
    final MvcResult result = mockMvc.perform(request).andReturn();
    assertEquals("[]", result.getResponse().getContentAsString());
    assertEquals(result.getResponse().getStatus(), 200);
  }

  Todo[] postThreeNewTodos() throws Exception {
    final String[] content = { "Take out the trash", "Go to the gym", "Write more tests" };
    Todo[] todos = new Todo[content.length];
    for (int i = 0; i < content.length; i++) {
      final String str = content[i];
      final JSONObject requestBody = new JSONObject();
      requestBody.put("content", str);
      final RequestBuilder request = MockMvcRequestBuilders.post("/todos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody.toString());
      final MvcResult result = mockMvc.perform(request).andReturn();
      assertEquals(result.getResponse().getStatus(), 201);
      final Todo newTodo = new Todo(str);
      assertTodo(result.getResponse().getContentAsString(), newTodo);
      todos[i] = newTodo;
    }
    return todos;
  }

  @Test
  void itPostsNewTodosAndGetsAll() throws Exception {
    Todo[] todos = postThreeNewTodos();
    final RequestBuilder request = MockMvcRequestBuilders.get("/todos");
    final MvcResult result = mockMvc.perform(request).andReturn();
    assertEquals(result.getResponse().getStatus(), 200);
    assertTodos(result.getResponse().getContentAsString(), todos);
  }

  @Test
  void itPostsNewTodosAndGetsById() throws Exception {
    Todo[] todos = postThreeNewTodos();
    final Integer todoId = 2;
    final RequestBuilder request = MockMvcRequestBuilders.get("/todos/" + todoId);
    final MvcResult result = mockMvc.perform(request).andReturn();
    assertTodo(result.getResponse().getContentAsString(), todos[todoId - 1]);
    assertEquals(result.getResponse().getStatus(), 200);
  }

  @Test
  void itUpdatesATodo() throws Exception {
    final JSONObject requestBody = new JSONObject();
    final String todoContent = "Update this todo";
    final Todo todo = new Todo(todoContent);
    requestBody.put("content", todoContent);
    RequestBuilder request = MockMvcRequestBuilders.post("/todos")
    .contentType(MediaType.APPLICATION_JSON)
    .content(requestBody.toString());
    MvcResult result = mockMvc.perform(request).andReturn();
    assertEquals(result.getResponse().getStatus(), 201);
    final String responseString = result.getResponse().getContentAsString();
    assertTodo(responseString, todo);
    // Parse the response and get the ID so we can update it
    Map<?,?> json = gson.fromJson(responseString, Map.class);
    Integer id = (int) Double.parseDouble(json.get("id").toString());
    request = MockMvcRequestBuilders.get("/todos/" + id);
    result = mockMvc.perform(request).andReturn();
    assertTodo(result.getResponse().getContentAsString(), todo);
    assertEquals(result.getResponse().getStatus(), 200);
    final String newContent = "Finish writing tests";
    todo.setContent(newContent);
    todo.setUpdatedAt(java.time.LocalDate.now());
    requestBody.put("content", newContent);
    request = MockMvcRequestBuilders.patch("/todos/" + id)
      .contentType(MediaType.APPLICATION_JSON)
      .content(requestBody.toString());
    result = mockMvc.perform(request).andReturn();
    assertTodo(result.getResponse().getContentAsString(), todo);
    assertEquals(result.getResponse().getStatus(), 200);
  }
}