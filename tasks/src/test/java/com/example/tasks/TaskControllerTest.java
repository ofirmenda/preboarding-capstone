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

    @Test
    // filter tasks by done=true
    void filterTasksByDoneTrueTest() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "buy milk", "done": false}
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "buy cheese", "done": true}
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/tasks").param("done", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("buy cheese"))
                .andExpect(jsonPath("$[0].done").value(true));
    }

    @Test
    // filter tasks by done=false
    void filterTasksByDoneFalseTest() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "buy milk", "done": false}
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "buy cheese", "done": true}
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/tasks").param("done", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("buy milk"))
                .andExpect(jsonPath("$[0].done").value(false));
    }

    @Test
    // update task successfully
    void updateTaskSuccessTest() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "buy milk", "done": false}
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/tasks/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "buy oat milk", "done": true}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.title").value("buy oat milk"))
                .andExpect(jsonPath("$.done").value(true));

        mockMvc.perform(get("/tasks/0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("buy oat milk"))
                .andExpect(jsonPath("$.done").value(true));
    }

    @Test
    // update task preserves existing done status if omitted
    void updateTaskPreserveDoneStatusTest() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "buy cheese", "done": true}
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/tasks/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "buy gouda"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.title").value("buy gouda"))
                .andExpect(jsonPath("$.done").value(true));
    }

    @Test
    // update non existing task returns 404
    void updateNonExistingTaskTest() throws Exception {
        mockMvc.perform(put("/tasks/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "buy milk", "done": true}
                        """))
                .andExpect(status().isNotFound());
    }

    @Test
    // update task with empty title returns 400
    void updateTaskWithEmptyTitleTest() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "buy milk"}
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/tasks/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": ""}
                        """))
                .andExpect(status().isBadRequest());
    }
}
