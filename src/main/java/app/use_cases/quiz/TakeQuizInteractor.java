package app.use_cases.quiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.entities.Question;
import app.entities.QuestionType;
import app.entities.Quiz;
import app.entities.QuizHistoryEntry;

/**
 * Interactor for the Take Quiz use case.
 *
 * <p>This class implements TakeQuizInputBoundary and the following functions:
 * <ul>
 *     <li>selecting questions from a QuestionRepository</li>
 *     <li>tracking score, streaks, and timing via the Quiz entity</li>
 *     <li>evaluating answers and preparing response models for the presenter</li>
 * </ul>
 */
public class TakeQuizInteractor implements TakeQuizInputBoundary {
    private final QuestionRepository questionRepo;
    private final TakeQuizOutputBoundary presenter;
    private final QuizHistoryDataAccessInterface historyGateway;

    private QuestionType currentQuestionType;

    private Quiz currentQuiz;

    /**
     * Constructs a TakeQuizInteractor instance which handles the process of taking a quiz,
     * including retrieving questions, managing quiz history, and presenting updates to the user.
     *
     * @param questionRepo    the repository used for retrieving questions for the quiz
     * @param historyGateway  the data access interface responsible for storing and retrieving quiz history
     * @param presenter       the output boundary used to present quiz-related updates to the user
     */
    public TakeQuizInteractor(QuestionRepository questionRepo,
                             QuizHistoryDataAccessInterface historyGateway,
                             TakeQuizOutputBoundary presenter) {
        this.questionRepo = questionRepo;
        this.historyGateway = historyGateway;
        this.presenter = presenter;
    }

    /**
     * Starts a quiz session using the parameters in the request model.
     */
    @Override
    public void startQuiz(TakeQuizStartRequestModel requestModel) {
        currentQuestionType = requestModel.getQuestionType();

        final List<Question> questions = questionRepo.getQuestionsForQuiz(
                requestModel.getQuizType(),
                requestModel.getQuestionType(),
                requestModel.getNumberOfQuestions()
        );

        currentQuiz = new Quiz(requestModel.getQuizType(), questions);

        final Question first = currentQuiz.getQuestions().get(0);

        final TakeQuizStartResponseModel response = new TakeQuizStartResponseModel(
                requestModel.getQuizType().getDisplayName(),
                first.getPrompt(),
                shuffledOptions(first),
                currentQuiz.getCurrentIndex(),
                currentQuiz.getTotalQuestions(),
                first.getMediaUrl()
        );

        presenter.prepareQuizStart(response);
    }

    /**
     * Submits the user's answer for the current question.
     */
    @Override
    public void submitAnswer(SubmitAnswerRequestModel requestModel) {
        if (currentQuiz == null || currentQuiz.isFinished()) {
            return;
        }

        final Question currentQuestion = currentQuiz.getCurrentQuestion();
        if (currentQuestion == null) {
            return;
        }

        final String userAnswer = requestModel.getUserAnswer();
        final boolean correct = currentQuiz.answerCurrentQuestion(userAnswer);

        final String feedbackMessage;
        if (correct) {
            feedbackMessage = "Correct!";
        }
        else {
            feedbackMessage = "Incorrect.";
        }

        final AnswerFeedbackResponseModel response = new AnswerFeedbackResponseModel(
                feedbackMessage,
                currentQuestion.getCorrect(),
                currentQuestion.getExplanation(),
                currentQuiz.getScore(),
                currentQuiz.getCurrentStreak(),
                currentQuiz.getHighestStreak()
        );

        presenter.presentAnswerFeedback(response);
    }

    /**
     * Advances the quiz to the next question, or ends the quiz if there are no more.
     */
    @Override
    public void nextQuestion() {
        if (currentQuiz == null) {
            return;
        }
        currentQuiz.nextQuestion();

        if (currentQuiz.isFinished()) {
            currentQuiz.finish();
            final TakeQuizEndResponseModel endResponse = new TakeQuizEndResponseModel(
                    currentQuiz.getScore(),
                    currentQuiz.getTotalQuestions(),
                    currentQuiz.getDurationPlayed(),
                    currentQuiz.getHighestStreak()
            );
            final QuizHistoryEntry entry = new QuizHistoryEntry(
                    currentQuiz.getQuizType(),
                    currentQuestionType,
                    currentQuiz.getTotalQuestions(),
                    currentQuiz.getScore(),
                    currentQuiz.getDurationPlayed(),
                    currentQuiz.getHighestStreak(),
                    java.time.LocalDateTime.now()
            );
            historyGateway.saveQuizAttempt(entry);
            presenter.presentQuizEnd(endResponse);
        }
        else {
            final Question q = currentQuiz.getCurrentQuestion();

            final TakeQuizQuestionResponseModel response = new TakeQuizQuestionResponseModel(
                    q.getPrompt(),
                    shuffledOptions(q),
                    currentQuiz.getCurrentIndex(),
                    currentQuiz.getTotalQuestions(),
                    q.getMediaUrl()
            );

            presenter.presentQuestion(response);
        }
    }

    /**
     * Handles the case when the countdown timer expires.
     */
    @Override
    public void timeExpired() {
        if (currentQuiz == null || currentQuiz.isFinished()) {
            return;
        }

        final Question currentQuestion = currentQuiz.getCurrentQuestion();
        if (currentQuestion == null) {
            return;
        }

        currentQuiz.answerCurrentQuestion("");

        final AnswerFeedbackResponseModel response = new AnswerFeedbackResponseModel(
                "Time's up!",
                currentQuestion.getCorrect(),
                currentQuestion.getExplanation(),
                currentQuiz.getScore(),
                currentQuiz.getCurrentStreak(),
                currentQuiz.getHighestStreak()
        );

        presenter.presentAnswerFeedback(response);
    }

    /**
     * Loads quiz history from the database and presents it to the view.
     */
    @Override
    public void loadQuizHistory() {
        final List<QuizHistoryEntry> historyEntries = historyGateway.getAllQuizAttempts();
        final QuizHistoryResponseModel response = new QuizHistoryResponseModel(historyEntries);
        presenter.presentQuizHistory(response);
    }

    /**
     * Shuffles the multiple-choice options of the given question and returns the shuffled list.
     *
     * @param question the Question object containing the multiple-choice options to be shuffled
     * @return a list of shuffled answer options
     */
    private List<String> shuffledOptions(Question question) {
        final List<String> shuffled = new ArrayList<>(question.getOptions());
        Collections.shuffle(shuffled);
        return shuffled;
    }
}
