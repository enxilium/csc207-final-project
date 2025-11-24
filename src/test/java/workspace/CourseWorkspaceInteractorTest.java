package workspace;

import entities.Course;
import org.junit.jupiter.api.Test;
import usecases.ICourseRepository;
import usecases.dashboard.CourseDashboardOutputBoundary;
import usecases.dashboard.CourseDashboardOutputData;
import usecases.workspace.CourseWorkspaceInteractor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CourseWorkspaceInteractor, focused on deleteCourse.
 */
public class CourseWorkspaceInteractorTest {

    /**
     * Simple in-memory fake repository for tests.
     */
    private static class FakeCourseRepository implements ICourseRepository {

        final List<Course> courses = new ArrayList<>();
        String lastDeletedId = null;

        @Override
        public void create(Course course) {
            courses.add(course);
        }

        @Override
        public void update(Course course) {
            delete(course.getCourseId());
            courses.add(course);
        }

        @Override
        public Course findById(String courseId) {
            for (Course c : courses) {
                if (c.getCourseId().equals(courseId)) {
                    return c;
                }
            }
            return null;
        }

        @Override
        public List<Course> findAll() {
            return new ArrayList<>(courses);
        }

        @Override
        public void delete(String courseId) {
            lastDeletedId = courseId;
            courses.removeIf(c -> c.getCourseId().equals(courseId));
        }
    }

    /**
     * Mock dashboard presenter to verify that the dashboard gets updated
     * after deleting a course.
     */
    private static class DashboardPresenterMock implements CourseDashboardOutputBoundary {

        CourseDashboardOutputData lastDashboardData;
        String lastErrorMessage;
        boolean createCourseViewCalled;

        @Override
        public void prepareDashboardView(CourseDashboardOutputData outputData) {
            lastDashboardData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            lastErrorMessage = errorMessage;
        }

        @Override
        public void prepareCreateCourseView() {
            createCourseViewCalled = true;
        }
    }

    @Test
    void deleteCourse_nullId_throwsIllegalArgumentException() {
        FakeCourseRepository repo = new FakeCourseRepository();
        DashboardPresenterMock dashboardPresenter = new DashboardPresenterMock();

        // workspacePresenter is null here because deleteCourse doesn't use it
        CourseWorkspaceInteractor interactor =
                new CourseWorkspaceInteractor(repo, null, dashboardPresenter);

        assertThrows(IllegalArgumentException.class,
                () -> interactor.deleteCourse(null),
                "deleteCourse(null) should throw IllegalArgumentException");
    }

    @Test
    void deleteCourse_validId_deletesAndUpdatesDashboard() {
        FakeCourseRepository repo = new FakeCourseRepository();
        DashboardPresenterMock dashboardPresenter = new DashboardPresenterMock();

        Course c1 = new Course("CSC207", "Software Design", "desc");
        Course c2 = new Course("CSC165", "Math for CS", "desc");
        repo.courses.add(c1);
        repo.courses.add(c2);

        CourseWorkspaceInteractor interactor =
                new CourseWorkspaceInteractor(repo, null, dashboardPresenter);

        // Act
        interactor.deleteCourse("CSC207");

        // Assert repository state
        assertEquals("CSC207", repo.lastDeletedId);
        assertEquals(1, repo.courses.size());
        assertEquals("CSC165", repo.courses.get(0).getCourseId());

        // Assert dashboard updated with new list
        assertNotNull(dashboardPresenter.lastDashboardData,
                "Dashboard data should be updated after deletion");
        assertEquals(1, dashboardPresenter.lastDashboardData.size());
        assertEquals("CSC165",
                dashboardPresenter.lastDashboardData.getCourses().get(0).getCourseId());
    }
}
