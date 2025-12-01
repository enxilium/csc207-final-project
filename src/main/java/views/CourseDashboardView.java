package views;

import entities.Course;
import interface_adapters.dashboard.CourseDashboardController;
import interface_adapters.dashboard.CourseDashboardState;
import interface_adapters.dashboard.CourseDashboardViewModel;
import interface_adapters.workspace.CourseController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * View for displaying the course dashboard.
 */
public class CourseDashboardView extends JPanel implements ActionListener, PropertyChangeListener {
  private final String viewName = "dashboard";
  private JPanel centerPanel = null; // To keep track of the previously selected button
  private Color defaultButtonColor;
  private CourseDashboardController courseDashboardController = null;
  private CourseController courseController = null;
  private CourseDashboardViewModel courseDashboardViewModel = null;

  /**
   * Constructs a CourseDashboardView with the given view model.
   *
   * @param courseDashboardViewModel the view model for the dashboard
   */
  public CourseDashboardView(CourseDashboardViewModel courseDashboardViewModel) {
    this.courseDashboardViewModel = courseDashboardViewModel;
    this.courseDashboardViewModel.addPropertyChangeListener(this);

    this.setPreferredSize(new Dimension(1200, 800));

    this.setBackground(Color.LIGHT_GRAY);
    this.setBorder(new EmptyBorder(20, 20, 20, 20));

    // Main panel using BorderLayout
    this.setLayout(new BorderLayout());

    // Top panel
    JPanel topPanel = new JPanel();
    topPanel.setBackground(Color.LIGHT_GRAY);
    topPanel.add(new JLabel("Course Dashboard"));
    this.add(topPanel, BorderLayout.NORTH);

    // Center panel (optional, can be used for main content)
    centerPanel = new JPanel();
    centerPanel.setBackground(Color.WHITE);
    this.add(centerPanel, BorderLayout.CENTER);

    // command panel
    JPanel bottomPanel = new JPanel();
    bottomPanel.setBackground(Color.DARK_GRAY);

    JButton newButton = new JButton("New");
    newButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        courseDashboardController.createCourse();
      }
    });
    bottomPanel.add(newButton);

    JButton exitButton = new JButton("Exit");
    bottomPanel.add(exitButton);
    exitButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0); // Exit the application
      }
    });
    this.add(bottomPanel, BorderLayout.SOUTH);
  }

  /**
   * Renders the dashboard by displaying all courses.
   */
  public void renderDashboard() {
    this.courseDashboardController.displayCourses();
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals("state")) {
      final CourseDashboardState state = (CourseDashboardState) evt.getNewValue();
      centerPanel.removeAll();
      List<Course> courses = state.getCourses();
      if (courses == null || courses.isEmpty()) {
        return;
      }
      courses.forEach(course -> {
        JButton button = new JButton(course.getCourseId());
        button.setPreferredSize(new Dimension(80, 30));
        defaultButtonColor = button.getBackground();

        button.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            JButton currentButton = (JButton) e.getSource();
            courseController.displayCourse(currentButton.getText());
          }
        });

        centerPanel.add(button);
      });
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // Code to execute when the action event occurs
    System.out.println("Action performed!");
  }

  /**
   * Gets the view name.
   *
   * @return the view name
   */
  public String getViewName() {
    return viewName;
  }

  /**
   * Sets the course dashboard controller.
   *
   * @param courseDashboardController the controller to set
   */
  public void setCourseDashboardController(CourseDashboardController courseDashboardController) {
    this.courseDashboardController = courseDashboardController;
  }

  /**
   * Sets the course workspace controller.
   *
   * @param courseController the controller to set
   */
  public void setCourseWorkspaceController(CourseController courseController) {
    this.courseController = courseController;
  }
}
