package views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import entities.*;
import interface_adapters.*;
import interface_adapters.dashboard.*;
import interface_adapters.mock_test.MockTestController;
import interface_adapters.workspace.*;

public class CourseWorkspaceView extends JPanel implements ActionListener, PropertyChangeListener {
    private final String viewName = "workspace";
    private JPanel centerPanel = null; // To keep track of the previously selected button
    private CourseDashboardController courseDashboardController = null;
    private MockTestController mockTestController = null;
    private CourseController courseController = null;
    private String courseId = null;
    private ViewManager viewManager = null;
    private CourseWorkspaceViewModel courseWorkspaceViewModel = null;
    private Runnable openLectureNotesAction = null;

    public CourseWorkspaceView(CourseWorkspaceViewModel courseWorkspaceViewModel){
        this.courseWorkspaceViewModel = courseWorkspaceViewModel;
        this.courseWorkspaceViewModel.addPropertyChangeListener(this);

        this.setPreferredSize(new Dimension(1200, 800));

        this.setBackground(Color.LIGHT_GRAY);
        this.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Main panel using BorderLayout
        this.setLayout(new BorderLayout());

        // Top panel
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.LIGHT_GRAY);
        topPanel.add(new JLabel("Course Workspace"));
        this.add(topPanel, BorderLayout.NORTH);

        // Center panel (optional, can be used for main content)
        centerPanel = new JPanel();
        centerPanel.setBackground(Color.WHITE);
        this.add(centerPanel, BorderLayout.CENTER);


        // command panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.DARK_GRAY);

        //edit
        JButton editButton = new JButton("Edit");
        editButton.setPreferredSize(new Dimension(80, 30));
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton currentButton = (JButton) e.getSource();
                courseController.editCourse(courseId);
            }
        });
        bottomPanel.add(editButton);


        //delete
        JButton deleteButton = new JButton("Delete");
        deleteButton.setPreferredSize(new Dimension(80, 30));
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton currentButton = (JButton) e.getSource();
                courseController.deleteCourse(courseId);
            }
        });
        bottomPanel.add(deleteButton);

        //upload content
        JButton uploadButton = new JButton("Upload");
        uploadButton.setPreferredSize(new Dimension(120, 30));
        bottomPanel.add(uploadButton);

        //open notes
        JButton noteButton = new JButton("Existing Notes");
        noteButton.setPreferredSize(new Dimension(140, 30));
        bottomPanel.add(noteButton);
        noteButton.addActionListener(e -> { if (openLectureNotesAction != null) openLectureNotesAction.run(); });

        //create note
        JButton createNoteButton = new JButton("Create Note");
        createNoteButton.setPreferredSize(new Dimension(140, 30));
        bottomPanel.add(createNoteButton);
        createNoteButton.addActionListener(e -> { if (openLectureNotesAction != null) openLectureNotesAction.run(); });

        //open flash card
        JButton flashCardButton = new JButton("Existing Flashcards");
        flashCardButton.setPreferredSize(new Dimension(160, 30));
        bottomPanel.add(flashCardButton);

        //create flash card
        JButton createFlashCardButton = new JButton("Create Flashcards");
        createFlashCardButton.setPreferredSize(new Dimension(150, 30));
        bottomPanel.add(createFlashCardButton);

        //open test
        JButton testButton = new JButton("Open Tests");
        flashCardButton.setPreferredSize(new Dimension(140, 30));
        bottomPanel.add(testButton);

        //create test
        JButton createtestButton = new JButton("Create Test");
        createFlashCardButton.setPreferredSize(new Dimension(150, 30));
        bottomPanel.add(createtestButton);
        createtestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mockTestController.execute(courseId);
            }
        });

        // return
        JButton returnButton = new JButton("Return");
        returnButton.setPreferredSize(new Dimension(80, 30));
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                courseDashboardController.displayCourses();
            }
        });
        bottomPanel.add(returnButton);
        this.add(bottomPanel, BorderLayout.SOUTH);
    }



    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            this.centerPanel.removeAll();
            final CourseState state = (CourseState) evt.getNewValue();
            Course course =  state.getCourse();
            this.courseId = course.getCourseId();
            JLabel textLabel = new JLabel("<html>ID: " + course.getCourseId() + "<br/> " +
                    "Name: " + course.getName()  + "<br/> " +
                    "Description: " + course.getDescription() + "</html>");
            centerPanel.add(textLabel);
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

    public MockTestController getMockTestController() {
        return mockTestController;
    }

    public void setMockTestController(MockTestController mockTestController) {
        this.mockTestController = mockTestController;
    }

    public void setOpenLectureNotesAction(Runnable action) {
        this.openLectureNotesAction = action;
    }
}

