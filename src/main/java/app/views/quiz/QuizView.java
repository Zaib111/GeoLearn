package app.views.quiz;

import app.controllers.TakeQuizController;
import app.entities.QuestionType;
import app.entities.QuizType;
import app.views.AbstractView;
import app.views.ViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * A Swing-based view for running quizzes. Handles quiz selection,
 * question display, timers, feedback, and end-of-quiz summary UI.
 * The presenter calls showQuestion, showAnswerFeedback,
 * and showQuizEnd to update the screen.
 */
public class QuizView extends AbstractView {

    private static final int QUESTION_TIME_LIMIT_SECONDS = 30;

    private final ViewModel<QuizState> viewModel;
    private TakeQuizController controller;

    // Timer
    private final JLabel timerLabel = new JLabel("", SwingConstants.CENTER);
    private javax.swing.Timer countdownTimer;
    private int remainingSeconds;

    // Top labels
    private final JLabel quizTitleLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel progressLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel promptLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel selectionPromptLabel =
            new JLabel("Choose a quiz to start:", SwingConstants.CENTER);

    // Main quiz area
    private JPanel centerPanel;      // prompt + options + image
    private final JPanel optionsPanel = new JPanel();   // where MCQ buttons go
    private final JLabel imageLabel = new JLabel();     // for flags later

    // Bottom: selection UI
    private JPanel configPanel;
    private final JComboBox<QuizType> quizTypeCombo =
            new JComboBox<>(QuizType.values());
    private final JComboBox<QuestionType> questionTypeCombo =
            new JComboBox<>(QuestionType.values());
    private final JSpinner numQuestionsSpinner =
            new JSpinner(new SpinnerNumberModel(5, 1, 50, 1));
    private final JButton startQuizButton = new JButton("Start Quiz");
    private final JButton historyButton = new JButton("History");

    // Bottom: answer controls (shown only during quiz)
    private JPanel answerPanel;
    private final JTextField typeInField = new JTextField(20);
    private final JButton submitButton = new JButton("Submit");
    private final JButton nextButton = new JButton("Next");
    private final JButton resetButton = new JButton("Reset");

    // History tab
    private DefaultTableModel historyModel;
    private JTable historyTable;
    private int attemptCounter = 0;

    private QuizType currentQuizType;
    private QuestionType currentQuestionType;
    private int currentTotalQuestions;

    /**
     * Creates the quiz view and builds all UI components.
     * The presenter updates this view through the provided ViewModel.
     */
    public QuizView(ViewModel<QuizState> quizViewModel) {
        super(quizViewModel);
        this.viewModel = quizViewModel;

        buildUI();
        initListeners();
    }

    /**
     * Builds all Swing UI components: selection menu, question UI,
     * feedback area, timer, and history table.
     */
    private void buildUI() {
        setLayout(new BorderLayout());

        // TOP: title + progress + timer
        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        topPanel.add(quizTitleLabel);
        topPanel.add(progressLabel);
        topPanel.add(timerLabel);
        add(topPanel, BorderLayout.NORTH);

        quizTitleLabel.setText("Choose a quiz to start");
        timerLabel.setText("Time: --");

        // CENTER: prompt + options + (optional) image
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(promptLabel, BorderLayout.NORTH);

        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        centerPanel.add(optionsPanel, BorderLayout.CENTER);

        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(imageLabel, BorderLayout.EAST);

        add(centerPanel, BorderLayout.CENTER);

        showSelectionHero();

        historyModel = new DefaultTableModel(
                new Object[]{"Attempt", "Quiz Type", "Mode", "Score"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Selection controls
        configPanel = new JPanel();
        configPanel.add(new JLabel("Quiz:"));
        configPanel.add(quizTypeCombo);
        configPanel.add(new JLabel("Mode:"));
        configPanel.add(questionTypeCombo);
        configPanel.add(new JLabel("#Q:"));
        configPanel.add(numQuestionsSpinner);
        configPanel.add(historyButton);
        configPanel.add(startQuizButton);

        // Answer controls (only visible during quiz)
        answerPanel = new JPanel();
        answerPanel.add(typeInField);
        answerPanel.add(submitButton);
        answerPanel.add(resetButton);
        answerPanel.add(nextButton);

        // Wrap the selection prompt + selection controls
        JPanel selectionContainer = new JPanel(new BorderLayout());
        selectionContainer.add(selectionPromptLabel, BorderLayout.NORTH);
        selectionContainer.add(configPanel, BorderLayout.CENTER);

        // Combine into bottomPanel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(selectionContainer, BorderLayout.NORTH);
        bottomPanel.add(answerPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        centerPanel.setVisible(true);
        answerPanel.setVisible(false);
        typeInField.setVisible(false);
        submitButton.setVisible(false);
        nextButton.setVisible(false);
        resetButton.setVisible(false);
        imageLabel.setVisible(false);
    }

    /**
     * Registers all button listeners for starting quizzes,
     * submitting answers, navigating questions, viewing history,
     * and resetting back to the selection screen.
     */
    private void initListeners() {
        // Start Quiz: go from selection UI to quiz UI and call controller
        startQuizButton.addActionListener(e -> {
            if (controller != null) {
                QuizType quizType =
                        (QuizType) quizTypeCombo.getSelectedItem();
                QuestionType questionType =
                        (QuestionType) questionTypeCombo.getSelectedItem();
                int n = (int) numQuestionsSpinner.getValue();

                currentQuizType = quizType;
                currentQuestionType = questionType;
                currentTotalQuestions = n;

                // Switch UI: hide selection, show quiz
                selectionPromptLabel.setVisible(false);
                configPanel.setVisible(false);
                answerPanel.setVisible(true);
                resetButton.setVisible(true);

                // Prepare centerPanel for quiz mode: prompt + options
                centerPanel.removeAll();
                centerPanel.setLayout(new BorderLayout());
                centerPanel.add(promptLabel, BorderLayout.NORTH);
                centerPanel.add(optionsPanel, BorderLayout.CENTER);
                centerPanel.add(imageLabel, BorderLayout.EAST);

                timerLabel.setVisible(true);

                controller.startQuiz(quizType, questionType, n);
            }
        });

        historyButton.addActionListener(e -> showHistoryDialog());

        // Type-in submit
        submitButton.addActionListener(e -> {
            if (controller != null) {
                controller.submitAnswer(typeInField.getText());
            }
        });

        // Next question
        nextButton.addActionListener(e -> {
            if (controller != null) {
                controller.nextQuestion();
            }
        });

        resetButton.addActionListener(e -> resetToSelection());
    }

    /**
     * Called by Main to attach the controller. Enables user actions
     * to trigger use case requests.
     */
    public void setController(TakeQuizController controller) {
        this.controller = controller;
    }

    /**
     * Displays a new question from the presenter. Shows prompt, MCQ buttons
     * or a type-in field, optional image, progress text, and starts timer.
     */
    public void showQuestion(String quizTitle,
                             String prompt,
                             List<String> options,
                             int questionIndex,
                             int totalQuestions,
                             String mediaUrl) {

        quizTitleLabel.setText(quizTitle == null ? "" : quizTitle);
        promptLabel.setText(prompt);
        progressLabel.setText("Question " + (questionIndex + 1)
                + " of " + totalQuestions);

        // Make sure we are in quiz mode (selection hidden)
        configPanel.setVisible(false);
        centerPanel.setVisible(true);
        answerPanel.setVisible(true);
        resetButton.setVisible(true);

        // Reset
        optionsPanel.removeAll();
        imageLabel.setVisible(false);
        typeInField.setText("");

        if (options == null || options.isEmpty()) {
            // TYPE-IN mode
            typeInField.setVisible(true);
            submitButton.setVisible(true);
        } else {
            // MCQ mode
            typeInField.setVisible(false);
            submitButton.setVisible(false);

            optionsPanel.removeAll();
            optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

            for (String opt : options) {
                JButton btn = new JButton(opt);

                // 🔹 Style the button (rounded + hover + press)
                styleAnswerButton(btn);

                btn.addActionListener(e -> {
                    if (controller != null) {
                        controller.submitAnswer(opt);
                    }
                });

                // Center the button and add spacing between them
                JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
                wrapper.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
                wrapper.setOpaque(false);  // let parent background show

                wrapper.add(btn);
                optionsPanel.add(wrapper);
            }
        }

        nextButton.setVisible(false);

        // Optional image
        if (mediaUrl != null && !mediaUrl.isEmpty()) {
            try {
                imageLabel.setText("");
                imageLabel.setIcon(null);

                if (mediaUrl.startsWith("http")) {
                    // Remote image from REST Countries (flag URL)
                    java.net.URL url = new java.net.URL(mediaUrl);
                    imageLabel.setIcon(new javax.swing.ImageIcon(url));
                } else {
                    // Classpath resource like "/flags/ca.png"
                    java.net.URL res = getClass().getResource(mediaUrl);
                    if (res != null) {
                        imageLabel.setIcon(new javax.swing.ImageIcon(res));
                    } else {
                        imageLabel.setText("[image not found]");
                    }
                }

                imageLabel.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                imageLabel.setIcon(null);
                imageLabel.setText("[image failed to load]");
                imageLabel.setVisible(true);
            }
        } else {
            imageLabel.setIcon(null);
            imageLabel.setVisible(false);
        }


        // Start countdown
        startTimer(QUESTION_TIME_LIMIT_SECONDS);

        revalidate();
        repaint();
    }

    /**
     * Displays correctness feedback, explanation, updated score,
     * and streak info. Stops the timer and waits for user to click Next.
     */
    public void showAnswerFeedback(String feedbackMessage,
                                   String correctAnswer,
                                   String explanation,
                                   int score,
                                   int currentStreak,
                                   int highestStreak) {

        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        optionsPanel.removeAll();
        optionsPanel.setLayout(new GridLayout(0, 1));

        optionsPanel.add(new JLabel(feedbackMessage));
        optionsPanel.add(new JLabel("Correct answer: " + correctAnswer));
        optionsPanel.add(new JLabel("Explanation: " + explanation));
        optionsPanel.add(new JLabel("Score: " + score));
        optionsPanel.add(new JLabel("Streak: "
                + currentStreak + " (Best: " + highestStreak + ")"));

        // After feedback, hide type-in stuff and show Next
        typeInField.setVisible(false);
        submitButton.setVisible(false);
        nextButton.setVisible(true);

        revalidate();
        repaint();
    }

    /**
     * Displays final quiz results in a dialog, records the attempt
     * in the session history table, and returns UI to selection mode.
     */
    public void showQuizEnd(String summaryText,
                            int score,
                            int totalQuestions,
                            int durationSeconds,
                            int highestStreak) {

        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        // Show summary dialog
        String message = summaryText + "\n"
                + "Score: " + score + "/" + totalQuestions + "\n"
                + "Time: " + durationSeconds + " seconds\n"
                + "Best streak: " + highestStreak;
        JOptionPane.showMessageDialog(
                this,
                message,
                "Quiz Finished",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Add a row to the history table
        attemptCounter++;
        String quizTypeLabel = (currentQuizType != null)
                ? currentQuizType.getDisplayName()
                : "Unknown";
        String modeLabel = (currentQuestionType != null)
                ? currentQuestionType.toString()
                : "Unknown";
        String scoreLabel = score + " / " + totalQuestions;

        historyModel.addRow(new Object[]{
                attemptCounter,
                quizTypeLabel,
                modeLabel,
                scoreLabel
        });

        // Return to selection page
        resetToSelection();
    }

    /**
     * Starts a per-question countdown timer. If time reaches zero,
     * notifies controller through timeExpired().
     */
    private void startTimer(int seconds) {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        remainingSeconds = seconds;
        timerLabel.setText("Time: " + remainingSeconds + "s");

        countdownTimer = new javax.swing.Timer(1000, e -> {
            remainingSeconds--;
            if (remainingSeconds <= 0) {
                countdownTimer.stop();
                timerLabel.setText("Time: 0s");
                if (controller != null) {
                    controller.timeExpired();
                }
            } else {
                timerLabel.setText("Time: " + remainingSeconds + "s");
            }
        });

        countdownTimer.start();
    }

    /**
     * Resets UI back to quiz selection mode. Clears prompts, options,
     * hides answer controls, and restores the selection hero layout.
     */
    private void resetToSelection() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        // Reset labels
        quizTitleLabel.setText("GeoLearn Quiz");
        promptLabel.setText("");
        progressLabel.setText("");
        timerLabel.setText("Time: --");
        timerLabel.setVisible(false);

        selectionPromptLabel.setVisible(true);

        // Show selection UI, hide quiz UI
        configPanel.setVisible(true);
        centerPanel.setVisible(true);
        answerPanel.setVisible(false);

        typeInField.setVisible(false);
        submitButton.setVisible(false);
        nextButton.setVisible(false);
        resetButton.setVisible(false);

        optionsPanel.removeAll();
        imageLabel.setVisible(false);

        showSelectionHero();

        revalidate();
        repaint();
    }

    /**
     * Opens a dialog showing the session's quiz attempt history
     * (quiz type, mode, and score).
     */
    private void showHistoryDialog() {
        if (historyTable == null) {
            historyTable = new JTable(historyModel);
            historyTable.setFillsViewportHeight(true);
        }

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setPreferredSize(new Dimension(500, 200));

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Quiz History",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setLayout(new BorderLayout());
        dialog.add(new JLabel("Quiz History (this session):",
                SwingConstants.CENTER), BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Displays the main menu hero layout with the big GeoLearn title
     * and decorative icons. Used when no quiz is running.
     */
    private void showSelectionHero() {
        // Big title text
        quizTitleLabel.setText("GeoLearn Quiz");
        quizTitleLabel.setFont(quizTitleLabel.getFont().deriveFont(Font.BOLD, 32f));

        promptLabel.setText("");
        timerLabel.setVisible(false);

        centerPanel.removeAll();
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Center with title
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(quizTitleLabel, gbc);

        // Around the title with emojis
        JLabel topImage = makeHeroImageLabel("🌍");
        JLabel bottomImage = makeHeroImageLabel("🏛️");
        JLabel leftImage = makeHeroImageLabel("🚩");
        JLabel rightImage = makeHeroImageLabel("💱");

        // Top
        gbc.gridx = 1;
        gbc.gridy = 0;
        centerPanel.add(topImage, gbc);

        // Bottom
        gbc.gridx = 1;
        gbc.gridy = 2;
        centerPanel.add(bottomImage, gbc);

        // Left
        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(leftImage, gbc);

        // Right
        gbc.gridx = 2;
        gbc.gridy = 1;
        centerPanel.add(rightImage, gbc);

        centerPanel.revalidate();
        centerPanel.repaint();
    }

    /**
     * Creates a large emoji label used in the hero layout.
     */
    private JLabel makeHeroImageLabel(String emoji) {
        JLabel label = new JLabel(emoji, SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(32f));
        return label;
    }

    private static class RoundedBorder implements javax.swing.border.Border {
        private final int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y,
                                int width, int height) {
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }


    /**
     * Called when the quiz view becomes visible. Restores selection mode.
     */
    @Override
    public void onViewOpened() {
        resetToSelection();
    }

    /**
     * Called when switching away from the quiz view. Stops active timers.
     */
    @Override
    public void onViewClosed() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
    }

    /**
     * Currently unused, if presenter later writes directly into QuizState,
     * this method will update the UI in response to state changes.
     */
    @Override
    public void onStateChange(Object oldState, Object newState) {
        if (!(newState instanceof QuizState)) {
            return;
        }
    }

    /**
     * Apply consistent styling + hover/press effects to answer buttons.
     */
    private void styleAnswerButton(JButton btn) {
        // Base colours
        Color normalBg = new Color(230, 230, 255);
        Color hoverBg  = new Color(210, 210, 245);
        Color pressedBg = new Color(190, 190, 235);

        btn.setBorder(new RoundedBorder(12));
        btn.setFocusPainted(false);
        btn.setBackground(normalBg);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(250, 40));
        btn.setFont(btn.getFont().deriveFont(Font.PLAIN, 16f));

        // Hover (4)
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(hoverBg);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(normalBg);
                }
            }
        });

        // Press "animation" + active look (5, 7)
        btn.getModel().addChangeListener(e -> {
            ButtonModel model = (ButtonModel) e.getSource();
            if (!btn.isEnabled()) {
                return;
            }

            if (model.isPressed()) {
                // While button is pressed
                btn.setBackground(pressedBg);
            } else if (model.isRollover()) {
                // Hover but not pressed
                btn.setBackground(hoverBg);
            } else {
                // Default
                btn.setBackground(normalBg);
            }
        });
    }

}
