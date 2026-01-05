package com.riwi.assesment.domain.model;

import java.util.UUID;

/**
 * Domain entity Project.
 * Represents a project in the system.
 * This class is pure and has no external framework dependencies.
 */
public class Project {

    private UUID id;
    private UUID ownerId;
    private String name;
    private ProjectStatus status;
    private boolean deleted;

    public Project() {
        this.status = ProjectStatus.DRAFT;
        this.deleted = false;
    }

    public Project(UUID id, UUID ownerId, String name, ProjectStatus status, boolean deleted) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.status = status;
        this.deleted = deleted;
    }

    // Builder pattern for fluent construction
    public static ProjectBuilder builder() {
        return new ProjectBuilder();
    }

    // Business methods

    /**
     * Checks if the project can be activated.
     * A project can only be activated if it is in DRAFT status.
     */
    public boolean canBeActivated() {
        return this.status == ProjectStatus.DRAFT && !this.deleted;
    }

    /**
     * Checks if the project can be activated with the given number of active tasks.
     * A project can only be activated if it is in DRAFT status and has at least one active task.
     * 
     * @param hasActiveTasks whether the project has at least one active (not completed) task
     */
    public boolean canBeActivated(boolean hasActiveTasks) {
        return canBeActivated() && hasActiveTasks;
    }

    /**
     * Activates the project by changing its status to ACTIVE.
     * @param hasActiveTasks whether the project has at least one active (not completed) task
     * @throws IllegalStateException if the project cannot be activated
     */
    public void activate(boolean hasActiveTasks) {
        if (!canBeActivated()) {
            throw new IllegalStateException("Project cannot be activated. Current status: " + this.status);
        }
        if (!hasActiveTasks) {
            throw new IllegalStateException("Project must have at least one active (not completed) task to be activated");
        }
        this.status = ProjectStatus.ACTIVE;
    }

    /**
     * Checks if the project is active.
     */
    public boolean isActive() {
        return this.status == ProjectStatus.ACTIVE;
    }

    /**
     * Checks if the project is in draft status.
     */
    public boolean isDraft() {
        return this.status == ProjectStatus.DRAFT;
    }

    /**
     * Marks the project as deleted (soft delete).
     */
    public void markAsDeleted() {
        this.deleted = true;
    }

    /**
     * Checks if the user is the owner of the project.
     */
    public boolean isOwnedBy(UUID userId) {
        return this.ownerId != null && this.ownerId.equals(userId);
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    // Builder class
    public static class ProjectBuilder {
        private UUID id;
        private UUID ownerId;
        private String name;
        private ProjectStatus status = ProjectStatus.DRAFT;
        private boolean deleted = false;

        public ProjectBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public ProjectBuilder ownerId(UUID ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public ProjectBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ProjectBuilder status(ProjectStatus status) {
            this.status = status;
            return this;
        }

        public ProjectBuilder deleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public Project build() {
            return new Project(id, ownerId, name, status, deleted);
        }
    }
}
