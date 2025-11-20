package app.use_cases.detail;

public interface DetailOutputBoundary {

    void prepareFailureView(String errorMessage);
    void prepareSuccessView(DetailOutputData detailOutputData);
}
