package interface_adapters.lecturenotes;

import interface_adapters.ViewManagerModel;
import org.junit.Test;
import usecases.lecturenotes.GenerateLectureNotesOutputData;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * Unit tests for GenerateLectureNotesPresenter.
 * These tests verify that the presenter updates the
 * LectureNotesViewModel state and the ViewManagerModel correctly.
 */
public class GenerateLectureNotesPresenterTest {

    @Test
    public void prepareSuccessView_updatesViewModelAndViewManager() {
        // Arrange: create view models
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        LectureNotesViewModel viewModel = new LectureNotesViewModel();

        // Put some dummy initial state to make sure it gets overwritten
        LectureNotesState initialState = viewModel.getState();
        initialState.setCourseId("OLD");
        initialState.setTopic("OLD");
        initialState.setNotesText("old notes");
        initialState.setError("old error");
        initialState.setLoading(true);
        viewModel.setState(initialState);

        GenerateLectureNotesPresenter presenter =
                new GenerateLectureNotesPresenter(viewModel, viewManagerModel);

        // Output data coming from the use case
        GenerateLectureNotesOutputData outputData =
                new GenerateLectureNotesOutputData(
                        "CSC207",
                        "Recursion",
                        "Generated notes for recursion",
                        LocalDateTime.now()
                );

        // Act: call the presenter
        presenter.prepareSuccessView(outputData);

        // Assert: view model state is updated
        LectureNotesState updatedState = viewModel.getState();
        assertEquals("CSC207", updatedState.getCourseId());
        assertEquals("Recursion", updatedState.getTopic());
        assertEquals("Generated notes for recursion", updatedState.getNotesText());
        assertEquals("", updatedState.getError());
        assertFalse(updatedState.isLoading());

        // Assert: view manager switches to the lecture-notes view
        assertEquals(LectureNotesViewModel.VIEW_NAME, viewManagerModel.getState());
    }

    @Test
    public void prepareFailView_setsErrorAndStopsLoading() {
        // Arrange
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        LectureNotesViewModel viewModel = new LectureNotesViewModel();

        LectureNotesState initialState = viewModel.getState();
        initialState.setCourseId("CSC207");
        initialState.setTopic("Recursion");
        initialState.setNotesText("some notes");
        initialState.setError("");
        initialState.setLoading(true);
        viewModel.setState(initialState);

        GenerateLectureNotesPresenter presenter =
                new GenerateLectureNotesPresenter(viewModel, viewManagerModel);

        String errorMessage = "Course not found";

        // Act
        presenter.prepareFailView(errorMessage);

        // Assert: error is set and loading is false
        LectureNotesState updatedState = viewModel.getState();
        assertEquals(errorMessage, updatedState.getError());
        assertFalse(updatedState.isLoading());

        // We do NOT expect the active view to change here,
        // so we simply check that the state is not equal to the lecture notes view name.
        // (If your design expects a switch, you can change this assertion.)
        assertNotEquals(LectureNotesViewModel.VIEW_NAME, viewManagerModel.getState());
    }
}