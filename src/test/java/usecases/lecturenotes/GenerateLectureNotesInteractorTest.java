package usecases.lecturenotes;

import entities.Course;
import entities.LectureNotes;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Unit tests for GenerateLectureNotesInteractor.
 * We use simple test doubles for the gateways and presenter
 * so that we do not call the real Gemini SDK or file system.
 */
public class GenerateLectureNotesInteractorTest {

    @Test
    public void execute_success_callsSuccessView() {
        // --- Arrange ---
        // Fake course returned by the course gateway
        Course course = new Course("CSC207");

        CourseLookupGateway courseGateway = new CourseLookupGateway() {
            @Override
            public Course getCourseById(String courseId) {
                // Verify the interactor passes the correct id
                assertEquals("CSC207", courseId);
                return course;
            }
        };

        // Fake Gemini gateway: always returns the same LectureNotes
        NotesGeminiGateway notesGateway = new NotesGeminiGateway() {
            @Override
            public LectureNotes generateNotes(Course c, String topic) throws Exception {
                assertSame(course, c);
                assertEquals("Recursion", topic);
                return new LectureNotes(
                        c.getCourseId(),
                        topic,
                        "Generated notes content",
                        LocalDateTime.of(2024, 1, 1, 12, 0)
                );
            }
        };

        // Test presenter to capture what the interactor sends
        TestPresenter presenter = new TestPresenter();

        GenerateLectureNotesInteractor interactor =
                new GenerateLectureNotesInteractor(courseGateway, notesGateway, presenter);

        GenerateLectureNotesInputData input =
                new GenerateLectureNotesInputData("CSC207", "Recursion");

        // --- Act ---
        interactor.execute(input);

        // --- Assert ---
        assertTrue(presenter.successCalled);
        assertFalse(presenter.failCalled);
        assertNotNull(presenter.outputData);

        assertEquals("CSC207", presenter.outputData.getCourseId());
        assertEquals("Recursion", presenter.outputData.getTopic());
        assertEquals("Generated notes content", presenter.outputData.getNotesText());
        assertEquals(
                LocalDateTime.of(2024, 1, 1, 12, 0),
                presenter.outputData.getGeneratedAt()
        );
    }

    @Test
    public void execute_courseNotFound_callsFailView() {
        // --- Arrange ---
        CourseLookupGateway courseGateway = new CourseLookupGateway() {
            @Override
            public Course getCourseById(String courseId) {
                // Simulate "course not found"
                return null;
            }
        };

        NotesGeminiGateway notesGateway = new NotesGeminiGateway() {
            @Override
            public LectureNotes generateNotes(Course c, String topic) throws Exception {
                fail("notesGateway should NOT be called when course is null");
                return null;
            }
        };

        TestPresenter presenter = new TestPresenter();

        GenerateLectureNotesInteractor interactor =
                new GenerateLectureNotesInteractor(courseGateway, notesGateway, presenter);

        GenerateLectureNotesInputData input =
                new GenerateLectureNotesInputData("MISSING101", "Any topic");

        // --- Act ---
        interactor.execute(input);

        // --- Assert ---
        assertFalse(presenter.successCalled);
        assertTrue(presenter.failCalled);
        assertEquals("Course not found: MISSING101", presenter.failMessage);
    }

    @Test
    public void execute_gatewayThrows_callsFailView() {
        // --- Arrange ---
        Course course = new Course("CSC207");

        CourseLookupGateway courseGateway = new CourseLookupGateway() {
            @Override
            public Course getCourseById(String courseId) {
                return course;
            }
        };

        NotesGeminiGateway notesGateway = new NotesGeminiGateway() {
            @Override
            public LectureNotes generateNotes(Course c, String topic) throws Exception {
                throw new RuntimeException("Gemini failure");
            }
        };

        TestPresenter presenter = new TestPresenter();

        GenerateLectureNotesInteractor interactor =
                new GenerateLectureNotesInteractor(courseGateway, notesGateway, presenter);

        GenerateLectureNotesInputData input =
                new GenerateLectureNotesInputData("CSC207", "Recursion");

        // --- Act ---
        interactor.execute(input);

        // --- Assert ---
        assertFalse(presenter.successCalled);
        assertTrue(presenter.failCalled);
        assertEquals(
                "Failed to generate lecture notes. Please try again.",
                presenter.failMessage
        );
    }

    /**
     * Simple test double for the output boundary.
     * It records whether success/fail methods were called and
     * stores the data / message for assertions.
     */
    private static class TestPresenter implements GenerateLectureNotesOutputBoundary {

        boolean successCalled = false;
        boolean failCalled = false;
        GenerateLectureNotesOutputData outputData;
        String failMessage;

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
}