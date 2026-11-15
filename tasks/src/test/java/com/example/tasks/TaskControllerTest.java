package com.example.tasks;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.http.MediaType;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WebMvcTest(TaskController.class)
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    // at first no tasks
    void emptyTasksList() throws Exception {
        mockMvc.perform(get("/tasks"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    // add task with empty title sould return 400 no content
    void addTaskWithEmptyTitleTest() throws Exception {
        mockMvc.perform(post("/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {"title": ""}
                    """))
       .andExpect(status().isBadRequest());
    }
    @Test
    // adding task should return 201 created
    void addTaskTest() throws Exception {
        mockMvc.perform(post("/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {"title": "buy milk"}
                    """))
       .andExpect(status().isCreated());
    }
    @Test
    // add done task
    void addDoneTaskTest() throws Exception {
        mockMvc.perform(post("/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {"title": "buy cheese" ,"done":true}
                    """))
       .andExpect(status().isCreated());
    }

    // get should return all added tasks
    @Test
    void getTasksList() throws Exception {
    // add two tasks
    mockMvc.perform(post("/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {"title": "buy tomato"}
                    """))
       .andExpect(status().isCreated());

    mockMvc.perform(post("/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {"title": "buy cucamber"}
                    """))
       .andExpect(status().isCreated());

    mockMvc.perform(get("/tasks"))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$.length()").value(2))
       .andExpect(jsonPath("$[0].title").value("buy tomato"))
       .andExpect(jsonPath("$[1].title").value("buy cucamber"));
    } 
    @Test
    // get specific task
    void getSpecificTask() throws Exception {
         mockMvc.perform(post("/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {"title": "buy tomato"}
                    """))
       .andExpect(status().isCreated());
           mockMvc.perform(get("/tasks/0"))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$.title").value("buy tomato"));
    }

    @Test
    // get non existing  task
    void getNonExistingTask() throws Exception {
           mockMvc.perform(get("/tasks/0"))
       .andExpect(status().isNotFound());
    }

  
    @Test
    // delete task
    void deleteTask() throws Exception {
                 mockMvc.perform(post("/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {"title": "buy tomato"}
                    """))
            .andExpect(status().isCreated());
                mockMvc.perform(delete("/tasks/0"))
               .andExpect(status().isNoContent());
    }


}
