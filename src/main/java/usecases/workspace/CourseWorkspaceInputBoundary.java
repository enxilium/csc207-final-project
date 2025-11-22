package usecases.workspace;

import entities.*;

/**
 * Input boundary for the course workspace use case.
 * Defines the interface for workspace-related course operations.
 */
public interface CourseWorkspaceInputBoundary {

    /**
     * Finds a course by its ID and prepares the appropriate view.
     *
     * @param courseId the ID of the course to find
     * @param isEdit if true, prepares the edit view; otherwise prepares the workspace view
     */
    void findCourseById(String courseId, boolean isEdit);

    /**
     * Creates a new course.
     *
     * @param course the course to create
     */
    void createCourse(Course course);

    /**
     * Updates an existing course.
     *
     * @param course the course to update
     */
    void updateCourse(Course course);

    /**
     * Deletes a course by its ID.
     *
     * @param courseId the ID of the course to delete
     */
    void deleteCourse(String courseId);

}

