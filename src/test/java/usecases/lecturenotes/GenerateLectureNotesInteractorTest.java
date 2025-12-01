// src/test/java/usecases/lecturenotes/GenerateLectureNotesInteractorTest.java
package usecases.lecturenotes;

import entities.Course;
import entities.LectureNotes;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class GenerateLectureNotesInteractorTest {

    @Test
    public void execute_success_callsSuccessView() {
        // Arrange
        final Course course = new Course("CSC207", "demo course", "CSC207");

        CourseLookupGateway courseGateway = new CourseLookupGateway() {
            @Override
            public Course getCourseById(String courseId) {
                assertEquals("CSC207", courseId);
                return course;
            }
        };

        NotesGeminiGateway notesGateway = new NotesGeminiGateway() {
            @Override
            public LectureNotes generateNotes(Course c, String topic) throws NotesGenerationException {
                assertSame(course, c);
                assertEquals("Recursion", topic);
                return makeLectureNotes("CSC207", "Recursion", "Generated notes content",
                        LocalDateTime.of(2024, 1, 1, 12, 0));
            }
        };

        TestPresenter presenter = new TestPresenter();
        GenerateLectureNotesInteractor interactor =
                new GenerateLectureNotesInteractor(courseGateway, notesGateway, presenter);

        // Act
        interactor.execute(new GenerateLectureNotesInputData("CSC207", "Recursion"));

        // Assert
        assertTrue(presenter.successCalled);
        assertFalse(presenter.failCalled);
        assertNotNull(presenter.outputData);

        assertEquals("CSC207", readString(presenter.outputData, "getCourseId", "getCourseID", "courseId"));
        assertEquals("Recursion", readString(presenter.outputData, "getTopic", "topic"));
        assertEquals("Generated notes content",
                readString(presenter.outputData, "getNotesText", "getContent", "getNotes", "notesText", "content"));
    }

    @Test
    public void execute_courseNotFound_callsFailView_andDoesNotCallNotesGateway() {
        // Arrange
        CourseLookupGateway courseGateway = new CourseLookupGateway() {
            @Override
            public Course getCourseById(String courseId) {
                return null;
            }
        };

        NotesGeminiGateway notesGateway = new NotesGeminiGateway() {
            @Override
            public LectureNotes generateNotes(Course c, String topic) throws NotesGenerationException {
                fail("notesGateway should NOT be called when course is null");
                return null;
            }
        };

        TestPresenter presenter = new TestPresenter();
        GenerateLectureNotesInteractor interactor =
                new GenerateLectureNotesInteractor(courseGateway, notesGateway, presenter);

        // Act
        interactor.execute(new GenerateLectureNotesInputData("MISSING101", "Any topic"));

        // Assert
        assertFalse(presenter.successCalled);
        assertTrue(presenter.failCalled);
        assertEquals("Course not found: MISSING101", presenter.failMessage);
    }

    @Test
    public void execute_gatewayThrows_callsFailView() {
        // Arrange
        final Course course = new Course("CSC207", "demo course", "CSC207");

        CourseLookupGateway courseGateway = new CourseLookupGateway() {
            @Override
            public Course getCourseById(String courseId) {
                return course;
            }
        };

        NotesGeminiGateway notesGateway = new NotesGeminiGateway() {
            @Override
            public LectureNotes generateNotes(Course c, String topic) throws NotesGenerationException {
                throw new NotesGenerationException("Gemini failure");
            }
        };

        TestPresenter presenter = new TestPresenter();
        GenerateLectureNotesInteractor interactor =
                new GenerateLectureNotesInteractor(courseGateway, notesGateway, presenter);

        // Act
        interactor.execute(new GenerateLectureNotesInputData("CSC207", "Recursion"));

        // Assert
        assertFalse(presenter.successCalled);
        assertTrue(presenter.failCalled);
        assertEquals("Failed to generate lecture notes. Please try again.", presenter.failMessage);
    }

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

    private static LectureNotes makeLectureNotes(String courseId, String topic, String content, LocalDateTime t) {
        try {
            for (Constructor<?> ctor : LectureNotes.class.getConstructors()) {
                Class<?>[] p = ctor.getParameterTypes();

                if (p.length == 4
                        && p[0] == String.class
                        && p[1] == String.class
                        && p[2] == String.class
                        && p[3] == LocalDateTime.class) {
                    return (LectureNotes) ctor.newInstance(courseId, topic, content, t);
                }

                if (p.length == 3 && p[0] == String.class && p[1] == String.class && p[2] == String.class) {
                    return (LectureNotes) ctor.newInstance(courseId, topic, content);
                }
            }

            fail("No supported LectureNotes constructor found. Update makeLectureNotes() to match your entity.");
            return null;
        } catch (Exception e) {
            throw new AssertionError("Failed to construct LectureNotes for test.", e);
        }
    }

    private static String readString(Object target, String primary, String... fallbacks) {
        String v = readNullableString(target, primary, fallbacks);
        if (v == null) {
            fail("Could not read String property/field: " + primary);
        }
        return v;
    }

    private static String readNullableString(Object target, String primary, String... fallbacks) {
        String[] names = new String[1 + (fallbacks == null ? 0 : fallbacks.length)];
        names[0] = primary;
        if (fallbacks != null) {
            System.arraycopy(fallbacks, 0, names, 1, fallbacks.length);
        }

        for (String name : names) {
            // Getter (no-arg)
            try {
                Method m = target.getClass().getMethod(name);
                Object v = m.invoke(target);
                if (v instanceof String) {
                    return (String) v;
                }
            } catch (Exception ignored) {
                // try next
            }

            // Field
            try {
                Field f = target.getClass().getDeclaredField(name);
                f.setAccessible(true);
                Object v = f.get(target);
                if (v instanceof String) {
                    return (String) v;
                }
            } catch (Exception ignored) {
                // try next
            }
        }
        return null;
    }
}
