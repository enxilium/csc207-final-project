package usecases;

import data_access.FlashcardGenerator;
import entities.FlashcardSet;
import java.io.IOException;

/**
 * Interactor for generating flashcards.
 * Connects the input (user or test) with the data source and presenter.
 */
public class GenerateFlashcardsInteractor implements GenerateFlashcardsInputBoundary {
    private final FlashcardGenerator generator;
    private final GenerateFlashcardsOutputBoundary presenter;

    public GenerateFlashcardsInteractor(FlashcardGenerator generator, GenerateFlashcardsOutputBoundary presenter) {
        this.generator = generator;
        this.presenter = presenter;
    }

    /**
     * Executes the flashcard generation process using the provided course name and content.
     */
    @Override
    public void execute(String courseName, String content) {
        try {
            // Generate flashcards using the selected generator (Mock or Gemini)
            FlashcardSet set = generator.generateForCourse(courseName, content);

            // Wrap result in a response model
            GenerateFlashcardsResponseModel response = new GenerateFlashcardsResponseModel(set);

            // Pass the result to the presenter
            presenter.presentFlashcards(response);

        } catch (IOException e) {
            // Handle any I/O errors
            presenter.presentError("Failed to generate flashcards: " + e.getMessage());
        } catch (RuntimeException e) {
            // Handle unexpected runtime errors to prevent app crash
            presenter.presentError("An unexpected error occurred: " + e.getMessage());
    }
    }
}