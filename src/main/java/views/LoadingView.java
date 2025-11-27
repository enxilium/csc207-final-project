package views;

import interface_adapters.LoadingViewModel;

import javax.swing.*;
import java.awt.*;

/**
 * A simple, passive view that is displayed during long-running operations
 * like API calls. It shows a "Loading..." message to the user.
 */
public class LoadingView extends JPanel {

    private final LoadingViewModel viewModel;
    private final JLabel loadingLabel;

    public LoadingView(LoadingViewModel viewModel) {
        this.viewModel = viewModel;

        // Initialize UI components
        this.loadingLabel = new JLabel("Loading...");

        initializeUI();
    }

    /**
     * Sets up the layout and visual style of the loading screen.
     */
    private void initializeUI() {
        // Use GridBagLayout to easily center the component both vertically and horizontally
        this.setLayout(new GridBagLayout());
        this.setPreferredSize(new Dimension(800, 600)); // Match other views for smooth transitions
        this.setBackground(new Color(245, 245, 245)); // A light grey background

        loadingLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        loadingLabel.setForeground(Color.DARK_GRAY);

        // Add the label to the center of the panel
        this.add(loadingLabel, new GridBagConstraints());
    }
}