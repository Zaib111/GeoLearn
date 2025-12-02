
package app.views.quiz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import app.controllers.TakeQuizController;
import app.entities.QuestionType;
import app.entities.QuizType;
import app.views.AbstractView;
import app.views.ViewModel;

/**
 * A Swing-based view for running quizzes. Handles quiz selection,
 * question display, timers, feedback, and end-of-quiz summary UI.
 * The presenter calls showQuestion, showAnswerFeedback,
 * and showQuizEnd to update the screen.
 */
public class QuizView extends AbstractView {

    private static final int QUESTION_TIME_LIMIT_SECONDS = 30;
    private static final String UNKNOWN_LABEL = "Unknown";
    private static final String TIME_PREFIX = "Time: ";
    private static final Logger LOGGER = Logger.getLogger(QuizView.class.getName());

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
    private JPanel centerPanel;
    private final JPanel optionsPanel = new JPanel();
    private final JLabel imageLabel = new JLabel();

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
    private int attemptCounter;

    private QuizType currentQuizType;
    private QuestionType currentQuestionType;

    /**
     * Creates the quiz view and builds all UI components.
     * The presenter updates this view through the provided ViewModel.
     * @param quizViewModel - The Quiz View Model
     */
    public QuizView(ViewModel<QuizState> quizViewModel) {
        super(quizViewModel);

        buildUserInterface();
        initListeners();
    }

    /**
     * Builds all Swing UI components: selection menu, question UI,
     * feedback area, timer, and history table.
     */
    private void buildUserInterface() {
        setLayout(new BorderLayout());

        final JPanel topPanel = new JPanel(new GridLayout(3, 1));
        topPanel.add(quizTitleLabel);
        topPanel.add(progressLabel);
        topPanel.add(timerLabel);
        add(topPanel, BorderLayout.NORTH);

        quizTitleLabel.setText("Choose a quiz to start");
        timerLabel.setText(TIME_PREFIX + "--");

        centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(promptLabel, BorderLayout.NORTH);

        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        centerPanel.add(optionsPanel, BorderLayout.CENTER);

        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(imageLabel, BorderLayout.EAST);

        add(centerPanel, BorderLayout.CENTER);

        showSelectionHero();

        createHistoryModel();
        createConfigPanel();
        createAnswerPanel();

        final JPanel selectionContainer = createSelectionContainer();
        final JPanel bottomPanel = createBottomPanel(selectionContainer);

        add(bottomPanel, BorderLayout.SOUTH);

        setInitialVisibility();
    }

    private void createHistoryModel() {
        historyModel = new DefaultTableModel(
                new Object[]{"Attempt", "Quiz Type", "Mode", "Score"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void createConfigPanel() {
        configPanel = new JPanel();
        configPanel.add(new JLabel("Quiz:"));
        configPanel.add(quizTypeCombo);
        configPanel.add(new JLabel("Mode:"));
        configPanel.add(questionTypeCombo);
        configPanel.add(new JLabel("#Q:"));
        configPanel.add(numQuestionsSpinner);
        configPanel.add(historyButton);
        configPanel.add(startQuizButton);
    }

    private void createAnswerPanel() {
        answerPanel = new JPanel();
        answerPanel.add(typeInField);
        answerPanel.add(submitButton);
        answerPanel.add(resetButton);
        answerPanel.add(nextButton);
    }

    private JPanel createSelectionContainer() {
        final JPanel selectionContainer = new JPanel(new BorderLayout());
        selectionContainer.add(selectionPromptLabel, BorderLayout.NORTH);
        selectionContainer.add(configPanel, BorderLayout.CENTER);
        return selectionContainer;
    }

    private JPanel createBottomPanel(JPanel selectionContainer) {
        final JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(selectionContainer, BorderLayout.NORTH);
        bottomPanel.add(answerPanel, BorderLayout.SOUTH);
        return bottomPanel;
    }

    private void setInitialVisibility() {
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
        startQuizButton.addActionListener(event -> handleStartQuiz());
        historyButton.addActionListener(event -> handleHistoryRequest());
        submitButton.addActionListener(event -> handleSubmitAnswer());
        nextButton.addActionListener(event -> handleNextQuestion());
        resetButton.addActionListener(event -> resetToSelection());
    }

    private void handleStartQuiz() {
        if (controller != null) {
            final QuizType quizType = (QuizType) quizTypeCombo.getSelectedItem();
            final QuestionType questionType = (QuestionType) questionTypeCombo.getSelectedItem();
            final int questionCount = (int) numQuestionsSpinner.getValue();

            currentQuizType = quizType;
            currentQuestionType = questionType;

            switchToQuizMode();

            controller.startQuiz(quizType, questionType, questionCount);
        }
    }

    private void switchToQuizMode() {
        selectionPromptLabel.setVisible(false);
        configPanel.setVisible(false);
        answerPanel.setVisible(true);
        resetButton.setVisible(true);

        centerPanel.removeAll();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(promptLabel, BorderLayout.NORTH);
        centerPanel.add(optionsPanel, BorderLayout.CENTER);
        centerPanel.add(imageLabel, BorderLayout.EAST);

        timerLabel.setVisible(true);
    }

    private void handleHistoryRequest() {
        if (controller != null) {
            controller.loadQuizHistory();
        }
    }

    private void handleSubmitAnswer() {
        if (controller != null) {
            controller.submitAnswer(typeInField.getText());
        }
    }

    private void handleNextQuestion() {
        if (controller != null) {
            controller.nextQuestion();
        }
    }

    /**
     * Called by Main to attach the controller. Enables user actions
     * to trigger use case requests.
     * @param controller the quiz controller to set
     */
    public void setController(TakeQuizController controller) {
        this.controller = controller;
    }

    /**
     * Displays a new question from the presenter. Shows prompt, MCQ buttons
     * or a type-in field, optional image, progress text, and starts timer.
     * @param quizTitle the title of the quiz
     * @param prompt the question prompt
     * @param options the list of answer options (null for type-in mode)
     * @param questionIndex the current question index
     * @param totalQuestions the total number of questions
     * @param mediaUrl the URL or path to media content
     */
    public void showQuestion(String quizTitle,
                             String prompt,
                             List<String> options,
                             int questionIndex,
                             int totalQuestions,
                             String mediaUrl) {

        setQuizTitle(quizTitle);
        promptLabel.setText(prompt);
        progressLabel.setText("Question " + (questionIndex + 1)
                + " of " + totalQuestions);

        prepareQuizMode();
        resetQuestionDisplay();

        if (options == null || options.isEmpty()) {
            showTypeInMode();
        }
        else {
            showMultipleChoiceMode(options);
        }

        nextButton.setVisible(false);
        displayMediaIfAvailable(mediaUrl);
        startTimer(QUESTION_TIME_LIMIT_SECONDS);

        revalidate();
        repaint();
    }

    private void setQuizTitle(String quizTitle) {
        if (quizTitle == null) {
            quizTitleLabel.setText("");
        }
        else {
            quizTitleLabel.setText(quizTitle);
        }
    }

    private void prepareQuizMode() {
        configPanel.setVisible(false);
        centerPanel.setVisible(true);
        answerPanel.setVisible(true);
        resetButton.setVisible(true);
    }

    private void resetQuestionDisplay() {
        optionsPanel.removeAll();
        imageLabel.setVisible(false);
        typeInField.setText("");
    }

    private void showTypeInMode() {
        typeInField.setVisible(true);
        submitButton.setVisible(true);
    }

    private void showMultipleChoiceMode(List<String> options) {
        typeInField.setVisible(false);
        submitButton.setVisible(false);

        optionsPanel.removeAll();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

        for (String opt : options) {
            final JButton btn = new JButton(opt);
            styleAnswerButton(btn);

            btn.addActionListener(event -> {
                if (controller != null) {
                    controller.submitAnswer(opt);
                }
            });

            final JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            wrapper.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            // Let parent background show
            wrapper.setOpaque(false);

            wrapper.add(btn);
            optionsPanel.add(wrapper);
        }
    }

    private void displayMediaIfAvailable(String mediaUrl) {
        if (mediaUrl != null && !mediaUrl.isEmpty()) {
            try {
                imageLabel.setText("");
                imageLabel.setIcon(null);

                if (mediaUrl.startsWith("http")) {
                    loadRemoteImage(mediaUrl);
                }
                else {
                    loadLocalImage(mediaUrl);
                }

                imageLabel.setVisible(true);
            }
            catch (java.io.IOException ex) {
                LOGGER.warning("Failed to load image: " + ex.getMessage());
                imageLabel.setIcon(null);
                imageLabel.setText("[image failed to load]");
                imageLabel.setVisible(true);
            }
        }
        else {
            imageLabel.setIcon(null);
            imageLabel.setVisible(false);
        }
    }

    private void loadRemoteImage(String mediaUrl) throws java.io.IOException {
        final java.net.URL url = new java.net.URL(mediaUrl);
        imageLabel.setIcon(new javax.swing.ImageIcon(url));
    }

    private void loadLocalImage(String mediaUrl) {
        final java.net.URL res = getClass().getResource(mediaUrl);
        if (res != null) {
            imageLabel.setIcon(new javax.swing.ImageIcon(res));
        }
        else {
            imageLabel.setText("[image not found]");
        }
    }

    /**
     * Displays correctness feedback, explanation, updated score,
     * and streak info. Stops the timer and waits for user to click Next.
     * @param feedbackMessage the feedback message to display
     * @param correctAnswer the correct answer
     * @param explanation the explanation of the answer
     * @param score the current score
     * @param currentStreak the current streak count
     * @param highestStreak the highest streak achieved
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

        typeInField.setVisible(false);
        submitButton.setVisible(false);
        nextButton.setVisible(true);

        revalidate();
        repaint();
    }

    /**
     * Displays final quiz results in a dialog, records the attempt
     * in the session history table, and returns UI to selection mode.
     * @param summaryText the summary text
     * @param score the final score
     * @param totalQuestions the total number of questions
     * @param durationSeconds the duration in seconds
     * @param highestStreak the highest streak achieved
     */
    public void showQuizEnd(String summaryText,
                            int score,
                            int totalQuestions,
                            int durationSeconds,
                            int highestStreak) {

        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        final String message = summaryText + "\n"
                + "Score: " + score + "/" + totalQuestions + "\n"
                + TIME_PREFIX + durationSeconds + " seconds\n"
                + "Best streak: " + highestStreak;
        JOptionPane.showMessageDialog(
                this,
                message,
                "Quiz Finished",
                JOptionPane.INFORMATION_MESSAGE
        );

        addHistoryEntry(score, totalQuestions);
        resetToSelection();
    }

    private void addHistoryEntry(int score, int totalQuestions) {
        attemptCounter++;
        final String quizTypeLabel = getQuizTypeLabel();
        final String modeLabel = getModeLabel();
        final String scoreLabel = score + " / " + totalQuestions;

        historyModel.addRow(new Object[]{
            attemptCounter,
            quizTypeLabel,
            modeLabel,
            scoreLabel,
        });
    }

    private String getQuizTypeLabel() {
        String result = UNKNOWN_LABEL;
        if (currentQuizType != null) {
            result = currentQuizType.getDisplayName();
        }
        return result;
    }

    private String getQuizTypeLabel(app.entities.QuizHistoryEntry entry) {
        String result = UNKNOWN_LABEL;
        if (entry.getQuizType() != null) {
            result = entry.getQuizType().getDisplayName();
        }
        return result;
    }

    private String getModeLabel() {
        String result = UNKNOWN_LABEL;
        if (currentQuestionType != null) {
            result = currentQuestionType.toString();
        }
        return result;
    }

    private String getModeLabel(app.entities.QuizHistoryEntry entry) {
        String result = UNKNOWN_LABEL;
        if (entry.getQuizType() != null) {
            result = entry.getQuestionType().toString();
        }
        return result;
    }

    /**
     * Starts a per-question countdown timer. If time reaches zero,
     * notifies controller through timeExpired().
     * @param seconds the number of seconds for the countdown
     */
    private void startTimer(int seconds) {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        remainingSeconds = seconds;
        timerLabel.setText(TIME_PREFIX + remainingSeconds + "s");

        countdownTimer = new javax.swing.Timer(1000, event -> handleTimerTick());
        countdownTimer.start();
    }

    private void handleTimerTick() {
        remainingSeconds--;
        if (remainingSeconds <= 0) {
            countdownTimer.stop();
            timerLabel.setText(TIME_PREFIX + "0s");
            if (controller != null) {
                controller.timeExpired();
            }
        }
        else {
            timerLabel.setText(TIME_PREFIX + remainingSeconds + "s");
        }
    }

    /**
     * Resets UI back to quiz selection mode. Clears prompts, options,
     * hides answer controls, and restores the selection hero layout.
     */
    private void resetToSelection() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        quizTitleLabel.setText("GeoLearn Quiz");
        promptLabel.setText("");
        progressLabel.setText("");
        timerLabel.setText(TIME_PREFIX + "--");
        timerLabel.setVisible(false);

        selectionPromptLabel.setVisible(true);

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

        final JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setPreferredSize(new Dimension(500, 200));

        final JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Quiz History",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setLayout(new BorderLayout());
        dialog.add(new JLabel("Quiz History:",
                SwingConstants.CENTER), BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);

        final JButton closeButton = new JButton("Close");
        closeButton.addActionListener(event -> dialog.dispose());

        final JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Displays quiz history from the database in a dialog.
     * Called by the presenter when history is loaded.
     *
     * @param historyEntries the list of quiz history entries from the database
     */
    public void showQuizHistory(java.util.List<app.entities.QuizHistoryEntry> historyEntries) {
        historyModel.setRowCount(0);

        for (app.entities.QuizHistoryEntry entry : historyEntries) {
            final String quizTypeLabel = getQuizTypeLabel(entry);
            final String modeLabel = getModeLabel(entry);
            final String scoreLabel = entry.getScore() + " / " + entry.getNumQuestions();

            historyModel.addRow(new Object[]{
                quizTypeLabel,
                modeLabel,
                scoreLabel,
            });
        }

        showHistoryDialog();
    }

    /**
     * Displays the main menu hero layout with the big GeoLearn title
     * and decorative icons. Used when no quiz is running.
     */
    private void showSelectionHero() {
        quizTitleLabel.setText("GeoLearn Quiz");
        quizTitleLabel.setFont(quizTitleLabel.getFont().deriveFont(Font.BOLD, 32f));

        promptLabel.setText("");
        timerLabel.setVisible(false);

        centerPanel.removeAll();
        centerPanel.setLayout(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(quizTitleLabel, gbc);

        final JLabel topImage = makeHeroImageLabel("🌍");
        gbc.gridx = 1;
        gbc.gridy = 0;
        centerPanel.add(topImage, gbc);

        final JLabel bottomImage = makeHeroImageLabel("🏛️");
        gbc.gridx = 1;
        gbc.gridy = 2;
        centerPanel.add(bottomImage, gbc);

        final JLabel leftImage = makeHeroImageLabel("🚩");
        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(leftImage, gbc);

        final JLabel rightImage = makeHeroImageLabel("💱");
        gbc.gridx = 2;
        gbc.gridy = 1;
        centerPanel.add(rightImage, gbc);

        centerPanel.revalidate();
        centerPanel.repaint();
    }

    /**
     * Creates a large emoji label used in the hero layout.
     * @param emoji the emoji or text to display
     * @return a styled JLabel
     */
    private JLabel makeHeroImageLabel(String emoji) {
        final JLabel label = new JLabel(emoji, SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(32f));
        return label;
    }

    /**
     * Called when the quiz view becomes visible. Restores selection mode.
     * @param param optional parameter
     */
    @Override
    public void onViewOpened(String param) {
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
     * Responds to view model state changes and updates the UI accordingly.
     * This method is called whenever the presenter updates the quiz state.
     * @param oldState the previous state
     * @param newState the new state
     */
    @Override
    public void onStateChange(Object oldState, Object newState) {
        if (!(newState instanceof QuizState)) {
            return;
        }

        final QuizState state = (QuizState) newState;

        if (state.isShowQuestion()) {
            showQuestion(
                    state.getQuizTitle(),
                    state.getPrompt(),
                    state.getOptions(),
                    state.getQuestionIndex(),
                    state.getTotalQuestions(),
                    state.getMediaUrl()
            );
        }

        if (state.isShowFeedback()) {
            showAnswerFeedback(
                    state.getFeedbackMessage(),
                    state.getCorrectAnswer(),
                    state.getExplanation(),
                    state.getScore(),
                    state.getCurrentStreak(),
                    state.getHighestStreak()
            );
        }

        if (state.isShowEnd()) {
            showQuizEnd(
                    "Quiz Completed!",
                    state.getScore(),
                    state.getTotalQuestions(),
                    state.getDurationSeconds(),
                    state.getHighestStreak()
            );
        }

        if (state.isShowHistory()) {
            showQuizHistory(state.getHistoryEntries());
        }
    }

    /**
     * Apply consistent styling + hover/press effects to answer buttons.
     * @param btn the button to style
     */
    private void styleAnswerButton(JButton btn) {
        final Color normalBg = new Color(230, 230, 255);
        final Color hoverBg = new Color(210, 210, 245);
        final Color pressedBg = new Color(190, 190, 235);

        btn.setBorder(new RoundedBorder(12));
        btn.setFocusPainted(false);
        btn.setBackground(normalBg);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(250, 40));
        btn.setFont(btn.getFont().deriveFont(Font.PLAIN, 16f));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                if (btn.isEnabled()) {
                    btn.setBackground(hoverBg);
                }
            }

            @Override
            public void mouseExited(MouseEvent event) {
                if (btn.isEnabled()) {
                    btn.setBackground(normalBg);
                }
            }
        });

        btn.getModel().addChangeListener(event -> {
            final ButtonModel model = (ButtonModel) event.getSource();
            if (!btn.isEnabled()) {
                return;
            }

            if (model.isPressed()) {
                btn.setBackground(pressedBg);
            }
            else if (model.isRollover()) {
                btn.setBackground(hoverBg);
            }
            else {
                btn.setBackground(normalBg);
            }
        });
    }

    /**
     * Custom border implementation for rounded button appearance.
     */
    private static class RoundedBorder implements javax.swing.border.Border {
        private final int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component component) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component component, Graphics graphics, int xCoord, int yCoord,
                                int width, int height) {
            graphics.drawRoundRect(xCoord, yCoord, width - 1, height - 1, radius, radius);
        }
    }
}
