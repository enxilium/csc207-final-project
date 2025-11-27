package interface_adapters.mock_test;

import usecases.mock_test_generation.MockTestGenerationInputBoundary;
import usecases.mock_test_generation.MockTestGenerationInputData;

public class MockTestController {
    private final MockTestGenerationInputBoundary mockTestGenerationInteractor;

    public MockTestController(MockTestGenerationInputBoundary mockTestGenerationInteractor) {
        this.mockTestGenerationInteractor = mockTestGenerationInteractor;
    }

    public void execute(String courseID) {
        final MockTestGenerationInputData testGenerationData = new MockTestGenerationInputData(courseID);
        // Run the generation workflow off the EDT so the loading view can render.
    Thread worker = new Thread(() -> mockTestGenerationInteractor.execute(testGenerationData),
        "mock-test-generation-worker");
    worker.setDaemon(true);
        worker.start();
    }
}
