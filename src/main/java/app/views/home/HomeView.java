package app.views.home;

import app.Navigator;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class HomeView extends JPanel {
    private final Map<String, JButton> buttons;

    public HomeView(Navigator navigator) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        // Big centered heading
        JLabel title = new JLabel("GeoLearn");
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        // Spacing to center content vertically
        add(Box.createVerticalGlue());
        add(title);
        add(Box.createVerticalStrut(30));

        // Create buttons for each view
        buttons = new HashMap<>();
        String[] viewNames = {"World Map", "Filter Countries", "Compare", "Quiz", "Collections"};

        for (String viewName : viewNames) {
            JButton button = new JButton(viewName);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(200, 40));

            // Convert display name to view key (handle "Collections" -> "collection")
            String viewKey = viewName.toLowerCase().replace(" ", "_");
            if (viewKey.equals("collections")) {
                viewKey = "collection";
            }

            if (viewKey.equals("compare")) {
                viewKey = "compare_countries";
            }

            if (viewKey.equals("world_map")) {
                viewKey = "explore_map";
            }

            final String finalViewKey = viewKey;
            buttons.put(viewKey, button);

            // Add action listener to navigate when button is clicked
            button.addActionListener(e -> navigator.navigateTo(finalViewKey));

            add(button);
            add(Box.createVerticalStrut(10));
        }

        add(Box.createVerticalGlue());
    }
}