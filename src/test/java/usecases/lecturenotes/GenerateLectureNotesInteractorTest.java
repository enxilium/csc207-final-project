// src/test/java/usecases/lecturenotes/GenerateLectureNotesInteractorTest.java
package usecases.lecturenotes;

import entities.Course;
import entities.LectureNotes;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class GenerateLectureNotesInteractorTest {

    @Test
    public void execute_success_callsSuccessView() {
        // Arrange
        final Course course = new Course("CSC207", "demo course", "CSC207");

        CourseLookupGateway courseGateway = courseId -> {
            assertEquals("CSC207", courseId);
            return course;
        };

        final boolean[] notesGatewayCalled = {false};
        NotesGeminiGateway notesGateway = (c, topic) -> {
            notesGatewayCalled[0] = true;
            assertSame(course, c);
            assertEquals("Recursion", topic);
            return makeLectureNotes(course, "Recursion", "Generated notes content");
        };

        TestPresenter presenter = new TestPresenter();
        GenerateLectureNotesInteractor interactor =
                new GenerateLectureNotesInteractor(courseGateway, notesGateway, presenter);

        // Act
        interactor.execute(new GenerateLectureNotesInputData("CSC207", "Recursion"));

        // Assert (interactor behavior)
        assertTrue(notesGatewayCalled[0]);
        assertTrue(presenter.successCalled);
        assertFalse(presenter.failCalled);
        assertNotNull(presenter.outputData);
        assertNull(presenter.failMessage);

        // Also cover OutputData getters (so OutputData hits 100% too)
        assertEquals("CSC207", presenter.outputData.getCourseId());
        assertEquals("Recursion", presenter.outputData.getTopic());
        assertEquals("Generated notes content", presenter.outputData.getNotesText());
    }

    @Test
    public void execute_courseNotFound_callsFailView_andDoesNotCallNotesGateway() {
        // Arrange
        CourseLookupGateway courseGateway = courseId -> null;

        final boolean[] notesGatewayCalled = {false};
        NotesGeminiGateway notesGateway = (c, topic) -> {
            notesGatewayCalled[0] = true;
            fail("notesGateway should NOT be called when course is null");
            return null;
        };

        TestPresenter presenter = new TestPresenter();
        GenerateLectureNotesInteractor interactor =
                new GenerateLectureNotesInteractor(courseGateway, notesGateway, presenter);

        // Act
        interactor.execute(new GenerateLectureNotesInputData("MISSING101", "Any topic"));

        // Assert
        assertFalse(notesGatewayCalled[0]);
        assertFalse(presenter.successCalled);
        assertTrue(presenter.failCalled);
        assertEquals("Course not found: MISSING101", presenter.failMessage);
        assertNull(presenter.outputData);
    }

    @Test
    public void execute_notesGatewayThrows_callsFailView() {
        // Arrange
        final Course course = new Course("CSC207", "demo course", "CSC207");

        CourseLookupGateway courseGateway = courseId -> course;

        NotesGeminiGateway notesGateway = (c, topic) -> {
            // Cover NotesGenerationException path, but interactor catches generic Exception anyway
            throw new NotesGenerationException("Gemini failure");
        };

        TestPresenter presenter = new TestPresenter();
        GenerateLectureNotesInteractor interactor =
                new GenerateLectureNotesInteractor(courseGateway, notesGateway, presenter);

        // Act
        interactor.execute(new GenerateLectureNotesInputData("CSC207", "Recursion"));

        // Assert (must match interactor string exactly)
        assertFalse(presenter.successCalled);
        assertTrue(presenter.failCalled);
        assertEquals("Failed to generate lecture notes. Please try again.", presenter.failMessage);
        assertNull(presenter.outputData);
    }

    @Test
    public void notesGenerationException_constructors_setMessageAndCause() {
        NotesGenerationException e1 = new NotesGenerationException("msg");
        assertEquals("msg", e1.getMessage());

        Throwable cause = new RuntimeException("root");
        NotesGenerationException e2 = new NotesGenerationException("msg2", cause);
        assertEquals("msg2", e2.getMessage());
        assertSame(cause, e2.getCause());
    }

    private static class TestPresenter implements GenerateLectureNotesOutputBoundary {
        boolean successCalled = false;
        boolean failCalled = false;
        GenerateLectureNotesOutputData outputData = null;
        String failMessage = null;

        @Override
        public void prepareSuccessView(GenerateLectureNotesOutputData outputData) {
            this.successCalled = true;
            this.outputData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.failCalled = true;
            this.failMessage = errorMessage;
        }
    }

    /**
     * Robustly constructs LectureNotes without assuming a single constructor signature.
     * Supports common patterns like:
     * - (String courseId, String topic, String content)
     * - (String courseId, String topic, String content, LocalDateTime time)
     * - (Course course, String topic, String content)
     * - (Course course, String topic, String content, LocalDateTime time)
     */
    private static LectureNotes makeLectureNotes(Course course, String topic, String content) {
        try {
            for (Constructor<?> ctor : LectureNotes.class.getConstructors()) {
                Class<?>[] p = ctor.getParameterTypes();

                // (String, String, String)
                if (p.length == 3 && p[0] == String.class && p[1] == String.class && p[2] == String.class) {
                    return (LectureNotes) ctor.newInstance(course.getCourseId(), topic, content);
                }

                // (String, String, String, LocalDateTime)
                if (p.length == 4
                        && p[0] == String.class
                        && p[1] == String.class
                        && p[2] == String.class
                        && p[3] == LocalDateTime.class) {
                    return (LectureNotes) ctor.newInstance(course.getCourseId(), topic, content, LocalDateTime.now());
                }

                // (Course, String, String)
                if (p.length == 3 && p[0] == Course.class && p[1] == String.class && p[2] == String.class) {
                    return (LectureNotes) ctor.newInstance(course, topic, content);
                }

                // (Course, String, String, LocalDateTime)
                if (p.length == 4
                        && p[0] == Course.class
                        && p[1] == String.class
                        && p[2] == String.class
                        && p[3] == LocalDateTime.class) {
                    return (LectureNotes) ctor.newInstance(course, topic, content, LocalDateTime.now());
                }
            }

            fail("No supported LectureNotes constructor found. Update makeLectureNotes() to match your entity.");
            return null;
        } catch (Exception e) {
            throw new AssertionError("Failed to construct LectureNotes for test.", e);
        }
    }
}
