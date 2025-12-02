package app.use_cases.quiz;

import app.entities.Question;
import app.entities.QuestionType;
import app.entities.QuizType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TakeQuizInteractor.
 * Uses fake implementations of QuestionRepository, QuizHistoryDataAccessInterface,
 * and TakeQuizOutputBoundary so we can test the interactor in isolation.
 */
public class TakeQuizInteractorTest {

    private FakeQuestionRepository fakeRepo;
    private FakePresenter fakePresenter;
    private FakeQuizHistoryDataAccess fakeHistoryData;
    private TakeQuizInteractor interactor;

    @BeforeEach
    void setUp() {
        fakeRepo = new FakeQuestionRepository();
        fakeHistoryData = new FakeQuizHistoryDataAccess();
        fakePresenter = new FakePresenter();

        interactor = new TakeQuizInteractor(
                fakeRepo,
                fakeHistoryData,
                fakePresenter
        );
    }

    @Test
    void testStartQuizShowsFirstQuestion() {
        // Arrange
        TakeQuizStartRequestModel request = new TakeQuizStartRequestModel(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                2
        );

        // Act
        interactor.startQuiz(request);

        // Assert
        assertNotNull(fakePresenter.lastStartResponse,
                "Presenter should receive a start response");
        TakeQuizStartResponseModel r = fakePresenter.lastStartResponse;

        assertEquals("Capitals Quiz", r.getQuizTitle());
        assertEquals("What is the capital of France?", r.getPrompt());
        assertEquals(0, r.getQuestionIndex());
        assertEquals(2, r.getTotalQuestions());
    }

    @Test
    void testSubmitAnswerCorrectUpdatesScoreAndStreak() {
        // Arrange: start quiz first
        interactor.startQuiz(new TakeQuizStartRequestModel(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                2
        ));

        // Act
        interactor.submitAnswer(new SubmitAnswerRequestModel("Paris"));

        // Assert
        assertNotNull(fakePresenter.lastFeedbackResponse);
        AnswerFeedbackResponseModel r = fakePresenter.lastFeedbackResponse;

        assertEquals("Correct!", r.getFeedbackMessage());
        assertEquals(1, r.getScore());
        assertEquals(1, r.getCurrentStreak());
        assertEquals(1, r.getHighestStreak());
    }

    @Test
    void testSubmitAnswerIncorrectResetsStreak() {
        // Arrange: start quiz and answer first correctly
        interactor.startQuiz(new TakeQuizStartRequestModel(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                2
        ));
        interactor.submitAnswer(new SubmitAnswerRequestModel("Paris"));

        // Act: move to second question and answer wrong
        interactor.nextQuestion();
        interactor.submitAnswer(new SubmitAnswerRequestModel("Wrong Answer"));

        // Assert
        assertNotNull(fakePresenter.lastFeedbackResponse);
        AnswerFeedbackResponseModel r = fakePresenter.lastFeedbackResponse;

        assertEquals("Incorrect.", r.getFeedbackMessage());
        assertEquals(1, r.getScore(), "Score should remain 1 after wrong answer");
        assertEquals(0, r.getCurrentStreak(), "Current streak should reset to 0");
        assertEquals(1, r.getHighestStreak(), "Highest streak so far is still 1");
    }

    @Test
    void testNextQuestionEventuallyEndsQuiz() {
        // Arrange
        interactor.startQuiz(new TakeQuizStartRequestModel(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                2
        ));

        // First question
        interactor.submitAnswer(new SubmitAnswerRequestModel("Paris"));
        interactor.nextQuestion();

        // After moving to second question, presenter should get a question response
        assertNotNull(fakePresenter.lastQuestionResponse);
        assertNull(fakePresenter.lastEndResponse, "Quiz should not be finished yet");

        // Second question
        interactor.submitAnswer(new SubmitAnswerRequestModel("Berlin"));
        interactor.nextQuestion();

        // Now quiz should be finished
        assertNotNull(fakePresenter.lastEndResponse, "presentQuizEnd should be called");
        TakeQuizEndResponseModel end = fakePresenter.lastEndResponse;

        assertEquals(2, end.getTotalQuestions());
        assertEquals(2, end.getScore(), "Both answers were correct in this test");
        assertTrue(end.getDurationSeconds() >= 0, "Duration is non-negative");
    }

    @Test
    void testTimeExpiredTreatsAnswerAsIncorrect() {
        // Arrange
        interactor.startQuiz(new TakeQuizStartRequestModel(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                1
        ));

        // Act
        interactor.timeExpired();

        // Assert
        assertNotNull(fakePresenter.lastFeedbackResponse);
        AnswerFeedbackResponseModel r = fakePresenter.lastFeedbackResponse;

        assertEquals("Time's up!", r.getFeedbackMessage());
        assertEquals(0, r.getScore());
        assertEquals(0, r.getCurrentStreak());
    }

    @Test
    void testSubmitAnswerWithNoQuizDoesNothing() {
        interactor.submitAnswer(new SubmitAnswerRequestModel("Paris"));

        assertNull(fakePresenter.lastFeedbackResponse);
        assertNull(fakePresenter.lastEndResponse);
    }

    @Test
    void testSubmitAnswerAfterQuizFinishedDoesNothing() {
        interactor.startQuiz(new TakeQuizStartRequestModel(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                1
        ));

        interactor.submitAnswer(new SubmitAnswerRequestModel("Paris"));
        AnswerFeedbackResponseModel before =
                fakePresenter.lastFeedbackResponse;

        interactor.nextQuestion();
        assertNotNull(fakePresenter.lastEndResponse);

        interactor.submitAnswer(new SubmitAnswerRequestModel("Paris"));

        assertSame(before, fakePresenter.lastFeedbackResponse);
    }

    @Test
    void testNextQuestionWithNoQuizDoesNothing() {
        interactor.nextQuestion();

        assertNull(fakePresenter.lastQuestionResponse);
        assertNull(fakePresenter.lastEndResponse);
    }

    @Test
    void testTimeExpiredWithNoQuizOrFinishedDoesNothing() {
        // Case 1: no quiz started
        interactor.timeExpired();
        assertNull(fakePresenter.lastFeedbackResponse);

        // Case 2: quiz already finished
        interactor.startQuiz(new TakeQuizStartRequestModel(
                QuizType.CAPITALS,
                QuestionType.TYPE_IN,
                1
        ));
        interactor.submitAnswer(new SubmitAnswerRequestModel("Paris"));
        interactor.nextQuestion(); // ends the quiz
        assertNotNull(fakePresenter.lastEndResponse);

        fakePresenter.lastFeedbackResponse = null;

        interactor.timeExpired();

        assertNull(fakePresenter.lastFeedbackResponse);
    }

    @Test
    void testLoadQuizHistoryCallsPresenter() {
        interactor.loadQuizHistory();

        assertNotNull(fakePresenter.lastHistoryResponse,
                "Presenter should receive a history response");
    }

    // ===== Helper fakes =====

    /**
     * Fake repository that always returns the same 2 simple questions,
     * ignoring the quiz type / question type / limit parameters.
     */
    private static class FakeQuestionRepository implements QuestionRepository {
        @Override
        public List<Question> getQuestionsForQuiz(QuizType quizType,
                                                  QuestionType questionType,
                                                  int limit) {
            Question q1 = new Question(
                    QuizType.CAPITALS,
                    QuestionType.TYPE_IN,
                    "What is the capital of France?",
                    List.of(),
                    "Paris",
                    List.of("paris"),
                    "Paris is the capital of France.",
                    null
            );

            Question q2 = new Question(
                    QuizType.CAPITALS,
                    QuestionType.TYPE_IN,
                    "What is the capital of Germany?",
                    List.of(),
                    "Berlin",
                    List.of("berlin"),
                    "Berlin is the capital of Germany.",
                    null
            );

            if (limit == 1) {
                return List.of(q1);
            } else {
                return List.of(q1, q2);
            }
        }
    }

    /**
     * Fake presenter that just stores the last response passed to each method.
     */
    private static class FakePresenter implements TakeQuizOutputBoundary {
        TakeQuizStartResponseModel lastStartResponse;
        TakeQuizQuestionResponseModel lastQuestionResponse;
        AnswerFeedbackResponseModel lastFeedbackResponse;
        TakeQuizEndResponseModel lastEndResponse;
        QuizHistoryResponseModel lastHistoryResponse;

        @Override
        public void prepareQuizStart(TakeQuizStartResponseModel responseModel) {
            this.lastStartResponse = responseModel;
        }

        @Override
        public void presentQuestion(TakeQuizQuestionResponseModel responseModel) {
            this.lastQuestionResponse = responseModel;
        }

        @Override
        public void presentAnswerFeedback(AnswerFeedbackResponseModel responseModel) {
            this.lastFeedbackResponse = responseModel;
        }

        @Override
        public void presentQuizEnd(TakeQuizEndResponseModel responseModel) {
            this.lastEndResponse = responseModel;
        }

        @Override
        public void presentQuizHistory(QuizHistoryResponseModel responseModel) {
            this.lastHistoryResponse = responseModel;
        }
    }

    /**
     * Fake in-memory implementation of the quiz history gateway.
     */
    private static class FakeQuizHistoryDataAccess
            implements QuizHistoryDataAccessInterface {

        private final List<app.entities.QuizHistoryEntry> savedEntries =
                new ArrayList<>();

        @Override
        public void saveQuizAttempt(app.entities.QuizHistoryEntry entry) {
            savedEntries.add(entry);
        }

        @Override
        public List<app.entities.QuizHistoryEntry> getAllQuizAttempts() {
            return new ArrayList<>(savedEntries);
        }
    }
}
