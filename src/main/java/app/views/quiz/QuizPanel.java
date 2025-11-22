package app.views.quiz;

import app.controllers.TakeQuizController;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class QuizPanel extends JPanel implements QuizView {

    private static final int QUESTION_TIME_LIMIT_SECONDS = 30;

    private TakeQuizController controller;

    private final JLabel timerLabel = new JLabel("", SwingConstants.CENTER);
    private javax.swing.Timer countdownTimer;
    private int remainingSeconds;

    private final JLabel quizTitleLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel progressLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel promptLabel = new JLabel("", SwingConstants.CENTER);

    private final JPanel optionsPanel = new JPanel();   // where MCQ buttons go
    private final JLabel imageLabel = new JLabel();     // for flags later

    private final JTextField typeInField = new JTextField(20);
    private final JButton submitButton = new JButton("Submit");
    private final JButton nextButton = new JButton("Next");

    public QuizPanel() {
        setLayout(new BorderLayout());

        // Top of panel (containing title, progress, and timer)
        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        topPanel.add(quizTitleLabel);
        topPanel.add(progressLabel);
        topPanel.add(timerLabel);
        add(topPanel, BorderLayout.NORTH);

        timerLabel.setText("Time: --");

        // Centre of panel (containing the question and the options)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(promptLabel, BorderLayout.NORTH);

        optionsPanel.setLayout(new GridLayout(0, 1));
        centerPanel.add(optionsPanel, BorderLayout.CENTER);

        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(imageLabel, BorderLayout.EAST);

        add(centerPanel, BorderLayout.CENTER);

        // South of panel (type-in box, submit and next)
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(typeInField);
        bottomPanel.add(submitButton);
        bottomPanel.add(nextButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // initial visibility
        typeInField.setVisible(false);
        submitButton.setVisible(false);
        nextButton.setVisible(false);
        imageLabel.setVisible(false);

        // Listeners
        submitButton.addActionListener(e -> {
            if (controller != null) {
                controller.submitAnswer(typeInField.getText());
            }
        });

        nextButton.addActionListener(e -> {
            if (controller != null) {
                controller.nextQuestion();
            }
        });
    }

    @Override
    public void setController(TakeQuizController controller) {
        this.controller = controller;
    }

    @Override
    public void showQuestion(String quizTitle,
                             String prompt,
                             List<String> options,
                             int questionIndex,
                             int totalQuestions,
                             String mediaUrl) {

        quizTitleLabel.setText(quizTitle == null ? "" : quizTitle);
        promptLabel.setText(prompt);
        progressLabel.setText("Question " + (questionIndex + 1) + " of " + totalQuestions);

        // Reset
        optionsPanel.removeAll();
        imageLabel.setVisible(false);
        typeInField.setText("");

        if (options.isEmpty()) {
            // TYPE-IN mode
            typeInField.setVisible(true);
            submitButton.setVisible(true);
        } else {
            // MCQ mode
            typeInField.setVisible(false);
            submitButton.setVisible(false);

            optionsPanel.removeAll();

            optionsPanel.setLayout(new GridLayout(0, 1));

            for (String opt : options) {
                JButton btn = new JButton(opt);

                // No colors, no styling — default Swing look
                btn.addActionListener(e -> {
                    if (controller != null) {
                        controller.submitAnswer(opt);
                    }
                });

                optionsPanel.add(btn);
            }
        }

        nextButton.setVisible(false);

        // Optional image part
        if (mediaUrl != null && !mediaUrl.isEmpty()) {
            imageLabel.setText("[image: " + mediaUrl + "]"); // placeholder
            imageLabel.setVisible(true);
        }

        startTimer(QUESTION_TIME_LIMIT_SECONDS);


        revalidate();
        repaint();
    }

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

    @Override
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
        optionsPanel.add(new JLabel("Streak: " + currentStreak + " (Best: " + highestStreak + ")"));

        // After feedback, hide type-in stuff and show Next
        typeInField.setVisible(false);
        submitButton.setVisible(false);
        nextButton.setVisible(true);

        revalidate();
        repaint();
    }

    @Override
    public void showQuizEnd(String summaryText,
                            int score,
                            int totalQuestions,
                            int durationSeconds,
                            int highestStreak) {

        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        removeAll();
        setLayout(new GridLayout(4, 1));

        add(new JLabel(summaryText, SwingConstants.CENTER));
        add(new JLabel("Score: " + score + "/" + totalQuestions, SwingConstants.CENTER));
        add(new JLabel("Time: " + durationSeconds + " seconds", SwingConstants.CENTER));
        add(new JLabel("Best streak: " + highestStreak, SwingConstants.CENTER));

        revalidate();
        repaint();
    }

    private void makeRounded(JButton btn) {
        btn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1, true));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
    }
}
