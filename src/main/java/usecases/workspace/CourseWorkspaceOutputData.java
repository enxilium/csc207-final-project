package usecases.workspace;
import entities.*;

import java.util.List;

/**
 * Output data model for the course workspace use case.
 * Contains the course data to be displayed in the workspace view.
 */
public class CourseWorkspaceOutputData {
    private Course course;

    /**
     * Constructs a CourseWorkspaceOutputData with the given course.
     *
     * @param course the course to display in the workspace view
     */
    public CourseWorkspaceOutputData(Course course){
        this.course = course;
    }

    public Course getCourse(){
        return this.course;
    }
    public void setCourse(Course course){
        this.course = course;
    }

}

