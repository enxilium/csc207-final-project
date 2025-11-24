package interface_adapters.workspace;

import entities.Course;
import usecases.*;
import entities.*;
import usecases.workspace.*;


public class CourseController {
    private final CourseWorkspaceInputBoundary  courseWorkspaceInteractor;

    public CourseController(CourseWorkspaceInputBoundary  courseWorkspaceInteractor) {
        this.courseWorkspaceInteractor = courseWorkspaceInteractor;
    }

    public void displayCourse(String courseId){
        this.courseWorkspaceInteractor.findCourseById(courseId, false);
    }

    public void editCourse(String courseId){
        this.courseWorkspaceInteractor.findCourseById(courseId, true);
    }

    public void createCourse(String courseId, String name, String description){
        Course course = new Course(courseId, name, description);
        this.courseWorkspaceInteractor.createCourse(course);
    }
    public void updateCourse(String courseId, String name, String description){
        Course course = new Course(courseId, name, description);
        this.courseWorkspaceInteractor.updateCourse(course);
    }

    public void deleteCourse(String courseId){
        this.courseWorkspaceInteractor.deleteCourse(courseId);
    }

}

