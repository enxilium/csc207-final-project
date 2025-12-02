// src/test/java/interface_adapters/lecturenotes/GenerateLectureNotesPresenterTest.java
package interface_adapters.lecturenotes;

import interface_adapters.ViewManagerModel;
import org.junit.jupiter.api.Test;
import usecases.lecturenotes.GenerateLectureNotesInputBoundary;
import usecases.lecturenotes.GenerateLectureNotesInputData;
import usecases.lecturenotes.GenerateLectureNotesOutputData;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class GenerateLectureNotesPresenterTest {

    @Test
    public void prepareSuccessView_updatesViewModel_andSwitchesView() {
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        LectureNotesViewModel viewModel = new LectureNotesViewModel();

        LectureNotesState initialState = viewModel.getState();
        initialState.setCourseId("OLD");
        initialState.setTopic("OLD");
        initialState.setNotesText("old notes");
        initialState.setError("old error");
        initialState.setLoading(true);
        viewModel.setState(initialState);

        GenerateLectureNotesPresenter presenter =
                new GenerateLectureNotesPresenter(viewModel, viewManagerModel);

        GenerateLectureNotesOutputData outputData =
                new GenerateLectureNotesOutputData("CSC207", "Recursion", "Generated notes for recursion");

        presenter.prepareSuccessView(outputData);

        assertEquals(viewModel.getViewName(), viewManagerModel.getState());
        Object state = invokeNoArg(viewModel, "getState");
        assertNotNull(state);

        assertEquals("CSC207", readString(state, "getCourseId", "courseId"));
        assertEquals("Recursion", readString(state, "getTopic", "topic"));
        assertEquals("Generated notes for recursion",
                readString(state, "getNotesText", "getContent", "notesText", "content"));

        Boolean loading = readNullableBoolean(state, "isLoading", "getLoading", "loading");
        if (loading != null) {
            assertFalse(loading);
        }

        String error = readNullableString(state, "getError", "error");
        if (error != null) {
            assertTrue(error.isEmpty());
        }

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

        String afterView = readNullableString(viewManagerModel, "getState", "state");
        if (beforeView != null && afterView != null) {
            assertEquals(beforeView, afterView);
        }
    }

    @Test
    public void controller_execute_passesInputDataToInteractor() {
        final boolean[] called = {false};

        GenerateLectureNotesInputBoundary interactor = new GenerateLectureNotesInputBoundary() {
            @Override
            public void execute(GenerateLectureNotesInputData inputData) {
                called[0] = true;
                assertNotNull(inputData);
                assertEquals("CSC207", inputData.getCourseId());
                assertEquals("Recursion", inputData.getTopic());
            }
        };

        GenerateLectureNotesController controller = new GenerateLectureNotesController(interactor);
        controller.execute("CSC207", "Recursion");

        assertTrue(called[0]);
    }

    @Test
    public void viewModel_setState_firesPropertyChange_and_listener_canBeRemoved() {
        LectureNotesViewModel vm = new LectureNotesViewModel();

        // cover getViewName()
        assertEquals("lecture_notes", vm.getViewName());

        final int[] eventCount = {0};
        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("state".equals(evt.getPropertyName())) {
                    eventCount[0]++;
                }
            }
        };

        vm.addPropertyChangeListener(listener);

        LectureNotesState newState = new LectureNotesState();
        newState.setCourseId("CSC207");
        newState.setTopic("Recursion");
        newState.setNotesText("hi");
        vm.setState(newState);

        assertEquals(1, eventCount[0]);

        // cover firePropertyChange() explicitly
        vm.firePropertyChange();
        assertEquals(2, eventCount[0]);

        // cover removePropertyChangeListener()
        vm.removePropertyChangeListener(listener);
        vm.firePropertyChange();
        assertEquals(2, eventCount[0]); // no longer increments
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
            }

            try {
                Field f = target.getClass().getDeclaredField(name);
                f.setAccessible(true);
                Object v = f.get(target);
                if (v instanceof String) {
                    return (String) v;
                }
            } catch (Exception ignored) {
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
            }

            try {
                Field f = target.getClass().getDeclaredField(name);
                f.setAccessible(true);
                Object v = f.get(target);
                if (v instanceof Boolean) {
                    return (Boolean) v;
                }
            } catch (Exception ignored) {
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
