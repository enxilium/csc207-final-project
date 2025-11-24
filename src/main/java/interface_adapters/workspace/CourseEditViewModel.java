package interface_adapters.workspace;
import interface_adapters.*;


public class CourseEditViewModel extends ViewModel<CourseState>{
    public CourseEditViewModel() {
        super("editCourse");
        setState(new CourseState());
    }
}
