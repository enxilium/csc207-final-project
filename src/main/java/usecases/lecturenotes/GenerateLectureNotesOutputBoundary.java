package usecases.lecturenotes;

/**
 * Output boundary interface for the lecture notes generation use case.
 */
public interface GenerateLectureNotesOutputBoundary {
  /**
   * Prepares the success view with the generated lecture notes.
   *
   * @param outputData the output data containing the lecture notes
   */
  void prepareSuccessView(GenerateLectureNotesOutputData outputData);

  /**
   * Prepares the fail view with an error message.
   *
   * @param error the error message
   */
  void prepareFailView(String error);
}
