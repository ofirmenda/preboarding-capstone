package com.example.tasks;

import java.util.Objects;

public record Task(Long id, String title,Boolean done) { 
        public Task {
        Objects.requireNonNull(title, "title must not be null");
        }
}
