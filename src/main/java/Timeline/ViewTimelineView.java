package Timeline;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ViewTimelineView extends JPanel implements PropertyChangeListener {
    private final ViewTimelineViewModel vm;
    private final TimelineController controller;
    private final DefaultListModel<ViewTimelineResponse.TimelineCardVM> listModel = new DefaultListModel<>();
    private final JList<ViewTimelineResponse.TimelineCardVM> list = new JList<>(listModel);
    private final JLabel emptyLabel = new JLabel("This page is empty", SwingConstants.CENTER);
    private final JButton refreshBtn = new JButton("Refresh");

    public ViewTimelineView(ViewTimelineViewModel vm, TimelineController controller) {
        this.vm = vm;
        this.controller = controller;
        this.vm.addPropertyChangeListener(this);

        setLayout(new BorderLayout(8, 8));
        var header = new JPanel(new BorderLayout());
        header.add(new JLabel("History", SwingConstants.LEFT), BorderLayout.WEST);
        header.add(refreshBtn, BorderLayout.EAST);

        list.setCellRenderer(new TimelineCardRenderer());

        add(header, BorderLayout.NORTH);
        add(new JScrollPane(list), BorderLayout.CENTER);
        add(emptyLabel, BorderLayout.SOUTH);

        emptyLabel.setVisible(false);

        refreshBtn.addActionListener(e -> {
            if (vm.getCourseId() != null) controller.open(vm.getCourseId());
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"timeline".equals(evt.getPropertyName())) return;

        listModel.clear();
        for (var card : vm.getItems()) listModel.addElement(card);

        emptyLabel.setVisible(vm.isEmpty());
        revalidate();
        repaint();
    }

    private static class TimelineCardRenderer extends JPanel implements ListCellRenderer<ViewTimelineResponse.TimelineCardVM> {
        private final JLabel title = new JLabel();
        private final JLabel subtitle = new JLabel();
        private final JLabel time = new JLabel();

        public TimelineCardRenderer() {
            setLayout(new BorderLayout(4, 4));
            var top = new JPanel(new BorderLayout());
            title.setFont(title.getFont().deriveFont(Font.BOLD));
            subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 11f));
            time.setFont(time.getFont().deriveFont(Font.PLAIN, 11f));
            top.add(title, BorderLayout.WEST);
            top.add(time, BorderLayout.EAST);
            add(top, BorderLayout.NORTH);
            add(subtitle, BorderLayout.SOUTH);
            setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends ViewTimelineResponse.TimelineCardVM> list,
                ViewTimelineResponse.TimelineCardVM value,
                int index, boolean isSelected, boolean cellHasFocus) {

            title.setText(value.title != null ? value.title : value.type);
            String sub = (value.subtitle == null || value.subtitle.isEmpty())
                    ? (value.snippet == null ? "" : value.snippet)
                    : value.subtitle;
            subtitle.setText(sub);
            time.setText(value.time != null ? value.time : "");

            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return this;
        }
    }

    public String getViewName() { return vm.getViewName(); }
}
