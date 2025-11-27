package interface_adapters.workspace;

import entities.*;

public class CourseState {
    private Course course;
    private String error;

    public CourseState(){
    }

    public Course getCourse(){
        return this.course;
    }
    public void setCourse(Course course){
        this.course = course;
    }

    public void setError(String error) {
        this.error = error;
    }
}
