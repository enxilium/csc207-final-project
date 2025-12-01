package usecases.lecturenotes;

import entities.Course;
import entities.LectureNotes;
import usecases.lecturenotes.CourseLookupGateway;
import usecases.lecturenotes.NotesGeminiGateway;

/**
 * Interactor for generating lecture notes.
 */
public class GenerateLectureNotesInteractor implements GenerateLectureNotesInputBoundary {

  private final CourseLookupGateway courseGateway;
  private final NotesGeminiGateway notesGateway;
  private final GenerateLectureNotesOutputBoundary presenter;

  /**
   * Constructs a GenerateLectureNotesInteractor with the given gateways
   * and presenter.
   *
   * @param courseGateway the course lookup gateway
   * @param notesGateway the notes generation gateway
   * @param presenter the output boundary presenter
   */
  public GenerateLectureNotesInteractor(CourseLookupGateway courseGateway,
      NotesGeminiGateway notesGateway,
      GenerateLectureNotesOutputBoundary presenter) {
    this.courseGateway = courseGateway;
    this.notesGateway = notesGateway;
    this.presenter = presenter;
  }

  @Override
  public void execute(GenerateLectureNotesInputData inputData) {
    String courseId = inputData.getCourseId();
    String topic = inputData.getTopic();

    Course course = courseGateway.getCourseById(courseId);

    // If the course does not exist, do NOT call the notes gateway.
    if (course == null) {
      presenter.prepareFailView("Course not found: " + courseId);
      return;
    }

    try {
      LectureNotes lectureNotes = notesGateway.generateNotes(course, topic);

      // Your OutputData constructor expects 3 args: (courseId, topic, notesText/content)
      GenerateLectureNotesOutputData outputData = new GenerateLectureNotesOutputData(
          lectureNotes.getCourseId(),
          lectureNotes.getTopic(),
          lectureNotes.getContent()
      );

      presenter.prepareSuccessView(outputData);

    } catch (Exception e) {
      presenter.prepareFailView("Failed to generate lecture notes. "
          + "Please try again.");
    }
  }
}
