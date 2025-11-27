package interface_adapters.dashboard;

import java.util.List;
import entities.*;

public class CourseDashboardState {
    private List<Course> courses;
    private String error;

    public CourseDashboardState(){
    }

    public List<Course> getCourses(){
        return this.courses;
    }
    public void setCourses(List<Course> courses){
        this.courses = courses;
    }

    public void setError(String error) {
        this.error = error;
    }
}
