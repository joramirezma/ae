package com.riwi.assesment.domain.model;

import java.util.UUID;

/**
 * Domain entity Task.
 * Represents a task associated with a project.
 * This class is pure and has no external framework dependencies.
 */
public class Task {

    private UUID id;
    private UUID projectId;
    private String title;
    private boolean completed;
    private boolean deleted;

    public Task() {
        this.completed = false;
        this.deleted = false;
    }

    public Task(UUID id, UUID projectId, String title, boolean completed, boolean deleted) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.completed = completed;
        this.deleted = deleted;
    }

    // Builder pattern for fluent construction
    public static TaskBuilder builder() {
        return new TaskBuilder();
    }

    // Business methods

    /**
     * Marks the task as completed.
     * @param project The project this task belongs to
     * @throws IllegalStateException if the task is already completed, deleted, or project is not active
     */
    public void complete(Project project) {
        if (this.deleted) {
            throw new IllegalStateException("Cannot complete a deleted task");
        }
        if (this.completed) {
            throw new IllegalStateException("Task is already completed");
        }
        if (!project.isActive()) {
            throw new IllegalStateException(
                "Tasks can only be completed when the project is ACTIVE. Current project status: " + project.getStatus());
        }
        this.completed = true;
    }

    /**
     * Checks if the task can be completed.
     * @param project The project this task belongs to
     */
    public boolean canBeCompleted(Project project) {
        return !this.completed && !this.deleted && project.isActive();
    }

    /**
     * Marks the task as deleted (soft delete).
     */
    public void markAsDeleted() {
        this.deleted = true;
    }

    /**
     * Checks if the task belongs to a specific project.
     */
    public boolean belongsToProject(UUID projectId) {
        return this.projectId != null && this.projectId.equals(projectId);
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isDeleted() {
        return deleted;
    }

    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    // Builder class
    public static class TaskBuilder {
        private UUID id;
        private UUID projectId;
        private String title;
        private boolean completed = false;
        private boolean deleted = false;

        public TaskBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public TaskBuilder projectId(UUID projectId) {
            this.projectId = projectId;
            return this;
        }

        public TaskBuilder title(String title) {
            this.title = title;
            return this;
        }

        public TaskBuilder completed(boolean completed) {
            this.completed = completed;
            return this;
        }

        public TaskBuilder deleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public Task build() {
            return new Task(id, projectId, title, completed, deleted);
        }
    }
}
