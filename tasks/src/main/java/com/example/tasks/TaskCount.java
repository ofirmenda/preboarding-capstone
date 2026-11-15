package com.example.tasks;

import java.util.List;

/**
 * Represents the count summary of tasks.
 *
 * @param total the total number of tasks
 * @param done  the number of completed tasks
 */
public record TaskCount(long total, long done) {

    /**
     * Computes the task count summary from the given list of tasks.
     *
     * @param tasks the list of tasks to summarize
     * @return a TaskCount with the calculated totals
     */
    public static TaskCount from(List<Task> tasks) {
        if (tasks == null) {
            return new TaskCount(0, 0);
        }
        long totalCount = tasks.size();
        long doneCount = tasks.stream()
                .filter(task -> Boolean.TRUE.equals(task.done()))
                .count();
        return new TaskCount(totalCount, doneCount);
    }
}
