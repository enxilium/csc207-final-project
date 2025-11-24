package dashboard;

import entities.Course;
import org.junit.jupiter.api.Test;
import usecases.ICourseRepository;
import usecases.dashboard.CourseDashboardInteractor;
import usecases.dashboard.CourseDashboardOutputBoundary;
import usecases.dashboard.CourseDashboardOutputData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CourseDashboardInteractor.
 *
 * These tests only touch:
 *  - the interactor
 *  - a fake ICourseRepository
 *  - a mock CourseDashboardOutputBoundary
 *
 * No Swing, no files, no JSON.
 */
public class CourseDashboardInteractorTest {

    /**
     * Simple in-memory fake repository for tests.
     */
    private static class FakeCourseRepository implements ICourseRepository {

        final List<Course> courses = new ArrayList<>();
        boolean findAllCalled = false;

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
            findAllCalled = true;
            return new ArrayList<>(courses); // defensive copy
        }

        @Override
        public void delete(String courseId) {
            courses.removeIf(c -> c.getCourseId().equals(courseId));
        }
    }

    /**
     * Mock presenter that just records what was called.
     */
    private static class DashboardPresenterMock implements CourseDashboardOutputBoundary {

        CourseDashboardOutputData lastDashboardData;
        String lastErrorMessage;
        boolean createCourseViewCalled = false;

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
    void getCourses_withExistingCourses_callsDashboardView() {
        // Arrange
        FakeCourseRepository repo = new FakeCourseRepository();
        DashboardPresenterMock presenter = new DashboardPresenterMock();

        repo.courses.add(new Course("CSC207", "Software Design", "OOP & Clean Architecture"));
        repo.courses.add(new Course("CSC165", "Math for CS", "Logic and proofs"));

        CourseDashboardInteractor interactor =
                new CourseDashboardInteractor(repo, presenter);

        // Act
        interactor.getCourses();

        // Assert
        assertTrue(repo.findAllCalled, "findAll should be called on repository");
        assertNull(presenter.lastErrorMessage, "No error should be reported");
        assertNotNull(presenter.lastDashboardData, "Dashboard data should be set");

        List<Course> returned = presenter.lastDashboardData.getCourses();
        assertEquals(2, returned.size());
        assertEquals("CSC207", returned.get(0).getCourseId());
        assertEquals("CSC165", returned.get(1).getCourseId());
    }

    @Test
    void getCourses_withNoCourses_callsFailView() {
        // Arrange: empty repo
        FakeCourseRepository repo = new FakeCourseRepository();
        DashboardPresenterMock presenter = new DashboardPresenterMock();

        CourseDashboardInteractor interactor =
                new CourseDashboardInteractor(repo, presenter);

        // Act
        interactor.getCourses();

        // Assert
        assertNull(presenter.lastDashboardData, "No dashboard data expected");
        assertEquals("There is no course", presenter.lastErrorMessage);
    }

    @Test
    void createCourse_callsPrepareCreateCourseView() {
        FakeCourseRepository repo = new FakeCourseRepository();
        DashboardPresenterMock presenter = new DashboardPresenterMock();

        CourseDashboardInteractor interactor =
                new CourseDashboardInteractor(repo, presenter);

        // Act
        interactor.createCourse();

        // Assert
        assertTrue(presenter.createCourseViewCalled,
                "Presenter should be told to show the Create Course view");
    }
}