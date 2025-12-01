package views;

import interface_adapters.LoadingViewModel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A simple, passive view that is displayed during long-running operations
 * like API calls. It shows a "Loading..." message to the user.
 */
public class LoadingView extends JPanel {

  private final LoadingViewModel viewModel;
  private final JLabel loadingLabel;

  /**
   * Constructs a LoadingView with the given view model.
   *
   * @param viewModel the loading view model
   */
  public LoadingView(LoadingViewModel viewModel) {
    this.viewModel = viewModel;

    // Initialize UI components
    this.loadingLabel = new JLabel("Loading...");

    initializeUserInterface();
  }

  /**
   * Sets up the layout and visual style of the loading screen.
   */
  private void initializeUserInterface() {
    // Use GridBagLayout to easily center the component both vertically
    // and horizontally
    this.setLayout(new GridBagLayout());
    // Match other views for smooth transitions
    this.setPreferredSize(new Dimension(800, 600));
    // A light grey background
    this.setBackground(new Color(245, 245, 245));

    loadingLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
    loadingLabel.setForeground(Color.DARK_GRAY);

    // Add the label to the center of the panel
    this.add(loadingLabel, new GridBagConstraints());
  }
}
