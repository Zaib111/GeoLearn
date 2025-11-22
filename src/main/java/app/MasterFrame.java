package app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import app.views.AbstractView;

/**
 * Master frame for the GeoLearn application.
 * Manages view navigation and back button functionality.
 */
public class MasterFrame extends JFrame {
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;

    private final Map<String, JPanel> views = new HashMap<>();
    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private final JButton backButton;

    private final Stack<String> navigationStack = new Stack<>();
    private String currentViewName;

    /**
     * Constructs a new MasterFrame with the specified title.
     *
     * @param title the title of the frame
     */
    public MasterFrame(String title) {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);

        final JPanel masterPanel = new JPanel(new BorderLayout());

        final JPanel topPanel = new JPanel(new BorderLayout());

        final JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("Back");
        backButton.setVisible(false);
        leftPanel.add(backButton);

        topPanel.add(leftPanel, BorderLayout.WEST);

        backButton.addActionListener(getOnBackClicked());

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        masterPanel.add(topPanel, BorderLayout.NORTH);
        masterPanel.add(contentPanel, BorderLayout.CENTER);

        this.setContentPane(masterPanel);

        this.setVisible(true);
    }

    /**
     * Gets the views map.
     *
     * @return the map of view names to panels
     */
    public Map<String, JPanel> getViews() {
        return views;
    }

    /**
     * Registers a view with the given name.
     *
     * @param view the panel to register
     * @param name the name to associate with the view
     */
    public void registerView(JPanel view, String name) {
        views.put(name, view);
        contentPanel.add(view, name);
    }

    /**
     * Navigates to the view with the specified name.
     *
     * @param name the name of the view to navigate to
     */
    public void navigateTo(String name) {
        if (views.containsKey(name)) {
            if (currentViewName != null) {
                final JPanel currentView = views.get(currentViewName);
                if (currentView instanceof AbstractView) {
                    ((AbstractView) currentView).onViewClosed();
                }
            }

            if (!navigationStack.isEmpty()) {
                final String currentView = navigationStack.peek();
                if (!currentView.equals(name)) {
                    navigationStack.push(name);
                }
            }
            else {
                navigationStack.push(name);
            }

            currentViewName = name;

            cardLayout.show(contentPanel, name);

            final JPanel newView = views.get(name);
            if (newView instanceof AbstractView) {
                ((AbstractView) newView).onViewOpened();
            }

            backButton.setVisible(navigationStack.size() > 1);
        }
    }

    private ActionListener getOnBackClicked() {
        return event -> handleBackNavigation();
    }

    private void handleBackNavigation() {
        if (navigationStack.size() > 1) {
            if (currentViewName != null) {
                final JPanel currentView = views.get(currentViewName);
                if (currentView instanceof AbstractView) {
                    ((AbstractView) currentView).onViewClosed();
                }
            }

            navigationStack.pop();
            final String previousView = navigationStack.peek();

            currentViewName = previousView;

            cardLayout.show(contentPanel, previousView);

            final JPanel prevView = views.get(previousView);
            if (prevView instanceof AbstractView) {
                ((AbstractView) prevView).onViewOpened();
            }

            backButton.setVisible(navigationStack.size() > 1);
        }
    }
}
