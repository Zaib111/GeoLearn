package use_case.detail;

public interface DetailOutputBoundary {

    void prepareFailureView(String errorMessage);
    void prepareSuccessView(DetailOutputData detailOutputData);
}
