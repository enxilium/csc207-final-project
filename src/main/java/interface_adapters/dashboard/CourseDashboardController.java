package interface_adapters.dashboard;

import usecases.dashboard.*;


public class CourseDashboardController {
    private final CourseDashboardInputBoundary courseDashboardInputBoundary;

    public CourseDashboardController(CourseDashboardInputBoundary  courseDashboardInputBoundary) {
        this.courseDashboardInputBoundary = courseDashboardInputBoundary;
    }

    public void displayCourses(){
        this.courseDashboardInputBoundary.getCourses();
    }

    public void createCourse(){
        this.courseDashboardInputBoundary.createCourse();
    }

}
