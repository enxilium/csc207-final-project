package interface_adapters.workspace;

import entities.Course;
import usecases.*;
import entities.*;
import usecases.workspace.*;


/**
 * Controller for the course workspace use case.
 * Handles user input and delegates to the workspace interactor.
 */
public class CourseController {
    private final CourseWorkspaceInputBoundary  courseWorkspaceInteractor;

    /**
     * Constructs a CourseController with the given input boundary.
     *
     * @param courseWorkspaceInteractor the interactor for workspace operations
     */
    public CourseController(CourseWorkspaceInputBoundary  courseWorkspaceInteractor) {
        this.courseWorkspaceInteractor = courseWorkspaceInteractor;
    }

    /**
     * Displays a course in the workspace view.
     *
     * @param courseId the ID of the course to display
     */
    public void displayCourse(String courseId){
        this.courseWorkspaceInteractor.findCourseById(courseId, false);
    }

    /**
     * Opens a course for editing.
     *
     * @param courseId the ID of the course to edit
     */
    public void editCourse(String courseId){
        this.courseWorkspaceInteractor.findCourseById(courseId, true);
    }

    /**
     * Creates a new course with the given information.
     *
     * @param courseId the ID of the course to create
     * @param name the name of the course
     * @param description the description of the course
     */
    public void createCourse(String courseId, String name, String description){
        Course course = new Course(courseId, name, description);
        this.courseWorkspaceInteractor.createCourse(course);
    }
    /**
     * Updates an existing course with the given information.
     *
     * @param courseId the ID of the course to update
     * @param name the new name of the course
     * @param description the new description of the course
     */
    public void updateCourse(String courseId, String name, String description){
        Course course = new Course(courseId, name, description);
        this.courseWorkspaceInteractor.updateCourse(course);
    }

    /**
     * Deletes a course by its ID.
     *
     * @param courseId the ID of the course to delete
     */
    public void deleteCourse(String courseId){
        this.courseWorkspaceInteractor.deleteCourse(courseId);
    }

}

