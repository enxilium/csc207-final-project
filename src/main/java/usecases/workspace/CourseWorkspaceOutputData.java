package usecases.workspace;
import entities.*;

import java.util.List;

public class CourseWorkspaceOutputData {
    private Course course;

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

