package com.example.avranas.springBootToDoList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.json.JSONObject;
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

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void greetingShouldReturnAnEmptyArray() throws Exception {
    final RequestBuilder request = MockMvcRequestBuilders.get("/todos");
    final MvcResult result = mockMvc.perform(request).andReturn();
    assertEquals("[]", result.getResponse().getContentAsString());
    assertEquals(result.getResponse().getStatus(), 200);
  }

  @Test
  void itPostsNewTodos() throws Exception {
    final String[] content = { "Take out the trash", "Go to the gym", "Write more tests" };
    final Todo[] expectedResults = new Todo[content.length];
    for (int i = 0; i < content.length; i++) {
      final String str = content[i]; 
      final JSONObject requestBody = new JSONObject();
      requestBody.put("content", str);
      final RequestBuilder request = MockMvcRequestBuilders.post("/todos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody.toString());
      final MvcResult result = mockMvc.perform(request).andReturn();
      assertEquals(result.getResponse().getStatus(), 200);
      final Todo newTodo = new Todo(str);
      expectedResults[i] = newTodo;
    }
    final RequestBuilder request = MockMvcRequestBuilders.get("/todos");
    final MvcResult result = mockMvc.perform(request).andReturn();
    String expected = gson.toJson(expectedResults);
    assertEquals(result.getResponse().getContentAsString(), expected);
  }
}