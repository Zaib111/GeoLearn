package app.views.home;

import app.NavigationService;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HomeView extends JPanel {
    private static final int LOGO_MAX_HEIGHT = 175;

    private final Map<String, JButton> buttons;

    public HomeView(NavigationService navigator) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        // Spacing to center content vertically
        add(Box.createVerticalGlue());

        // Load and display GeoLearn logo image
        try (InputStream is = getClass().getResourceAsStream("/images/GeoLearn.png")) {
            if (is != null) {
                BufferedImage logoImage = ImageIO.read(is);

                // Calculate scaled dimensions to maintain aspect ratio
                int originalWidth = logoImage.getWidth();
                int originalHeight = logoImage.getHeight();
                int scaledHeight = LOGO_MAX_HEIGHT;
                int scaledWidth = (originalWidth * LOGO_MAX_HEIGHT) / originalHeight;

                // Use the original high-res image and let Java's default scaling handle it
                // This preserves quality better for high-resolution source images
                Image scaledImage = logoImage.getScaledInstance(
                        scaledWidth,
                        scaledHeight,
                        Image.SCALE_AREA_AVERAGING  // Best for downscaling high-res images
                );

                JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
                logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                add(logoLabel);
            } else {
                // Fallback to text if image not found
                JLabel title = new JLabel("geolearn");
                title.setFont(new Font("SansSerif", Font.BOLD, 36));
                title.setAlignmentX(Component.CENTER_ALIGNMENT);
                title.setHorizontalAlignment(SwingConstants.CENTER);
                add(title);
            }
        } catch (IOException e) {
            // Fallback to text if image loading fails
            JLabel title = new JLabel("geolearn");
            title.setFont(new Font("SansSerif", Font.BOLD, 36));
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            title.setHorizontalAlignment(SwingConstants.CENTER);
            add(title);
            System.err.println("Failed to load GeoLearn logo: " + e.getMessage());
        }

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