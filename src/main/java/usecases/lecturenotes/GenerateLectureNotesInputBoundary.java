package usecases.lecturenotes;

/**
 * Input boundary interface for the lecture notes generation use case.
 */
public interface GenerateLectureNotesInputBoundary {
  /**
   * Executes the lecture notes generation process.
   *
   * @param inputData the input data containing course ID and topic
   */
  void execute(GenerateLectureNotesInputData inputData);
}
