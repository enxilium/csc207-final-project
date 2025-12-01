package views;

import entities.Course;
import interface_adapters.workspace.CourseController;
import interface_adapters.workspace.CourseEditViewModel;
import interface_adapters.workspace.CourseState;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * View for editing a course.
 */
public class CourseEditView extends JPanel implements ActionListener, PropertyChangeListener {
  private final String viewName = "editCourse";
  private JPanel centerPanel = null; // To keep track of the previously selected button
  private String courseId = null;
  private CourseController courseController = null;
  private CourseEditViewModel courseEditViewModel = null;
  private JTextField idText = null;
  private JTextField nameText = null;
  private JTextField descriptionText = null;

  /**
   * Constructs a CourseEditView with the given view model.
   *
   * @param courseEditViewModel the view model for course editing
   */
  public CourseEditView(CourseEditViewModel courseEditViewModel) {
    this.courseEditViewModel = courseEditViewModel;
    this.courseEditViewModel.addPropertyChangeListener(this);
    this.setPreferredSize(new Dimension(800, 600));

    Course course = null;
    this.setBackground(Color.LIGHT_GRAY);
    this.setBorder(new EmptyBorder(20, 20, 20, 20));

    // Main panel using BorderLayout
    this.setLayout(new BorderLayout());

    // Top panel
    JPanel topPanel = new JPanel();
    topPanel.setBackground(Color.LIGHT_GRAY);
    topPanel.add(new JLabel("Edit Course"));
    this.add(topPanel, BorderLayout.NORTH);

    // Center panel (optional, can be used for main content)
    centerPanel = new JPanel();
    centerPanel.setBackground(Color.WHITE);
    // Main panel uses BoxLayout for vertical stacking
    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
    this.add(centerPanel, BorderLayout.CENTER);

    // Bottom panel
    JPanel bottomPanel = new JPanel();
    bottomPanel.setBackground(Color.DARK_GRAY);

    // save and close
    JButton noteButton = new JButton("Save and Return");
    noteButton.setPreferredSize(new Dimension(180, 30));
    noteButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String id = idText.getText();
        String name = nameText.getText();
        String description = descriptionText.getText();
        // TODO: validation if value is missing
        courseController.updateCourse(id, name, description);
      }
    });
    bottomPanel.add(noteButton);

    // Cancel
    JButton returnButton = new JButton("Cancel");
    returnButton.setPreferredSize(new Dimension(80, 30));
    returnButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        courseController.displayCourse(courseId);
      }
    });
    bottomPanel.add(returnButton);
    this.add(bottomPanel, BorderLayout.SOUTH);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    String courseName = null;
    String description = null;

    if (evt.getPropertyName().equals("state")) {
      this.centerPanel.removeAll();
      final CourseState state = (CourseState) evt.getNewValue();
      Course course = state.getCourse();

      if (course != null) {
        courseId = course.getCourseId();
        courseName = course.getName();
        description = course.getDescription();
      }

      // Align left within the row
      JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      idPanel.setBackground(Color.WHITE);
      JLabel idLabel = new JLabel("ID: ");
      idPanel.add(idLabel);
      idText = new JTextField(this.courseId);
      idText.setPreferredSize(new Dimension(150, 25));
      idText.setEnabled(false);
      idPanel.add(idText);
      idPanel.setMaximumSize(idPanel.getPreferredSize());
      centerPanel.add(idPanel);

      // Align left within the row
      JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      namePanel.setBackground(Color.WHITE);
      JLabel nameLabel = new JLabel("Name: ");
      namePanel.add(nameLabel);
      nameText = new JTextField(courseName);
      nameText.setPreferredSize(new Dimension(150, 25));
      namePanel.add(nameText);
      namePanel.setMaximumSize(namePanel.getPreferredSize());
      centerPanel.add(namePanel);

      // Align left within the row
      JPanel descriptionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      descriptionPanel.setBackground(Color.WHITE);
      JLabel descriptionLabel = new JLabel("Description: ");
      descriptionPanel.add(descriptionLabel);
      descriptionText = new JTextField(description);
      descriptionText.setPreferredSize(new Dimension(150, 25));
      descriptionPanel.add(descriptionText);
      descriptionPanel.setMaximumSize(descriptionPanel.getPreferredSize());
      centerPanel.add(descriptionPanel);
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
   * Sets the course workspace controller.
   *
   * @param courseController the controller to set
   */
  public void setCourseWorkspaceController(CourseController courseController) {
    this.courseController = courseController;
  }
}
