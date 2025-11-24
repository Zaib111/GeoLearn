package app.controllers;

import app.use_cases.detail.DetailInputBoundary;
import app.use_cases.detail.DetailInputData;

public class DetailController {

    private final DetailInputBoundary interactor;

    public DetailController(DetailInputBoundary interactor) { this.interactor = interactor; }

    public void loadDetails(DetailInputData detailInputData) { interactor.execute(detailInputData); }
}
