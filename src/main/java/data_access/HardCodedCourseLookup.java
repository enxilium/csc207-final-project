package data_access;

import entities.Course;
import entities.PDFFile;
import usecases.lecturenotes.CourseLookupGateway;

public class HardCodedCourseLookup implements CourseLookupGateway {
    private final Course demo;
/**
 * Temporary hard-coded course lookup for demo and testing purposes.
 * TODO: Replace with proper CourseRepository interface and file-based implementation
 * when integrating with the full application architecture.
 */
public class HardCodedCourseLookup {

    private final Course demoCourse;

    public HardCodedCourseLookup() {
        // Match the course you create in AppBuilder (PHL245)
        demo = new Course("PHL245", "Modern Symbolic Logic", "demo course");
        demo.addFile(new PDFFile("test.pdf")); // keep your local test.pdf
    }

    @Override
    public Course getCourseById(String courseId) {
        if (courseId == null) return null;
        return demo.getCourseId().equalsIgnoreCase(courseId) ? demo : null;
    }
}