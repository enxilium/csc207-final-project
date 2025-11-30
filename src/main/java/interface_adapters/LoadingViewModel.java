package interface_adapters;

import interface_adapters.mock_test.MockTestState;

public class LoadingViewModel extends ViewModel<MockTestState> {
    public LoadingViewModel() {
        super("loading");
        setState(new MockTestState());
    }
}
