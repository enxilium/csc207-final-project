package usecases.flashcards;

import data_access.FlashcardGenerator;
import entities.Flashcard;
import entities.FlashcardSet;
import interface_adapters.ViewManagerModel;
import interface_adapters.flashcards.FlashcardViewModel;
import interface_adapters.flashcards.GenerateFlashcardsController;
import interface_adapters.flashcards.GenerateFlashcardsPresenter;
import org.junit.Before;
import org.junit.Test;
import usecases.GenerateFlashcardsInputBoundary;
import usecases.GenerateFlashcardsInteractor;
import usecases.GenerateFlashcardsOutputBoundary;
import usecases.GenerateFlashcardsRequestModel;
import usecases.GenerateFlashcardsResponseModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Comprehensive test for Flashcard feature with 100% coverage
 * Uses test doubles (mocks) - no real API calls
 */
public class FlashcardTest {

    // ========== ENTITY TESTS ==========

    @Test
    public void testFlashcard_creation() {
        Flashcard card = new Flashcard("What is Java?", "A programming language");

        assertEquals("What is Java?", card.getQuestion());
        assertEquals("A programming language", card.getAnswer());
        assertEquals("What is Java?", card.getFront());
        assertEquals("A programming language", card.getBack());
    }

    @Test
    public void testFlashcard_toString() {
        Flashcard card = new Flashcard("Q1", "A1");
        assertEquals("Q: Q1 | A: A1", card.toString());
    }

    @Test
    public void testFlashcard_emptyStrings() {
        Flashcard card = new Flashcard("", "");
        assertEquals("", card.getQuestion());
        assertEquals("", card.getAnswer());
    }

    @Test
    public void testFlashcard_specialCharacters() {
        Flashcard card = new Flashcard("What is 2+2?", "4");
        assertEquals("What is 2+2?", card.getQuestion());
        assertEquals("4", card.getAnswer());
    }

    @Test
    public void testFlashcard_longStrings() {
        String longQ = "This is a very long question with lots of text";
        String longA = "This is a very long answer with lots of text";
        Flashcard card = new Flashcard(longQ, longA);

        assertEquals(longQ, card.getQuestion());
        assertEquals(longA, card.getAnswer());
    }

    @Test
    public void testFlashcardSet_creation() {
        List<Flashcard> cards = Arrays.asList(
                new Flashcard("Q1", "A1"),
                new Flashcard("Q2", "A2")
        );
        FlashcardSet set = new FlashcardSet("CSC207", cards);

        assertEquals("CSC207", set.getCourseName());
        assertEquals(2, set.size());
        assertEquals("Q1", set.getCard(0).getQuestion());
        assertEquals("A1", set.getCard(0).getAnswer());
    }

    @Test
    public void testFlashcardSet_getFlashcards() {
        List<Flashcard> cards = Arrays.asList(new Flashcard("Q", "A"));
        FlashcardSet set = new FlashcardSet("TEST", cards);

        assertEquals(cards, set.getFlashcards());
        assertEquals(1, set.size());
    }

    @Test
    public void testFlashcardSet_toString() {
        List<Flashcard> cards = Arrays.asList(new Flashcard("Q1", "A1"));
        FlashcardSet set = new FlashcardSet("CSC207", cards);
        String result = set.toString();

        assertTrue(result.contains("Flashcard Set for Course: CSC207"));
        assertTrue(result.contains("Q: Q1 | A: A1"));
    }

    @Test
    public void testFlashcardSet_emptySet() {
        FlashcardSet set = new FlashcardSet("EMPTY", new ArrayList<>());
        assertEquals(0, set.size());
        assertTrue(set.getFlashcards().isEmpty());
    }

    @Test
    public void testFlashcardSet_multipleCards() {
        List<Flashcard> cards = Arrays.asList(
                new Flashcard("Q1", "A1"),
                new Flashcard("Q2", "A2"),
                new Flashcard("Q3", "A3")
        );
        FlashcardSet set = new FlashcardSet("CSC207", cards);

        assertEquals(3, set.size());
        assertEquals("Q2", set.getCard(1).getQuestion());
        assertEquals("A3", set.getCard(2).getAnswer());
    }

    // ========== VIEW MODEL TESTS ==========

    private FlashcardViewModel viewModel;

    @Before
    public void setUp() {
        viewModel = new FlashcardViewModel();
    }

    @Test
    public void testViewModel_initialState() {
        assertNull(viewModel.getCurrentFlashcardSet());
        assertEquals(0, viewModel.getCurrentCardIndex());
        assertFalse(viewModel.isFlipped());
        assertNull(viewModel.getErrorMessage());
        assertFalse(viewModel.isLoading());
    }

    @Test
    public void testViewModel_setFlashcardSet() {
        List<Flashcard> cards = Arrays.asList(new Flashcard("Q", "A"));
        FlashcardSet set = new FlashcardSet("TEST", cards);

        viewModel.setCurrentFlashcardSet(set);

        assertEquals(set, viewModel.getCurrentFlashcardSet());
        assertEquals(0, viewModel.getCurrentCardIndex());
        assertFalse(viewModel.isFlipped());
    }

    @Test
    public void testViewModel_setCardIndex() {
        List<Flashcard> cards = Arrays.asList(
                new Flashcard("Q1", "A1"),
                new Flashcard("Q2", "A2"),
                new Flashcard("Q3", "A3")
        );
        FlashcardSet set = new FlashcardSet("TEST", cards);
        viewModel.setCurrentFlashcardSet(set);

        viewModel.setCurrentCardIndex(1);
        assertEquals(1, viewModel.getCurrentCardIndex());
        assertFalse(viewModel.isFlipped());

        viewModel.setCurrentCardIndex(2);
        assertEquals(2, viewModel.getCurrentCardIndex());
    }

    @Test
    public void testViewModel_setCardIndexOutOfBounds() {
        List<Flashcard> cards = Arrays.asList(new Flashcard("Q", "A"));
        FlashcardSet set = new FlashcardSet("TEST", cards);
        viewModel.setCurrentFlashcardSet(set);

        int initialIndex = viewModel.getCurrentCardIndex();
        viewModel.setCurrentCardIndex(10);
        assertEquals(initialIndex, viewModel.getCurrentCardIndex());

        viewModel.setCurrentCardIndex(-1);
        assertEquals(initialIndex, viewModel.getCurrentCardIndex());
    }

    @Test
    public void testViewModel_flip() {
        viewModel.setFlipped(true);
        assertTrue(viewModel.isFlipped());

        viewModel.setFlipped(false);
        assertFalse(viewModel.isFlipped());
    }

    @Test
    public void testViewModel_flipResetsOnCardChange() {
        List<Flashcard> cards = Arrays.asList(
                new Flashcard("Q1", "A1"),
                new Flashcard("Q2", "A2")
        );
        FlashcardSet set = new FlashcardSet("TEST", cards);
        viewModel.setCurrentFlashcardSet(set);

        viewModel.setFlipped(true);
        assertTrue(viewModel.isFlipped());

        viewModel.setCurrentCardIndex(1);
        assertFalse(viewModel.isFlipped());
    }

    @Test
    public void testViewModel_errorMessage() {
        viewModel.setErrorMessage("Test error");
        assertEquals("Test error", viewModel.getErrorMessage());

        viewModel.setErrorMessage(null);
        assertNull(viewModel.getErrorMessage());
    }

    @Test
    public void testViewModel_loading() {
        viewModel.setLoading(true);
        assertTrue(viewModel.isLoading());

        viewModel.setLoading(false);
        assertFalse(viewModel.isLoading());
    }

    @Test
    public void testViewModel_viewName() {
        assertEquals("flashcard", viewModel.getViewName());
    }

    @Test
    public void testViewModel_indexResetsOnNewSet() {
        List<Flashcard> cards1 = Arrays.asList(
                new Flashcard("Q1", "A1"),
                new Flashcard("Q2", "A2")
        );
        FlashcardSet set1 = new FlashcardSet("Course1", cards1);
        viewModel.setCurrentFlashcardSet(set1);
        viewModel.setCurrentCardIndex(1);

        List<Flashcard> cards2 = Arrays.asList(new Flashcard("Q3", "A3"));
        FlashcardSet set2 = new FlashcardSet("Course2", cards2);
        viewModel.setCurrentFlashcardSet(set2);

        assertEquals(0, viewModel.getCurrentCardIndex());
    }

    // ========== USE CASE TESTS ==========

    @Test
    public void testInteractor_successfulGeneration() {
        RecordingGenerator generator = new RecordingGenerator();
        RecordingPresenter presenter = new RecordingPresenter();
        GenerateFlashcardsInteractor interactor = new GenerateFlashcardsInteractor(generator, presenter);

        List<Flashcard> expectedCards = Arrays.asList(
                new Flashcard("Q1", "A1"),
                new Flashcard("Q2", "A2")
        );
        FlashcardSet expectedSet = new FlashcardSet("CSC207", expectedCards);
        generator.nextFlashcardSet = expectedSet;

        interactor.execute("CSC207", "test content");

        assertEquals("CSC207", generator.capturedCourseName);
        assertEquals("test content", generator.capturedContent);
        assertNotNull(presenter.presentedResponse);
        assertEquals(expectedSet, presenter.presentedResponse.getFlashcardSet());
        assertNull(presenter.presentedError);
    }

    @Test
    public void testInteractor_handlesIOException() {
        RecordingGenerator generator = new RecordingGenerator();
        RecordingPresenter presenter = new RecordingPresenter();
        GenerateFlashcardsInteractor interactor = new GenerateFlashcardsInteractor(generator, presenter);

        generator.failWith = new IOException("API call failed");

        interactor.execute("CSC207", "content");

        assertNull(presenter.presentedResponse);
        assertNotNull(presenter.presentedError);
        assertTrue(presenter.presentedError.contains("Failed to generate flashcards"));
        assertTrue(presenter.presentedError.contains("API call failed"));
    }

    @Test
    public void testInteractor_handlesRuntimeException() {
        RecordingGenerator generator = new RecordingGenerator();
        RecordingPresenter presenter = new RecordingPresenter();
        GenerateFlashcardsInteractor interactor = new GenerateFlashcardsInteractor(generator, presenter);

        generator.failWithRuntime = new RuntimeException("Unexpected error");

        interactor.execute("CSC207", "content");

        assertNull(presenter.presentedResponse);
        assertNotNull(presenter.presentedError);
        assertTrue(presenter.presentedError.contains("An unexpected error occurred"));
    }

    @Test
    public void testController_callsInteractor() {
        RecordingInteractor interactor = new RecordingInteractor();
        GenerateFlashcardsController controller = new GenerateFlashcardsController(interactor);

        controller.generateFlashcards("CSC207", "Clean Architecture");

        assertTrue(interactor.executeCalled);
        assertEquals("CSC207", interactor.capturedCourseName);
        assertEquals("Clean Architecture", interactor.capturedContent);
    }

    @Test
    public void testController_multipleCallsWithDifferentInputs() {
        RecordingInteractor interactor = new RecordingInteractor();
        GenerateFlashcardsController controller = new GenerateFlashcardsController(interactor);

        controller.generateFlashcards("CSC207", "Content1");
        assertEquals("CSC207", interactor.capturedCourseName);
        assertEquals("Content1", interactor.capturedContent);

        controller.generateFlashcards("MAT137", "Content2");
        assertEquals("MAT137", interactor.capturedCourseName);
        assertEquals("Content2", interactor.capturedContent);
    }

    @Test
    public void testPresenter_presentFlashcards() throws InterruptedException {
        FlashcardViewModel viewModel = new FlashcardViewModel();
        RecordingViewManager viewManager = new RecordingViewManager();
        GenerateFlashcardsPresenter presenter = new GenerateFlashcardsPresenter(viewModel, viewManager);

        List<Flashcard> cards = Arrays.asList(new Flashcard("Q", "A"));
        FlashcardSet set = new FlashcardSet("CSC207", cards);
        GenerateFlashcardsResponseModel response = new GenerateFlashcardsResponseModel(set);

        presenter.presentFlashcards(response);
        Thread.sleep(150);

        assertFalse(viewModel.isLoading());
        assertEquals(set, viewModel.getCurrentFlashcardSet());
        assertNull(viewModel.getErrorMessage());
        assertEquals("flashcardDisplay", viewManager.capturedState);
        assertTrue(viewManager.firePropertyChangeCalled);
    }

    @Test
    public void testPresenter_presentError() throws InterruptedException {
        FlashcardViewModel viewModel = new FlashcardViewModel();
        RecordingViewManager viewManager = new RecordingViewManager();
        GenerateFlashcardsPresenter presenter = new GenerateFlashcardsPresenter(viewModel, viewManager);

        presenter.presentError("Generation failed");
        Thread.sleep(150);

        assertFalse(viewModel.isLoading());
        assertEquals("Generation failed", viewModel.getErrorMessage());
    }

    @Test
    public void testRequestModel() {
        GenerateFlashcardsRequestModel request = new GenerateFlashcardsRequestModel("CSC207", "content");

        assertEquals("CSC207", request.getCourseName());
        assertEquals("content", request.getContent());
    }

    @Test
    public void testRequestModel_nullValues() {
        GenerateFlashcardsRequestModel request = new GenerateFlashcardsRequestModel(null, null);

        assertNull(request.getCourseName());
        assertNull(request.getContent());
    }

    @Test
    public void testResponseModel() {
        List<Flashcard> cards = Arrays.asList(new Flashcard("Q", "A"));
        FlashcardSet set = new FlashcardSet("CSC207", cards);
        GenerateFlashcardsResponseModel response = new GenerateFlashcardsResponseModel(set);

        assertEquals(set, response.getFlashcardSet());
    }

    @Test
    public void testResponseModel_nullSet() {
        GenerateFlashcardsResponseModel response = new GenerateFlashcardsResponseModel(null);
        assertNull(response.getFlashcardSet());
    }

    // ========== TEST DOUBLES (MOCKS) ==========

    private static class RecordingGenerator implements FlashcardGenerator {
        String capturedCourseName;
        String capturedContent;
        FlashcardSet nextFlashcardSet;
        IOException failWith;
        RuntimeException failWithRuntime;

        @Override
        public FlashcardSet generateForCourse(String courseName, String content) throws IOException {
            this.capturedCourseName = courseName;
            this.capturedContent = content;

            if (failWith != null) {
                throw failWith;
            }
            if (failWithRuntime != null) {
                throw failWithRuntime;
            }

            return nextFlashcardSet;
        }
    }

    private static class RecordingPresenter implements GenerateFlashcardsOutputBoundary {
        GenerateFlashcardsResponseModel presentedResponse;
        String presentedError;

        @Override
        public void presentFlashcards(GenerateFlashcardsResponseModel response) {
            this.presentedResponse = response;
        }

        @Override
        public void presentError(String message) {
            this.presentedError = message;
        }
    }

    private static class RecordingInteractor implements GenerateFlashcardsInputBoundary {
        boolean executeCalled;
        String capturedCourseName;
        String capturedContent;

        @Override
        public void execute(String courseName, String content) {
            this.executeCalled = true;
            this.capturedCourseName = courseName;
            this.capturedContent = content;
        }
    }

    private static class RecordingViewManager extends ViewManagerModel {
        String capturedState;
        boolean firePropertyChangeCalled;

        @Override
        public void setState(String state) {
            this.capturedState = state;
        }

        @Override
        public void firePropertyChange() {
            this.firePropertyChangeCalled = true;
        }
    }
    // ========== MISSING COVERAGE TESTS ==========

    @Test
    public void testViewModel_addPropertyChangeListener() {
        FlashcardViewModel viewModel = new FlashcardViewModel();
        TestPropertyChangeListener listener = new TestPropertyChangeListener();

        viewModel.addPropertyChangeListener(listener);

        // Trigger a property change
        viewModel.setLoading(true);

        // Listener should be notified
        assertTrue(listener.wasNotified);
        assertEquals("loadingChanged", listener.lastPropertyName);
    }

    @Test
    public void testViewModel_removePropertyChangeListener() {
        FlashcardViewModel viewModel = new FlashcardViewModel();
        TestPropertyChangeListener listener = new TestPropertyChangeListener();

        viewModel.addPropertyChangeListener(listener);
        viewModel.removePropertyChangeListener(listener);

        // Trigger a property change
        viewModel.setLoading(true);

        // Listener should NOT be notified
        assertFalse(listener.wasNotified);
    }

    @Test
    public void testHardCodedCourseLookup_found() {
        data_access.HardCodedCourseLookup lookup = new data_access.HardCodedCourseLookup();
        entities.Course course = lookup.getCourseById("RLG200");

        assertNotNull(course);
        assertEquals("RLG200", course.getCourseId());
        assertEquals("Religion Studies", course.getName());
        assertEquals("Demo course for testing", course.getDescription());
        assertFalse(course.getUploadedFiles().isEmpty());
    }

    @Test
    public void testHardCodedCourseLookup_notFound() {
        data_access.HardCodedCourseLookup lookup = new data_access.HardCodedCourseLookup();
        entities.Course course = lookup.getCourseById("INVALID");

        assertNull(course);
    }

    @Test
    public void testHardCodedCourseLookup_nullInput() {
        data_access.HardCodedCourseLookup lookup = new data_access.HardCodedCourseLookup();
        entities.Course course = lookup.getCourseById(null);

        assertNull(course);
    }

    @Test
    public void testHardCodedCourseLookup_emptyString() {
        data_access.HardCodedCourseLookup lookup = new data_access.HardCodedCourseLookup();
        entities.Course course = lookup.getCourseById("");

        assertNull(course);
    }

    // Helper class for property change listener testing
    private static class TestPropertyChangeListener implements java.beans.PropertyChangeListener {
        boolean wasNotified = false;
        String lastPropertyName = null;

        @Override
        public void propertyChange(java.beans.PropertyChangeEvent evt) {
            wasNotified = true;
            lastPropertyName = evt.getPropertyName();
        }
    }

    @Test
    public void testViewModel_setCardIndex_allBranches() {
        FlashcardViewModel viewModel = new FlashcardViewModel();

        // Branch 1: currentFlashcardSet is null
        viewModel.setCurrentCardIndex(0);
        assertEquals(0, viewModel.getCurrentCardIndex());

        // Set up flashcard set
        List<Flashcard> cards = Arrays.asList(
                new Flashcard("Q1", "A1"),
                new Flashcard("Q2", "A2")
        );
        FlashcardSet set = new FlashcardSet("TEST", cards);
        viewModel.setCurrentFlashcardSet(set);

        // Branch 2: index < 0
        viewModel.setCurrentCardIndex(-5);
        assertEquals(0, viewModel.getCurrentCardIndex()); // Should not change

        // Branch 3: index >= size
        viewModel.setCurrentCardIndex(10);
        assertEquals(0, viewModel.getCurrentCardIndex()); // Should not change

        // Branch 4: valid index at boundary (size - 1)
        viewModel.setCurrentCardIndex(1);
        assertEquals(1, viewModel.getCurrentCardIndex());

        // Branch 5: valid index at 0
        viewModel.setCurrentCardIndex(0);
        assertEquals(0, viewModel.getCurrentCardIndex());
    }
}