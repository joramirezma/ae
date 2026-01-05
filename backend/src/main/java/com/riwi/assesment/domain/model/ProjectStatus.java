package com.riwi.assesment.domain.model;

/**
 * Enum representing the possible states of a project.
 */
public enum ProjectStatus {
    /**
     * Initial state of the project.
     * The project is in draft and can be modified.
     */
    DRAFT,

    /**
     * The project is active.
     * Can only be activated if it has at least one task.
     */
    ACTIVE
}
