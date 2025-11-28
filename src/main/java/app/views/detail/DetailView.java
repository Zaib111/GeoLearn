package app.views.detail;

import app.controllers.DetailController;
import app.use_cases.detail.DetailInputData;
import app.views.AbstractView;
import app.views.ViewModel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class DetailView extends AbstractView{
    private final DetailController controller;

    private final JLabel titleLabel = new JLabel();
    private final JLabel flagLabel = new JLabel();
    private final JLabel codeLabel = new JLabel();
    private final JLabel capitalLabel = new JLabel();
    private final JLabel regionLabel = new JLabel();
    private final JLabel populationLabel = new JLabel();
    private final JLabel areaLabel = new JLabel();
    private final JTextArea bordersArea = new JTextArea(3, 20);
    private final JTextArea languagesArea = new JTextArea(3, 20);
    private final JTextArea currenciesArea = new JTextArea(3, 20);
    private final JTextArea timezonesArea = new JTextArea(3, 20);

    public DetailView(ViewModel<DetailState> detailViewModel, DetailController controller) {
        super(detailViewModel);
        this.controller = controller;

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        headerPanel.add(titleLabel);

        JPanel flagPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        flagPanel.add(flagLabel);
        flagLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        addDetailPanel(detailsPanel, "Code:", codeLabel);
        addDetailPanel(detailsPanel, "Capital:", capitalLabel);
        addDetailPanel(detailsPanel, "Region:", regionLabel);
        addDetailPanel(detailsPanel, "Population:", populationLabel);
        addDetailPanel(detailsPanel, "Area (km²):", areaLabel);

        addTextAreaPanel(detailsPanel, "Borders:", bordersArea);
        addTextAreaPanel(detailsPanel, "Languages:", languagesArea);
        addTextAreaPanel(detailsPanel, "Currencies:", currenciesArea);
        addTextAreaPanel(detailsPanel, "Timezones:", timezonesArea);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.WEST);
        topPanel.add(flagPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JScrollPane scrollDetails = new JScrollPane(detailsPanel);

        add(scrollDetails, BorderLayout.CENTER);

        this.revalidate();
        this.repaint();
    }

    @Override
    public void onViewOpened(String param) {
        DetailInputData inputData = new DetailInputData(param);
        controller.loadDetails(inputData);
        this.revalidate();
        this.repaint();
    }

    @Override
    public void onViewClosed() {}

    @Override
    public void onStateChange(Object oldState, Object newState) {
        DetailState detailState = (DetailState) newState;
        // Update view
        titleLabel.setText(detailState.getCountryName());
        codeLabel.setText(detailState.getCountryCode());
        displayFlag(detailState.getFlagUrl());
        String capital = detailState.getCapital().orElse("N/A");
        capitalLabel.setText(capital);
        regionLabel.setText(detailState.getRegion() + detailState.getSubregion().map(s -> " (" + s + ")").orElse(""));
        populationLabel.setText(String.format("%,d", detailState.getPopulation()));
        areaLabel.setText(String.format("%,.2f", detailState.getAreaKm2()));
        bordersArea.setText(joinList(detailState.getBorders()));
        languagesArea.setText(joinList(detailState.getLanguages()));
        currenciesArea.setText(joinList(detailState.getCurrencies()));
        timezonesArea.setText(joinList(detailState.getTimezones()));

        this.revalidate();
        this.repaint();
    }

    // Helper function for adding the entries for each country attribute
    private void addDetailPanel(JPanel targetPanel, String title, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.CENTER);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Ensures the panel takes up the full width for proper alignment
        Dimension d = panel.getPreferredSize();
        d.width = Integer.MAX_VALUE;
        panel.setMaximumSize(d);

        targetPanel.add(panel);
    }

    // Same helper but for the entries that use TextAreaPanel
    private void addTextAreaPanel(JPanel targetPanel, String title, JTextArea textArea) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, 60));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(scrollPane);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Ensures the panel takes up the full width for proper alignment
        Dimension d = panel.getPreferredSize();
        d.width = Integer.MAX_VALUE;
        panel.setMaximumSize(d);

        targetPanel.add(panel);
        targetPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private String joinList(List<String> list) {
        return list != null ? String.join(", ", list) : "N/A";
    }

    // Flag display helper
    private void displayFlag(String flagUrl) {
        if (flagUrl != null && !flagUrl.isEmpty()) {
            try {
                URL url = new URL(flagUrl);
                ImageIcon icon = new ImageIcon(url);

                // Optional: Scale the image down if it's too large
                int width = 100;
                int height = (icon.getIconHeight() * width) / icon.getIconWidth();
                Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                flagLabel.setIcon(new ImageIcon(scaledImage));

            } catch (IOException e) {
                flagLabel.setText("Flag not available");
                flagLabel.setIcon(null);
                System.err.println("Failed to load flag image: " + e.getMessage());
            } catch (Exception e) {
                flagLabel.setText("Flag not available");
                flagLabel.setIcon(null);
            }
        } else {
            flagLabel.setText("Flag N/A");
            flagLabel.setIcon(null);
        }
    }
}

