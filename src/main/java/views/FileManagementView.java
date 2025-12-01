package views;

import interface_adapters.file_management.FileManagementController;
import interface_adapters.file_management.FileManagementState;
import interface_adapters.file_management.FileManagementViewModel;
import interface_adapters.ViewManagerModel;
import entities.PDFFile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class FileManagementView extends JPanel implements PropertyChangeListener {
    private final String viewName = "fileManagement";
    private final FileManagementViewModel viewModel;
    private FileManagementController controller;
    private ViewManagerModel viewManagerModel;
    private JPanel fileListPanel;
    private JLabel errorLabel;
    private String currentCourseId;

    public FileManagementView(FileManagementViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        this.setPreferredSize(new Dimension(1200, 800));
        this.setBackground(Color.LIGHT_GRAY);
        this.setBorder(new EmptyBorder(20, 20, 20, 20));
        this.setLayout(new BorderLayout());

        // Top panel with title
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.LIGHT_GRAY);
        topPanel.add(new JLabel("File Management"));
        this.add(topPanel, BorderLayout.NORTH);

        // Center panel for file list
        fileListPanel = new JPanel();
        fileListPanel.setLayout(new BoxLayout(fileListPanel, BoxLayout.Y_AXIS));
        fileListPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(fileListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);

        // Error label
        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(errorLabel, BorderLayout.SOUTH);

        // Bottom panel with buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.DARK_GRAY);

        JButton returnButton = new JButton("Return");
        returnButton.setPreferredSize(new Dimension(120, 30));
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (viewManagerModel != null) {
                    viewManagerModel.setState("workspace");
                    viewManagerModel.firePropertyChange();
                }
            }
        });
        bottomPanel.add(returnButton);
        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            FileManagementState state = (FileManagementState) evt.getNewValue();
            currentCourseId = state.getCourseId();

            // Clear error
            errorLabel.setText("");

            // Display error if any
            if (state.getError() != null && !state.getError().isEmpty()) {
                errorLabel.setText("Error: " + state.getError());
            }

            // Update file list
            updateFileList(state.getFiles());
        }
    }

    private void updateFileList(List<PDFFile> files) {
        fileListPanel.removeAll();

        if (files == null || files.isEmpty()) {
            JLabel noFilesLabel = new JLabel("No files uploaded yet.");
            noFilesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            fileListPanel.add(noFilesLabel);
        } else {
            for (PDFFile file : files) {
                JPanel fileItemPanel = new JPanel(new BorderLayout());
                fileItemPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
                        new EmptyBorder(10, 10, 10, 10)
                ));
                fileItemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                fileItemPanel.setBackground(Color.WHITE);

                // File name label
                String fileName = file.getPath().getFileName().toString();
                JLabel fileNameLabel = new JLabel(fileName);
                fileNameLabel.setFont(new Font(fileNameLabel.getFont().getName(), Font.PLAIN, 14));
                fileItemPanel.add(fileNameLabel, BorderLayout.CENTER);

                // Delete button
                JButton deleteButton = new JButton("Delete");
                deleteButton.setPreferredSize(new Dimension(80, 30));
                deleteButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (controller != null && currentCourseId != null) {
                            int confirm = JOptionPane.showConfirmDialog(
                                    FileManagementView.this,
                                    "Are you sure you want to delete " + fileName + "?",
                                    "Confirm Delete",
                                    JOptionPane.YES_NO_OPTION
                            );
                            if (confirm == JOptionPane.YES_OPTION) {
                                controller.deleteFile(currentCourseId, file.getPath().toString());
                            }
                        }
                    }
                });
                fileItemPanel.add(deleteButton, BorderLayout.EAST);

                fileListPanel.add(fileItemPanel);
            }
        }

        fileListPanel.revalidate();
        fileListPanel.repaint();
    }

    public void setController(FileManagementController controller) {
        this.controller = controller;
    }

    public void setViewManagerModel(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
    }

    public String getViewName() {
        return viewName;
    }
}
