package app.use_cases.detail;

public interface DetailOutputBoundary {

    void prepareDetailFailureView(String errorMessage);
    void prepareDetailSuccessView(DetailOutputData detailOutputData);
}
