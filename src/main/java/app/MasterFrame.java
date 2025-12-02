package app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import app.views.AbstractView;

/**
 * Master frame for the GeoLearn application.
 * Manages view navigation and back button functionality.
 * Singleton pattern ensures only one instance exists.
 */
public final class MasterFrame extends JFrame implements NavigationService {
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;

    private static MasterFrame instance;

    private final Map<String, JPanel> views = new HashMap<>();
    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private final JButton backButton;

    private final Stack<List<String>> navigationStack = new Stack<>();
    private String currentViewName;

    /**
     * Private constructor to prevent direct instantiation.
     *
     * @param title the title of the frame
     */
    private MasterFrame(String title) {
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
     * Gets the singleton instance of MasterFrame.
     *
     * @return the singleton instance
     */
    public static MasterFrame getInstance() {
        if (instance == null) {
            instance = new MasterFrame("GeoLearn");
        }
        return instance;
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
    @Override
    public void navigateTo(String name) {
        navigateTo(name, "");
    }

    /**
     * Navigates to the view with the specified name and parameter.
     *
     * @param name the name of the view to navigate to
     * @param param the parameter to pass when navigating
     */
    @Override
    public void navigateTo(String name, String param) {
        if (views.containsKey(name)) {
            if (currentViewName != null) {
                final JPanel currentView = views.get(currentViewName);
                if (currentView instanceof AbstractView) {
                    ((AbstractView) currentView).onViewClosed();
                }
            }

            if (!navigationStack.isEmpty()) {
                final String currentView = navigationStack.peek().get(0);
                final String currentParam = navigationStack.peek().get(1);
                if (!currentView.equals(name) || !currentParam.equals(param)) {
                    navigationStack.push(List.of(name, param));
                }
            }
            else {
                navigationStack.push(List.of(name, param));
            }

            currentViewName = name;

            cardLayout.show(contentPanel, name);

            final JPanel newView = views.get(name);
            if (newView instanceof AbstractView) {
                this.toFront();
                ((AbstractView) newView).onViewOpened(param);
            }

            updateBackButton();
        }
    }

    /**
     * Updates the back button visibility and text based on navigation state.
     */
    private void updateBackButton() {
        if (navigationStack.size() > 1) {
            backButton.setVisible(true);
            final String previousView = navigationStack.get(navigationStack.size() - 2).get(0);
            if ("authentication".equals(previousView)) {
                backButton.setText("Sign Out");
            }
            else {
                backButton.setText("Back");
            }
        }
        else {
            backButton.setVisible(false);
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
            final String previousView = navigationStack.peek().get(0);
            final String previousParam = navigationStack.peek().get(1);

            currentViewName = previousView;

            cardLayout.show(contentPanel, previousView);

            final JPanel prevView = views.get(previousView);
            if (prevView instanceof AbstractView) {
                ((AbstractView) prevView).onViewOpened(previousParam);
            }

            updateBackButton();
        }
    }

    /**
     * Allows views to explicitly control the visibility of the global back button.
     *
     * For example, CompareView can hide the back button on its internal
     * comparison screen while still allowing normal back behavior elsewhere.
     *
     * @param visible true to show the back button, false to hide it
     */
    @Override
    public void setBackButtonVisible(boolean visible) {
        backButton.setVisible(visible);
    }
}
