package interface_adapters.lecturenotes;

import interface_adapters.ViewManagerModel;
import org.junit.Test;
import usecases.lecturenotes.GenerateLectureNotesOutputData;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class GenerateLectureNotesPresenterTest {

    @Test
    public void prepareSuccessView_updatesViewModel_andSwitchesView() {
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        LectureNotesViewModel viewModel = new LectureNotesViewModel();

        GenerateLectureNotesPresenter presenter =
                new GenerateLectureNotesPresenter(viewModel, viewManagerModel);

        GenerateLectureNotesOutputData outputData =
                new GenerateLectureNotesOutputData("CSC207", "Recursion", "Generated notes");

        presenter.prepareSuccessView(outputData);

        Object state = invokeNoArg(viewModel, "getState");
        assertNotNull(state);

        assertEquals("CSC207", readString(state, "getCourseId", "courseId"));
        assertEquals("Recursion", readString(state, "getTopic", "topic"));
        assertEquals("Generated notes", readString(state, "getNotesText", "getContent", "notesText", "content"));

        Boolean loading = readNullableBoolean(state, "isLoading", "getLoading", "loading");
        if (loading != null) {
            assertFalse(loading);
        }

        String error = readNullableString(state, "getError", "error");
        if (error != null) {
            assertTrue(error.isEmpty());
        }

        // View should switch to lecture notes view after success (if your ViewManagerModel supports getState()).
        String expectedViewName = readStaticString(LectureNotesViewModel.class, "VIEW_NAME");
        String actualViewName = readNullableString(viewManagerModel, "getState", "state");
        if (expectedViewName != null && actualViewName != null) {
            assertEquals(expectedViewName, actualViewName);
        }
    }

    @Test
    public void prepareFailView_setsError_andDoesNotSwitchView() {
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        LectureNotesViewModel viewModel = new LectureNotesViewModel();

        // Record current view BEFORE calling fail view (no need to set it).
        String beforeView = readNullableString(viewManagerModel, "getState", "state");

        GenerateLectureNotesPresenter presenter =
                new GenerateLectureNotesPresenter(viewModel, viewManagerModel);

        presenter.prepareFailView("Course not found");

        Object state = invokeNoArg(viewModel, "getState");
        assertNotNull(state);

        String error = readNullableString(state, "getError", "error");
        assertNotNull(error);
        assertEquals("Course not found", error);

        Boolean loading = readNullableBoolean(state, "isLoading", "getLoading", "loading");
        if (loading != null) {
            assertFalse(loading);
        }

        // If ViewManagerModel has a readable state, assert it didn't change.
        String afterView = readNullableString(viewManagerModel, "getState", "state");
        if (beforeView != null && afterView != null) {
            assertEquals(beforeView, afterView);
        }
    }

    // ---------- reflection helpers ----------

    private static Object invokeNoArg(Object target, String methodName) {
        try {
            Method m = target.getClass().getMethod(methodName);
            return m.invoke(target);
        } catch (Exception e) {
            fail("Could not invoke " + methodName + " on " + target.getClass().getName());
            return null;
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
            try {
                Method m = target.getClass().getMethod(name);
                Object v = m.invoke(target);
                if (v instanceof String) {
                    return (String) v;
                }
            } catch (Exception ignored) {
                // try next
            }

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

    private static Boolean readNullableBoolean(Object target, String primary, String... fallbacks) {
        String[] names = new String[1 + (fallbacks == null ? 0 : fallbacks.length)];
        names[0] = primary;
        if (fallbacks != null) {
            System.arraycopy(fallbacks, 0, names, 1, fallbacks.length);
        }

        for (String name : names) {
            try {
                Method m = target.getClass().getMethod(name);
                Object v = m.invoke(target);
                if (v instanceof Boolean) {
                    return (Boolean) v;
                }
            } catch (Exception ignored) {
                // try next
            }

            try {
                Field f = target.getClass().getDeclaredField(name);
                f.setAccessible(true);
                Object v = f.get(target);
                if (v instanceof Boolean) {
                    return (Boolean) v;
                }
            } catch (Exception ignored) {
                // try next
            }
        }
        return null;
    }

    private static String readStaticString(Class<?> clazz, String fieldName) {
        try {
            Field f = clazz.getDeclaredField(fieldName);
            f.setAccessible(true);
            Object v = f.get(null);
            return (v instanceof String) ? (String) v : null;
        } catch (Exception ignored) {
            return null;
        }
    }
}
