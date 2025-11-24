package flashcards;

import data_access.FlashcardGenerator;
import entities.Flashcard;
import entities.FlashcardSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import usecases.GenerateFlashcardsInteractor;
import usecases.GenerateFlashcardsOutputBoundary;
import usecases.GenerateFlashcardsResponseModel;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GenerateFlashcardsInteractorTest {

    private TestFlashcardGenerator generator;
    private TestPresenter presenter;
    private GenerateFlashcardsInteractor interactor;

    @BeforeEach
    void setUp() {
        generator = new TestFlashcardGenerator();
        presenter = new TestPresenter();
        interactor = new GenerateFlashcardsInteractor(generator, presenter);
    }

    @Test
    void testSuccessfulFlashcardGeneration() {
        // Arrange
        FlashcardSet expectedSet = new FlashcardSet("TestCourse", Arrays.asList(
                new Flashcard("Q1", "A1"),
                new Flashcard("Q2", "A2")
        ));
        generator.setFlashcardSet(expectedSet);

        // Act
        interactor.execute("TestCourse", "test content");

        // Assert
        assertNotNull(presenter.responseModel);
        assertEquals(expectedSet, presenter.responseModel.getFlashcardSet());
        assertNull(presenter.errorMessage);
    }

    @Test
    void testIOExceptionHandling() {
        // Arrange
        generator.setThrowIOException(true);

        // Act
        interactor.execute("TestCourse", "test content");

        // Assert
        assertNull(presenter.responseModel);
        assertNotNull(presenter.errorMessage);
        assertTrue(presenter.errorMessage.contains("Failed to generate flashcards"));
    }

    @Test
    void testRuntimeExceptionHandling() {
        // Arrange
        generator.setThrowRuntimeException(true);

        // Act
        interactor.execute("TestCourse", "test content");

        // Assert
        assertNull(presenter.responseModel);
        assertNotNull(presenter.errorMessage);
    }

    // === Test Doubles ===

    private static class TestFlashcardGenerator implements FlashcardGenerator {
        private FlashcardSet flashcardSet;
        private boolean throwIOException = false;
        private boolean throwRuntimeException = false;

        void setFlashcardSet(FlashcardSet set) {
            this.flashcardSet = set;
        }

        void setThrowIOException(boolean value) {
            this.throwIOException = value;
        }

        void setThrowRuntimeException(boolean value) {
            this.throwRuntimeException = value;
        }

        @Override
        public FlashcardSet generateForCourse(String courseName, String content) throws IOException {
            if (throwIOException) {
                throw new IOException("Test IO Exception");
            }
            if (throwRuntimeException) {
                throw new RuntimeException("Test Runtime Exception");
            }
            return flashcardSet;
        }
    }

    private static class TestPresenter implements GenerateFlashcardsOutputBoundary {
        GenerateFlashcardsResponseModel responseModel;
        String errorMessage;

        @Override
        public void presentFlashcards(GenerateFlashcardsResponseModel response) {
            this.responseModel = response;
        }

        @Override
        public void presentError(String message) {
            this.errorMessage = message;
        }
    }
}