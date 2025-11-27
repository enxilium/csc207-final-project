package usecases.workspace;

public interface CourseWorkspaceOutputBoundary {
    /**
     * Prepares the success view for the dashboard Use Case.
     * @param outputData the output data
     */
    void prepareWorkspaceView(CourseWorkspaceOutputData outputData);

    /**
     * Prepares the success view for the edit course Use Case.
     * @param outputData the output data
     */
    void prepareEditView(CourseWorkspaceOutputData outputData);


    /**
     * Prepares the failure view for the dashboard Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}
