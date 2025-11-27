package views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import entities.*;
import interface_adapters.*;
import interface_adapters.dashboard.*;
import interface_adapters.workspace.*;


public class CourseDashboardView extends JPanel implements ActionListener, PropertyChangeListener  {
    private final String viewName = "dashboard";
    private JPanel centerPanel = null; // To keep track of the previously selected button
    private Color defaultButtonColor;
    private CourseDashboardController courseDashboardController = null;
    private CourseController courseController = null;
    private CourseDashboardViewModel courseDashboardViewModel = null;
    private ViewManager viewManager = null;

    public CourseDashboardView(CourseDashboardViewModel courseDashboardViewModel){
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


    public void renderDashboard(){
        this.courseDashboardController.displayCourses();
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            final CourseDashboardState state = (CourseDashboardState) evt.getNewValue();
            centerPanel.removeAll();
            List<Course> courses =  state.getCourses();
            if (courses == null || courses.isEmpty()){
                return;
            }
            courses.forEach(course->{
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

    public String getViewName() {
        return viewName;
    }

    public void setCourseDashboardController(CourseDashboardController courseDashboardController) {
        this.courseDashboardController = courseDashboardController;
    }
    public void setCourseWorkspaceController(CourseController courseController) {
        this.courseController = courseController;
    }
}

