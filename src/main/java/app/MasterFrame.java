package app;

import app.views.AbstractView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class MasterFrame extends JFrame {
    public Map<String, JPanel> views = new HashMap<>();
    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private final JButton backButton;

    private Stack<String> navigationStack = new Stack<>();
    private String currentViewName = null;

    public MasterFrame(String title) {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Add a master panel with BorderLayout
        JPanel masterPanel = new JPanel(new BorderLayout());

        // Create top app.navigation panel with BorderLayout
        JPanel topPanel = new JPanel(new BorderLayout());

        // Left side for back button
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("Back");
        backButton.setVisible(false); // Initially hidden
        leftPanel.add(backButton);

        topPanel.add(leftPanel, BorderLayout.WEST);

        backButton.addActionListener(getOnBackClicked());

        // Create content panel with CardLayout for dynamic view swapping
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Add panels to master panel
        masterPanel.add(topPanel, BorderLayout.NORTH);
        masterPanel.add(contentPanel, BorderLayout.CENTER);

        this.setContentPane(masterPanel);

        this.setVisible(true);
    }

    public void registerView(JPanel view, String name) {
        views.put(name, view);
        contentPanel.add(view, name);
    }

    public void navigateTo(String name) {
        if (views.containsKey(name)) {
            // Trigger onViewClosed for the current view if it's an AbstractView
            if (currentViewName != null) {
                JPanel currentView = views.get(currentViewName);
                if (currentView instanceof AbstractView) {
                    ((AbstractView) currentView).onViewClosed();
                }
            }

            if (!navigationStack.isEmpty()) {
                String currentView = navigationStack.peek();
                if (!currentView.equals(name)) {
                    navigationStack.push(name);
                }
            } else {
                navigationStack.push(name);
            }

            // Update current view name
            currentViewName = name;

            cardLayout.show(contentPanel, name);

            // Trigger onViewOpened for the new view if it's an AbstractView
            JPanel newView = views.get(name);
            if (newView instanceof AbstractView) {
                ((AbstractView) newView).onViewOpened();
            }

            // Update back button visibility
            backButton.setVisible(navigationStack.size() > 1);
        }
    }

    private ActionListener getOnBackClicked() {
        return e -> {
            if (navigationStack.size() > 1) {
                // Trigger onViewClosed for the current view if it's an AbstractView
                if (currentViewName != null) {
                    JPanel currentView = views.get(currentViewName);
                    if (currentView instanceof AbstractView) {
                        ((AbstractView) currentView).onViewClosed();
                    }
                }

                // Pop current view
                navigationStack.pop();
                // Get previous view
                String previousView = navigationStack.peek();

                // Update current view name
                currentViewName = previousView;

                cardLayout.show(contentPanel, previousView);

                // Trigger onViewOpened for the previous view if it's an AbstractView
                JPanel prevView = views.get(previousView);
                if (prevView instanceof AbstractView) {
                    ((AbstractView) prevView).onViewOpened();
                }

                // Update back button visibility
                backButton.setVisible(navigationStack.size() > 1);
            }
        };
    }
}
