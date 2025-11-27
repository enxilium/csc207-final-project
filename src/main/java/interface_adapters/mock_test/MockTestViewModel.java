package interface_adapters.mock_test;

import interface_adapters.ViewModel;

public class MockTestViewModel extends ViewModel<MockTestState> {
    public MockTestViewModel() {
        super("mock test");
        setState(new MockTestState());
    }
}
