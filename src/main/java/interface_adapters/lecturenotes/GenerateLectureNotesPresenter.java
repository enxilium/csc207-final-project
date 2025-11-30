package interface_adapters.lecturenotes;

import usecases.Timeline.CourseIdMapper;
import usecases.Timeline.TimelineLogger;
import interface_adapters.ViewManagerModel;
import usecases.lecturenotes.GenerateLectureNotesOutputBoundary;
import usecases.lecturenotes.GenerateLectureNotesOutputData;

import java.util.UUID;

public class GenerateLectureNotesPresenter implements GenerateLectureNotesOutputBoundary {

    private final LectureNotesViewModel lectureNotesViewModel;
    private final ViewManagerModel viewManagerModel;
    private final TimelineLogger timelineLogger;

    public GenerateLectureNotesPresenter(LectureNotesViewModel lectureNotesViewModel,
                                         ViewManagerModel viewManagerModel,
                                         TimelineLogger timelineLogger) {
        this.lectureNotesViewModel = lectureNotesViewModel;
        this.viewManagerModel = viewManagerModel;
        this.timelineLogger = timelineLogger;
    }

    @Override
    public void prepareSuccessView(GenerateLectureNotesOutputData outputData) {
        LectureNotesState state = lectureNotesViewModel.getState();
        state.setCourseId(outputData.getCourseId());
        state.setTopic(outputData.getTopic());
        state.setNotesText(outputData.getNotesText());
        state.setError("");
        state.setLoading(false);

        lectureNotesViewModel.setState(state);
        viewManagerModel.setState(lectureNotesViewModel.getViewName());
        viewManagerModel.firePropertyChange();

        // Log to Timeline
        if (timelineLogger != null && outputData.getCourseId() != null && !outputData.getCourseId().isEmpty()) {
            try {
                UUID courseUuid = CourseIdMapper.getUuidForCourseId(outputData.getCourseId());
                UUID contentId = UUID.randomUUID(); // Generate a unique content ID for these notes
                String title = outputData.getTopic() != null && !outputData.getTopic().isEmpty() 
                    ? outputData.getTopic() : "Notes";
                String snippet = outputData.getNotesText() != null 
                    ? (outputData.getNotesText().length() > 100 
                        ? outputData.getNotesText().substring(0, 100) + "..." 
                        : outputData.getNotesText())
                    : "";
                String fullNotesText = outputData.getNotesText() != null ? outputData.getNotesText() : "";
                timelineLogger.logNotesGenerated(courseUuid, contentId, title, snippet, fullNotesText);
            } catch (Exception e) {
                // Log error but don't break the flow
                System.err.println("Failed to log notes to timeline: " + e.getMessage());
            }
        }
    }

    @Override
    public void prepareFailView(String error) {
        LectureNotesState state = lectureNotesViewModel.getState();
        state.setError(error);
        state.setLoading(false);
        lectureNotesViewModel.setState(state);
    }
}