package interface_adapters.dashboard;

import usecases.dashboard.*;


/**
 * Controller for the course dashboard use case.
 * Handles user input and delegates to the dashboard interactor.
 */
public class CourseDashboardController {
    private final CourseDashboardInputBoundary courseDashboardInputBoundary;

    /**
     * Constructs a CourseDashboardController with the given input boundary.
     *
     * @param courseDashboardInputBoundary the interactor for dashboard operations
     */
    public CourseDashboardController(CourseDashboardInputBoundary  courseDashboardInputBoundary) {
        this.courseDashboardInputBoundary = courseDashboardInputBoundary;
    }

    /**
     * Displays all courses by delegating to the dashboard interactor.
     */
    public void displayCourses(){
        this.courseDashboardInputBoundary.getCourses();
    }

    /**
     * Initiates the course creation flow by delegating to the dashboard interactor.
     */
    public void createCourse(){
        this.courseDashboardInputBoundary.createCourse();
    }

}
