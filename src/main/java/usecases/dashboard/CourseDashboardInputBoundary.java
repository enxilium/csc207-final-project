package usecases.dashboard;

/**
 * Input boundary for the course dashboard use case.
 * Defines the interface for dashboard-related operations.
 */
public interface CourseDashboardInputBoundary {
    /**
     * Retrieves all courses and prepares the dashboard view.
     */
    void getCourses();
    /**
     * Prepares the view for creating a new course.
     */
    void createCourse();
}
