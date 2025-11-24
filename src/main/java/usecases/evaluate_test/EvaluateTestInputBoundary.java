package usecases.evaluate_test;

import usecases.mock_test_generation.MockTestGenerationInputData;

import java.io.IOException;

/**
 * The mock test writing use case.
 */

public interface EvaluateTestInputBoundary {
    /**
     * Executes the mock test generation use case so users can write it.
     */
    void execute(EvaluateTestInputData inputData);
}
