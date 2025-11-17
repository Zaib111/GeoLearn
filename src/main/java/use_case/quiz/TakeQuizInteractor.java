package use_case.quiz;

import entity.Quiz;
import entity.Question;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class TakeQuizInteractor implements TakeQuizInputBoundary{
    private final QuestionRepository questionRepo;
    private final TakeQuizOutputBoundary presenter;

    private Quiz currentQuiz;

    public TakeQuizInteractor(QuestionRepository questionRepo,
                             TakeQuizOutputBoundary presenter) {
        this.questionRepo = questionRepo;
        this.presenter = presenter;
    }

    @Override
    public void startQuiz(TakeQuizStartRequestModel requestModel) {
        List<Question> questions = questionRepo.getQuestionsForQuiz(
                requestModel.getQuizType(),
                requestModel.getQuestionType(),
                requestModel.getNumberOfQuestions()
        );
        currentQuiz = new Quiz(requestModel.getQuizType(),questions);

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

    private List<String> shuffledOptions(Question q) {
        List<String> shuffled = new ArrayList<>(q.getOptions());
        Collections.shuffle(shuffled);
        return shuffled;
    }
}
