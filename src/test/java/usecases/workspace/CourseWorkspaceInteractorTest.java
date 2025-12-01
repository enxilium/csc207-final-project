package usecases.workspace;

import entities.Course;
import org.junit.jupiter.api.Test;
import usecases.ICourseRepository;
import usecases.dashboard.CourseDashboardOutputBoundary;
import usecases.dashboard.CourseDashboardOutputData;
import usecases.workspace.CourseWorkspaceInteractor;
import usecases.workspace.CourseWorkspaceOutputBoundary;
import usecases.workspace.CourseWorkspaceOutputData;

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

    /**
     * Mock workspace presenter to verify workspace operations.
     */
    private static class WorkspacePresenterMock implements CourseWorkspaceOutputBoundary {

        CourseWorkspaceOutputData lastWorkspaceData;
        CourseWorkspaceOutputData lastEditData;
        String lastErrorMessage;

        @Override
        public void prepareWorkspaceView(CourseWorkspaceOutputData outputData) {
            lastWorkspaceData = outputData;
        }

        @Override
        public void prepareEditView(CourseWorkspaceOutputData outputData) {
            lastEditData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            lastErrorMessage = errorMessage;
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

    @Test
    void findCourseById_courseFound_isEditFalse_callsWorkspaceView() {
        FakeCourseRepository repo = new FakeCourseRepository();
        WorkspacePresenterMock workspacePresenter = new WorkspacePresenterMock();
        DashboardPresenterMock dashboardPresenter = new DashboardPresenterMock();

        Course c1 = new Course("CSC207", "Software Design", "desc");
        repo.courses.add(c1);

        CourseWorkspaceInteractor interactor =
                new CourseWorkspaceInteractor(repo, workspacePresenter, dashboardPresenter);

        // Act
        interactor.findCourseById("CSC207", false);

        // Assert
        assertNotNull(workspacePresenter.lastWorkspaceData, "Workspace data should be set");
        assertEquals("CSC207", workspacePresenter.lastWorkspaceData.getCourse().getCourseId());
        assertNull(workspacePresenter.lastEditData, "Edit view should not be called");
        assertNull(workspacePresenter.lastErrorMessage, "No error should be reported");
    }

    @Test
    void findCourseById_courseFound_isEditTrue_callsEditView() {
        FakeCourseRepository repo = new FakeCourseRepository();
        WorkspacePresenterMock workspacePresenter = new WorkspacePresenterMock();
        DashboardPresenterMock dashboardPresenter = new DashboardPresenterMock();

        Course c1 = new Course("CSC207", "Software Design", "desc");
        repo.courses.add(c1);

        CourseWorkspaceInteractor interactor =
                new CourseWorkspaceInteractor(repo, workspacePresenter, dashboardPresenter);

        // Act
        interactor.findCourseById("CSC207", true);

        // Assert
        assertNotNull(workspacePresenter.lastEditData, "Edit data should be set");
        assertEquals("CSC207", workspacePresenter.lastEditData.getCourse().getCourseId());
        assertNull(workspacePresenter.lastWorkspaceData, "Workspace view should not be called");
        assertNull(workspacePresenter.lastErrorMessage, "No error should be reported");
    }

    @Test
    void findCourseById_courseNotFound_callsFailView() {
        FakeCourseRepository repo = new FakeCourseRepository();
        WorkspacePresenterMock workspacePresenter = new WorkspacePresenterMock();
        DashboardPresenterMock dashboardPresenter = new DashboardPresenterMock();

        CourseWorkspaceInteractor interactor =
                new CourseWorkspaceInteractor(repo, workspacePresenter, dashboardPresenter);

        // Act
        interactor.findCourseById("NONEXISTENT", false);

        // Assert
        assertNull(workspacePresenter.lastWorkspaceData, "No workspace data expected");
        assertNull(workspacePresenter.lastEditData, "No edit data expected");
        assertEquals("There is no course", workspacePresenter.lastErrorMessage);
    }

    @Test
    void createCourse_nullCourse_throwsIllegalArgumentException() {
        FakeCourseRepository repo = new FakeCourseRepository();
        WorkspacePresenterMock workspacePresenter = new WorkspacePresenterMock();
        DashboardPresenterMock dashboardPresenter = new DashboardPresenterMock();

        CourseWorkspaceInteractor interactor =
                new CourseWorkspaceInteractor(repo, workspacePresenter, dashboardPresenter);

        assertThrows(IllegalArgumentException.class,
                () -> interactor.createCourse(null),
                "createCourse(null) should throw IllegalArgumentException");
    }

    @Test
    void createCourse_nullCourseId_throwsIllegalArgumentException() {
        FakeCourseRepository repo = new FakeCourseRepository();
        WorkspacePresenterMock workspacePresenter = new WorkspacePresenterMock();
        DashboardPresenterMock dashboardPresenter = new DashboardPresenterMock();

        CourseWorkspaceInteractor interactor =
                new CourseWorkspaceInteractor(repo, workspacePresenter, dashboardPresenter);

        Course courseWithNullId = new Course(null, "Name", "Description");

        assertThrows(IllegalArgumentException.class,
                () -> interactor.createCourse(courseWithNullId),
                "createCourse with null courseId should throw IllegalArgumentException");
    }

    @Test
    void createCourse_duplicateCourseId_throwsRuntimeException() {
        FakeCourseRepository repo = new FakeCourseRepository();
        WorkspacePresenterMock workspacePresenter = new WorkspacePresenterMock();
        DashboardPresenterMock dashboardPresenter = new DashboardPresenterMock();

        Course existingCourse = new Course("CSC207", "Existing", "desc");
        repo.courses.add(existingCourse);

        CourseWorkspaceInteractor interactor =
                new CourseWorkspaceInteractor(repo, workspacePresenter, dashboardPresenter);

        Course duplicateCourse = new Course("CSC207", "Duplicate", "desc");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> interactor.createCourse(duplicateCourse),
                "createCourse with duplicate courseId should throw RuntimeException");
        assertTrue(exception.getMessage().contains("course already exist"));
        assertTrue(exception.getMessage().contains("CSC207"));
    }

    @Test
    void createCourse_validCourse_callsWorkspaceView() {
        FakeCourseRepository repo = new FakeCourseRepository();
        WorkspacePresenterMock workspacePresenter = new WorkspacePresenterMock();
        DashboardPresenterMock dashboardPresenter = new DashboardPresenterMock();

        CourseWorkspaceInteractor interactor =
                new CourseWorkspaceInteractor(repo, workspacePresenter, dashboardPresenter);

        Course newCourse = new Course("CSC207", "Software Design", "desc");

        // Act
        interactor.createCourse(newCourse);

        // Assert
        assertEquals(1, repo.courses.size());
        assertEquals("CSC207", repo.courses.get(0).getCourseId());
        assertNotNull(workspacePresenter.lastWorkspaceData, "Workspace data should be set");
        assertEquals("CSC207", workspacePresenter.lastWorkspaceData.getCourse().getCourseId());
        assertNull(workspacePresenter.lastErrorMessage, "No error should be reported");
    }

    @Test
    void updateCourse_nullCourse_throwsIllegalArgumentException() {
        FakeCourseRepository repo = new FakeCourseRepository();
        WorkspacePresenterMock workspacePresenter = new WorkspacePresenterMock();
        DashboardPresenterMock dashboardPresenter = new DashboardPresenterMock();

        CourseWorkspaceInteractor interactor =
                new CourseWorkspaceInteractor(repo, workspacePresenter, dashboardPresenter);

        assertThrows(IllegalArgumentException.class,
                () -> interactor.updateCourse(null),
                "updateCourse(null) should throw IllegalArgumentException");
    }

    @Test
    void updateCourse_nullCourseId_throwsIllegalArgumentException() {
        FakeCourseRepository repo = new FakeCourseRepository();
        WorkspacePresenterMock workspacePresenter = new WorkspacePresenterMock();
        DashboardPresenterMock dashboardPresenter = new DashboardPresenterMock();

        CourseWorkspaceInteractor interactor =
                new CourseWorkspaceInteractor(repo, workspacePresenter, dashboardPresenter);

        Course courseWithNullId = new Course(null, "Name", "Description");

        assertThrows(IllegalArgumentException.class,
                () -> interactor.updateCourse(courseWithNullId),
                "updateCourse with null courseId should throw IllegalArgumentException");
    }

    @Test
    void updateCourse_courseDoesNotExist_throwsRuntimeException() {
        FakeCourseRepository repo = new FakeCourseRepository();
        WorkspacePresenterMock workspacePresenter = new WorkspacePresenterMock();
        DashboardPresenterMock dashboardPresenter = new DashboardPresenterMock();

        CourseWorkspaceInteractor interactor =
                new CourseWorkspaceInteractor(repo, workspacePresenter, dashboardPresenter);

        Course nonExistentCourse = new Course("NONEXISTENT", "Name", "desc");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> interactor.updateCourse(nonExistentCourse),
                "updateCourse with non-existent courseId should throw RuntimeException");
        assertTrue(exception.getMessage().contains("course does not exist"));
        assertTrue(exception.getMessage().contains("NONEXISTENT"));
    }

    @Test
    void updateCourse_validCourse_callsWorkspaceView() {
        FakeCourseRepository repo = new FakeCourseRepository();
        WorkspacePresenterMock workspacePresenter = new WorkspacePresenterMock();
        DashboardPresenterMock dashboardPresenter = new DashboardPresenterMock();

        Course existingCourse = new Course("CSC207", "Old Name", "old desc");
        repo.courses.add(existingCourse);

        CourseWorkspaceInteractor interactor =
                new CourseWorkspaceInteractor(repo, workspacePresenter, dashboardPresenter);

        Course updatedCourse = new Course("CSC207", "New Name", "new desc");

        // Act
        interactor.updateCourse(updatedCourse);

        // Assert
        assertEquals(1, repo.courses.size());
        assertEquals("CSC207", repo.courses.get(0).getCourseId());
        assertEquals("New Name", repo.courses.get(0).getName());
        assertNotNull(workspacePresenter.lastWorkspaceData, "Workspace data should be set");
        assertEquals("CSC207", workspacePresenter.lastWorkspaceData.getCourse().getCourseId());
        assertNull(workspacePresenter.lastErrorMessage, "No error should be reported");
    }
}
