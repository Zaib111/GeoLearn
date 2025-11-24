package app.use_cases.quiz;

import app.entities.Question;
import app.entities.QuestionType;
import app.entities.Quiz;
import app.entities.QuizHistoryEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public class TakeQuizInteractor implements TakeQuizInputBoundary{
    private final QuestionRepository questionRepo;
    private final TakeQuizOutputBoundary presenter;
    private final QuizHistoryDataAccessInterface historyGateway;

    private QuestionType  currentQuestionType;

    private Quiz currentQuiz;

    /**
     * Creates a new TakeQuizInteractor.
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

        List<Question> questions = questionRepo.getQuestionsForQuiz(
                requestModel.getQuizType(),
                requestModel.getQuestionType(),
                requestModel.getNumberOfQuestions()
        );

        currentQuiz = new Quiz(requestModel.getQuizType(), questions);

        Question first = currentQuiz.getQuestions().get(0);

        TakeQuizStartResponseModel response = new TakeQuizStartResponseModel(
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
    public void submitAnswer(SubmitAnswerRequestModel requestModel){
        if (currentQuiz == null || currentQuiz.isFinished()) {
            return;
        }

        Question currentQuestion = currentQuiz.getCurrentQuestion();
        if (currentQuestion == null) {
            return;
        }

        String userAnswer = requestModel.getUserAnswer();
        boolean correct = currentQuiz.answerCurrentQuestion(userAnswer);

        String feedbackMessage = correct ? "Correct!" : "Incorrect.";

        AnswerFeedbackResponseModel response = new AnswerFeedbackResponseModel(
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
    public void nextQuestion(){
        if (currentQuiz == null) {
            return;
        }
        currentQuiz.nextQuestion();

        if (currentQuiz.isFinished()) {
            currentQuiz.finish();
            TakeQuizEndResponseModel endResponse = new TakeQuizEndResponseModel(
                    currentQuiz.getScore(),
                    currentQuiz.getTotalQuestions(),
                    currentQuiz.getDurationPlayed(),
                    currentQuiz.getHighestStreak()
            );
            QuizHistoryEntry entry = new QuizHistoryEntry(
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
        } else {
            Question q = currentQuiz.getCurrentQuestion();

            TakeQuizQuestionResponseModel response = new TakeQuizQuestionResponseModel(
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

        Question currentQuestion = currentQuiz.getCurrentQuestion();
        if (currentQuestion == null) {
            return;
        }

        currentQuiz.answerCurrentQuestion("");

        AnswerFeedbackResponseModel response = new AnswerFeedbackResponseModel(
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
     * Returns a shuffled copy of the question's options.
     */
    private List<String> shuffledOptions(Question q) {
        List<String> shuffled = new ArrayList<>(q.getOptions());
        Collections.shuffle(shuffled);
        return shuffled;
    }
}
