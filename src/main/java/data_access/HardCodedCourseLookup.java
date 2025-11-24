package data_access;

import entities.Course;
import entities.PDFFile;

/**
 * Temporary hard-coded course lookup for demo and testing purposes.
 * TODO: Replace with proper CourseRepository interface and file-based implementation
 * when integrating with the full application architecture.
 */
public class HardCodedCourseLookup {

    private final Course demoCourse;

    public HardCodedCourseLookup() {

        this.demoCourse = new Course("RLG200", "Religion Studies", "Demo course for testing");

        // Only the filename. Must be inside src/main/resources/
        this.demoCourse.addFile(new PDFFile("test.pdf"));
    }

    public Course getCourseById(String courseId) {
        if (demoCourse.getCourseId().equals(courseId)) {
            return demoCourse;
        }
        return null;
    }
}