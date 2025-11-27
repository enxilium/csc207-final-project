package views;

import interface_adapters.lecturenotes.GenerateLectureNotesController;
import interface_adapters.lecturenotes.LectureNotesState;
import interface_adapters.lecturenotes.LectureNotesViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;

/**
 * Lecture notes UI with per-course storage.
 * Left: Back, Topic, Generate, editor
 * Right: Existing notes list and actions (Refresh, Delete, Save, Highlight, Clear)
 * Notes are saved as JSON (text + highlight ranges) under ./notes/<courseId>/*.txt
 */
public class LectureNotesView extends JPanel implements PropertyChangeListener {

    // Wiring
    private final LectureNotesViewModel viewModel;
    private GenerateLectureNotesController controller; // allow setter if needed

    // UI
    private JTextField topicField;
    private JTextArea notesArea;
    private DefaultListModel<String> listModel;
    private JList<String> notesList;
    private JButton generateBtn;
    private JButton saveBtn;
    private JButton deleteBtn;
    private JButton refreshBtn;
    private JButton highlightBtn;
    private JButton clearHighlightsBtn;
    private JButton backBtn;

    // State
    private String courseId = "";                               // provided by caller
    private Runnable backAction = null;                         // provided by caller
    private final Path baseNotesDir = Paths.get("notes");       // root folder
    private Path courseNotesDir = baseNotesDir;                 // notes/<courseId>/
    private Path currentOpenFile = null;                        // file currently opened
    private final List<int[]> highlightRanges = new ArrayList<>();
    private final Highlighter.HighlightPainter painter =
            new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 255, 120));
    private final Gson gson = new Gson();

    // ---- ctor ----
    public LectureNotesView(LectureNotesViewModel viewModel,
                            GenerateLectureNotesController controller) {
        this.viewModel = viewModel;
        this.controller = controller;
        this.viewModel.addPropertyChangeListener(this);

        buildUI();
        ensureNotesDir();
        reloadList();
    }

    // Optional setter if you prefer to inject controller after construction
    public void setController(GenerateLectureNotesController controller) {
        this.controller = controller;
    }

    // Course id + navigation hook
    public void setCourseId(String courseId) {
        this.courseId = (courseId == null) ? "" : courseId.trim();
        this.courseNotesDir = this.courseId.isEmpty()
                ? baseNotesDir
                : baseNotesDir.resolve(this.courseId);
        ensureNotesDir();
        reloadList();
    }
    public void setBackAction(Runnable backAction) { this.backAction = backAction; }

    public String getViewName() { return viewModel.getViewName(); }

    // ---- UI ----
    private void buildUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new BorderLayout(8, 0));

        backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            if (backAction != null) backAction.run();
        });
        JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftTop.add(backBtn);

        JPanel centerTop = new JPanel(new BorderLayout(8, 0));
        JLabel topicLabel = new JLabel("Topic:");
        topicField = new JTextField();
        centerTop.add(topicLabel, BorderLayout.WEST);
        centerTop.add(topicField, BorderLayout.CENTER);

        generateBtn = new JButton("Generate");
        generateBtn.addActionListener(e -> onGenerate());

        top.add(leftTop, BorderLayout.WEST);
        top.add(centerTop, BorderLayout.CENTER);
        top.add(generateBtn, BorderLayout.EAST);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.7);

        notesArea = new JTextArea();
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane editorScroll = new JScrollPane(notesArea);
        split.setLeftComponent(editorScroll);

        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.add(new JLabel("Existing Notes"), BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        notesList = new JList<>(listModel);
        notesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String name = notesList.getSelectedValue();
                if (name != null) loadNoteByName(name);
            }
        });
        right.add(new JScrollPane(notesList), BorderLayout.CENTER);

        JPanel rightButtons = new JPanel(new GridLayout(0, 1, 6, 6));
        refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> reloadList());
        deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> onDeleteSelected());
        saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> onSave());
        highlightBtn = new JButton("Highlight");
        highlightBtn.addActionListener(e -> onAddHighlight());
        clearHighlightsBtn = new JButton("Clear Highlights");
        clearHighlightsBtn.addActionListener(e -> clearAllHighlights());

        rightButtons.add(refreshBtn);
        rightButtons.add(deleteBtn);
        rightButtons.add(saveBtn);
        rightButtons.add(highlightBtn);
        rightButtons.add(clearHighlightsBtn);
        right.add(rightButtons, BorderLayout.SOUTH);

        split.setRightComponent(right);

        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    // ---- Actions ----
    private void onGenerate() {
        if (controller == null) {
            JOptionPane.showMessageDialog(this, "Controller is not set.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (courseId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Course id is missing.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        final String topic = topicField.getText().trim();
        if (topic.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a topic.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final JDialog loading = createLoadingDialog("Generating notes...");
        setBusy(true);
        new Thread(() -> {
            try {
                controller.execute(courseId, topic);
            } finally {
                SwingUtilities.invokeLater(() -> {
                    setBusy(false);
                    loading.dispose();
                });
            }
        }, "notes-generate").start();
        loading.setVisible(true); // modal
    }

    private void onSave() {
        String text = notesArea.getText();
        if (text == null) text = "";

        if (currentOpenFile != null) {
            writeNotePayload(currentOpenFile, text, highlightRanges);
            JOptionPane.showMessageDialog(this, "Saved.");
            reloadList();
            return;
        }

        String suggested = topicField.getText().trim();
        if (suggested.isEmpty()) suggested = "notes";
        suggested = sanitizeFileName(suggested);

        String name = suggested;
        while (true) {
            name = (String) JOptionPane.showInputDialog(
                    this,
                    "File name (no extension):",
                    "Save Notes",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    name
            );
            if (name == null) return; // cancel
            name = sanitizeFileName(name);
            Path target = courseNotesDir.resolve(name + ".txt");
            if (Files.exists(target)) {
                JOptionPane.showMessageDialog(this, "Name already exists. Please rename.", "Name Conflict", JOptionPane.WARNING_MESSAGE);
            } else {
                currentOpenFile = target;
                writeNotePayload(target, text, highlightRanges);
                JOptionPane.showMessageDialog(this, "Saved.");
                reloadList();
                selectInList(name + ".txt");
                break;
            }
        }
    }

    private void onDeleteSelected() {
        String sel = notesList.getSelectedValue();
        if (sel == null) return;
        int ok = JOptionPane.showConfirmDialog(this, "Delete \"" + sel + "\"?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        Path p = courseNotesDir.resolve(sel);
        try {
            Files.deleteIfExists(p);
            if (currentOpenFile != null && currentOpenFile.equals(p)) {
                currentOpenFile = null;
                notesArea.setText("");
                clearAllHighlights();
            }
            reloadList();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAddHighlight() {
        int start = notesArea.getSelectionStart();
        int end = notesArea.getSelectionEnd();
        if (end <= start) return;
        try {
            notesArea.getHighlighter().addHighlight(start, end, painter);
            highlightRanges.add(new int[]{start, end});
        } catch (BadLocationException ignored) { }
    }

    private void clearAllHighlights() {
        notesArea.getHighlighter().removeAllHighlights();
        highlightRanges.clear();
    }

    // ---- Persistence ----
    private void ensureNotesDir() {
        try { Files.createDirectories(courseNotesDir); } catch (IOException ignored) { }
    }

    private void reloadList() {
        listModel.clear();
        if (!Files.isDirectory(courseNotesDir)) return;
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(courseNotesDir, "*.txt")) {
            for (Path p : ds) {
                listModel.addElement(p.getFileName().toString());
            }
        } catch (IOException ignored) { }
    }

    private void selectInList(String fileName) {
        for (int i = 0; i < listModel.size(); i++) {
            if (fileName.equals(listModel.get(i))) {
                notesList.setSelectedIndex(i);
                notesList.ensureIndexIsVisible(i);
                break;
            }
        }
    }

    private void loadNoteByName(String name) {
        Path p = courseNotesDir.resolve(name);
        try {
            String raw = Files.readString(p, StandardCharsets.UTF_8);
            NotePayload payload;
            if (raw.trim().startsWith("{")) {
                payload = gson.fromJson(raw, NotePayload.class);
                if (payload == null) payload = new NotePayload();
            } else {
                payload = new NotePayload();
                payload.text = raw;
                payload.highlights = new ArrayList<>();
            }
            currentOpenFile = p;
            notesArea.setText(payload.text == null ? "" : payload.text);
            restoreHighlights(payload.highlights);

            String base = name.endsWith(".txt") ? name.substring(0, name.length() - 4) : name;
            topicField.setText(base);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to open note.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void restoreHighlights(List<int[]> ranges) {
        clearAllHighlights();
        if (ranges == null) return;
        int len = notesArea.getText().length();
        for (int[] r : ranges) {
            if (r == null || r.length != 2) continue;
            int s = Math.max(0, Math.min(r[0], len));
            int e = Math.max(0, Math.min(r[1], len));
            if (e <= s) continue;
            try {
                notesArea.getHighlighter().addHighlight(s, e, painter);
                highlightRanges.add(new int[]{s, e});
            } catch (BadLocationException ignored) { }
        }
    }

    private void writeNotePayload(Path target, String text, List<int[]> ranges) {
        NotePayload payload = new NotePayload();
        payload.text = text;
        payload.highlights = new ArrayList<>();
        if (ranges != null) payload.highlights.addAll(ranges);

        String json = gson.toJson(payload);
        try {
            Files.createDirectories(target.getParent());
            Files.writeString(
                    target,
                    json,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException ignored) { }
    }

    private static String sanitizeFileName(String s) {
        String x = s.replaceAll("[\\\\/<>:\"|?*]", "_").trim();
        if (x.isEmpty()) {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
            x = "notes_" + df.format(new Date());
        }
        return x;
    }

    private JDialog createLoadingDialog(String text) {
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), "Please wait", Dialog.ModalityType.APPLICATION_MODAL);
        d.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        d.setLayout(new BorderLayout(8, 8));
        d.add(new JLabel(text, SwingConstants.CENTER), BorderLayout.CENTER);
        d.setSize(260, 100);
        d.setLocationRelativeTo(this);
        return d;
    }

    private void setBusy(boolean busy) {
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
        boolean enable = !busy;
        generateBtn.setEnabled(enable);
        saveBtn.setEnabled(enable);
        deleteBtn.setEnabled(enable);
        refreshBtn.setEnabled(enable);
        highlightBtn.setEnabled(enable);
        clearHighlightsBtn.setEnabled(enable);
        backBtn.setEnabled(enable);
        notesList.setEnabled(enable);
        topicField.setEnabled(enable);
        notesArea.setEditable(enable);
    }

    // ---- ViewModel binding ----
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"state".equals(evt.getPropertyName())) return;

        Object nv = evt.getNewValue();
        if (nv instanceof LectureNotesState) {
            LectureNotesState s = (LectureNotesState) nv;
            SwingUtilities.invokeLater(() -> {
                if (s.getCourseId() != null && !s.getCourseId().isEmpty()) {
                    setCourseId(s.getCourseId()); // also refreshes list
                }
                if (s.getTopic() != null) topicField.setText(s.getTopic());
                if (s.getNotesText() != null) {
                    currentOpenFile = null; // new generated content is not yet tied to a file
                    notesArea.setText(s.getNotesText());
                    clearAllHighlights();
                }
                String err = s.getError();
                if (err != null && !err.isEmpty()) {
                    JOptionPane.showMessageDialog(LectureNotesView.this, err, "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }

    // ---- payload model (JSON) ----
    private static class NotePayload {
        String text;
        List<int[]> highlights;
    }
}