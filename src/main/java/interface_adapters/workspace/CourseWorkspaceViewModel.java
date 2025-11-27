package interface_adapters.workspace;
import interface_adapters.*;

public class CourseWorkspaceViewModel extends ViewModel<CourseState>{
    public CourseWorkspaceViewModel() {
        super("workspace");
        setState(new CourseState());
    }
}
