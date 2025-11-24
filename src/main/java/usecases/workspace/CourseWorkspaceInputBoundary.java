package usecases.workspace;

import entities.*;

public interface CourseWorkspaceInputBoundary {

    void findCourseById(String courseId, boolean isEdit);

    void createCourse(Course course);

    void updateCourse(Course course);

    void deleteCourse(String courseId);

}

