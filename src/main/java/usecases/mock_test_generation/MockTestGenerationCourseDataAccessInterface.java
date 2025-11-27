package usecases.mock_test_generation;

import entities.PDFFile;

import java.util.List;

public interface MockTestGenerationCourseDataAccessInterface {
    List<PDFFile> getCourseMaterials(String courseId);
}
