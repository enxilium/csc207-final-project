package interface_adapters.lecturenotes;

import usecases.lecturenotes.GenerateLectureNotesInputBoundary;
import usecases.lecturenotes.GenerateLectureNotesInputData;

/**
 * Controller for generating lecture notes.
 */
public class GenerateLectureNotesController {
  private final GenerateLectureNotesInputBoundary interactor;

  /**
   * Constructs a GenerateLectureNotesController with the given interactor.
   *
   * @param interactor the lecture notes generation interactor
   */
  public GenerateLectureNotesController(GenerateLectureNotesInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Executes the lecture notes generation process.
   *
   * @param courseId the course ID
   * @param topic the topic for the lecture notes
   */
  public void execute(String courseId, String topic) {
    interactor.execute(new GenerateLectureNotesInputData(courseId, topic));
  }
}
