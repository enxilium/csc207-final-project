package usecases.mock_test_generation;

import entities.PDFFile;
import entities.TestData;

import java.io.IOException;
import java.util.List;

public interface MockTestGenerationTestDataAccessInterface {
    TestData getTestData(List<PDFFile> courseMaterials) throws IOException;
}
