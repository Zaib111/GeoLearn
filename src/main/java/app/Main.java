package app;

import app.controllers.CollectionController;
import app.controllers.CompareController;
import app.controllers.DetailController;
import app.controllers.ExploreMapController;
import app.controllers.FilterCountriesController;
import app.controllers.SettingsController;
import app.controllers.TakeQuizController;
import app.data_access.APICountryDataAccessObject;
import app.data_access.ExploreMapDataAccessObject;
import app.data_access.UserDataFireStoreDataAccessObject;
import app.data_access.UserDataInMemoryDataAccessObject;
import app.presenters.CollectionPresenter;
import app.presenters.ComparePresenter;
import app.presenters.DetailPresenter;
import app.presenters.ExploreMapPresenter;
import app.presenters.FilterCountriesPresenter;
import app.presenters.SettingsPresenter;
import app.presenters.TakeQuizPresenter;
import app.use_cases.country_collection.CollectionDataAccessInterface;
import app.use_cases.country_collection.CollectionInteractor;
import app.use_cases.compare.CompareInteractor;
import app.use_cases.compare.CompareViewModel;
import app.use_cases.detail.DetailDataAccessInterface;
import app.use_cases.detail.DetailInteractor;
import app.use_cases.explore_map.ExploreMapInteractor;
import app.use_cases.filter_country.FilterCountriesInteractor;
import app.use_cases.quiz.LocalQuestionRepository;
import app.use_cases.quiz.QuestionRepository;
import app.use_cases.quiz.QuizHistoryDataAccessInterface;
import app.use_cases.quiz.QuizViewModel;
import app.use_cases.quiz.TakeQuizInteractor;
import app.use_cases.quiz.TakeQuizOutputBoundary;
import app.use_cases.settings.SettingsDataAccessInterface;
import app.use_cases.settings.SettingsInteractor;
import app.views.ViewModel;
import app.views.country_collection.CollectionState;
import app.views.country_collection.CollectionView;
import app.views.compare.CompareView;
import app.views.detail.DetailState;
import app.views.detail.DetailView;
import app.views.explore_map.ExploreMapState;
import app.views.explore_map.ExploreMapView;
import app.views.filter_countries.FilterCountriesState;
import app.views.filter_countries.FilterCountriesView;
import app.views.home.HomeView;
import app.views.quiz.QuizView;
import app.views.settings.SettingsState;
import app.views.settings.SettingsView;

/**
 * Main entry point for the GeoLearn application.
 */
public class Main {
    /**
     * Initializes and starts the GeoLearn application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        final MasterFrame masterFrame = new MasterFrame("GeoLearn");
        final Navigator navigator = new Navigator(masterFrame);
        final APICountryDataAccessObject countryDataApi =
                new APICountryDataAccessObject();
        // Call getCountries to load cache at startup
        countryDataApi.getCountries();
        final UserDataFireStoreDataAccessObject inMemoryUserDataStorage =
                new UserDataFireStoreDataAccessObject();

        setupHomeModule(masterFrame, navigator);
        setupCompareModule(masterFrame, navigator, countryDataApi);
        setupCollectionModule(masterFrame, inMemoryUserDataStorage,
                countryDataApi, navigator);
        setupSettingsModule(masterFrame, inMemoryUserDataStorage);
        setupFilterCountriesModule(masterFrame, countryDataApi, navigator);
        setupExploreMapModule(masterFrame, navigator);
        setupDetailModule(masterFrame, navigator);
        setupQuizModule(masterFrame, countryDataApi, inMemoryUserDataStorage);

        navigator.navigateTo("home");
    }

    private static void setupHomeModule(MasterFrame masterFrame,
                                         Navigator navigator) {
        final HomeView homeView = new HomeView(navigator);
        masterFrame.registerView(homeView, "home");
    }

    private static void setupCompareModule(MasterFrame masterFrame,
                                            Navigator navigator,
                                            APICountryDataAccessObject countryDataApi) {
        final CompareViewModel compareViewModel = new CompareViewModel();
        final ComparePresenter comparePresenter =
                new ComparePresenter(compareViewModel);
        final CompareInteractor compareInteractor =
                new CompareInteractor(countryDataApi, comparePresenter);
        final CompareController compareController =
                new CompareController(compareInteractor);
        final CompareView compareView =
                new CompareView(compareViewModel, compareController, navigator);
        masterFrame.registerView(compareView, "compare_countries");
    }

    private static void setupCollectionModule(
            MasterFrame masterFrame,
            CollectionDataAccessInterface inMemoryUserDataStorage,
            APICountryDataAccessObject countryDataApi,
            Navigator navigator) {
        final ViewModel<CollectionState> collectionViewModel =
                new ViewModel<>(new CollectionState());
        final CollectionPresenter collectionPresenter =
                new CollectionPresenter(collectionViewModel);
        final CollectionInteractor collectionInteractor =
                new CollectionInteractor(inMemoryUserDataStorage,
                        collectionPresenter, countryDataApi);
        final CollectionController collectionController =
                new CollectionController(collectionInteractor);
        final CollectionView collectionView =
                new CollectionView(collectionViewModel, collectionController, navigator);
        masterFrame.registerView(collectionView, "collection");
    }

    private static void setupSettingsModule(
            MasterFrame masterFrame,
            SettingsDataAccessInterface inMemoryUserDataStorage) {
        final ViewModel<SettingsState> settingsViewModel =
                new ViewModel<>(new SettingsState());
        final SettingsPresenter settingsPresenter =
                new SettingsPresenter(settingsViewModel);
        final SettingsInteractor settingsInteractor =
                new SettingsInteractor(settingsPresenter,
                        inMemoryUserDataStorage);
        final SettingsController settingsController =
                new SettingsController(settingsInteractor);
        final SettingsView settingsView =
                new SettingsView(settingsViewModel, settingsController);
        masterFrame.registerView(settingsView, "settings");
    }

    private static void setupFilterCountriesModule(
            MasterFrame masterFrame,
            APICountryDataAccessObject countryDataApi,
            Navigator navigator) {
        final ViewModel<FilterCountriesState> filterCountriesViewModel =
                new ViewModel<>(new FilterCountriesState());
        final FilterCountriesPresenter filterCountriesPresenter =
                new FilterCountriesPresenter(filterCountriesViewModel);
        final FilterCountriesInteractor filterCountriesInteractor =
                new FilterCountriesInteractor(countryDataApi,
                        filterCountriesPresenter);
        final FilterCountriesController filterCountriesController =
                new FilterCountriesController(filterCountriesInteractor);
        final FilterCountriesView filterCountriesView =
                new FilterCountriesView(filterCountriesViewModel,
                        filterCountriesController, navigator);
        masterFrame.registerView(filterCountriesView, "filter_countries");
    }

    private static void setupExploreMapModule(MasterFrame masterFrame, Navigator navigator) {
        final ViewModel<ExploreMapState> exploreMapViewModel =
                new ViewModel<>(new ExploreMapState());
        final ExploreMapPresenter exploreMapPresenter =
                new ExploreMapPresenter(exploreMapViewModel);
        final ExploreMapDataAccessObject exploreMapDataAccess =
                new ExploreMapDataAccessObject();
        final ExploreMapInteractor exploreMapInteractor =
                new ExploreMapInteractor(exploreMapDataAccess,
                        exploreMapPresenter);
        final ExploreMapController exploreMapController =
                new ExploreMapController(exploreMapInteractor);
        final ExploreMapView exploreMapView =
                new ExploreMapView(exploreMapViewModel, navigator);
        exploreMapView.setController(exploreMapController);
        masterFrame.registerView(exploreMapView, "explore_map");
    }

    private static void setupDetailModule(MasterFrame masterFrame, Navigator navigator) {
        final ViewModel<DetailState> detailViewModel =
                new ViewModel<>(new DetailState());
        final DetailPresenter detailPresenter =
                new DetailPresenter(detailViewModel);
        final DetailDataAccessInterface dataAccessInterface =
                new APICountryDataAccessObject();
        final DetailInteractor detailInteractor =
                new DetailInteractor(dataAccessInterface, detailPresenter);
        final DetailController detailController =
                new DetailController(detailInteractor);
        final DetailView detailView =
                new DetailView(detailViewModel, detailController, navigator);
        masterFrame.registerView(detailView, "country_details");
    }

    private static void setupQuizModule(
            MasterFrame masterFrame,
            APICountryDataAccessObject countryDataApi,
            QuizHistoryDataAccessInterface userDataStorage) {
        // ViewModel for the quiz screen
        final QuizViewModel quizViewModel = new QuizViewModel();

        // Swing view for the quiz screen
        final QuizView quizView = new QuizView(quizViewModel);

        // Presenter (use case → view)
        final TakeQuizOutputBoundary quizPresenter =
                new TakeQuizPresenter(quizView);

        // Question repository (manual questions for now)
        final QuestionRepository questionRepository =
                new LocalQuestionRepository(countryDataApi);

        // Interactor (quiz business logic)
        final TakeQuizInteractor takeQuizInteractor =
                new TakeQuizInteractor(questionRepository, userDataStorage, quizPresenter);

        // Controller (view → use case)
        final TakeQuizController takeQuizController =
                new TakeQuizController(takeQuizInteractor);

        // Hook controller into the view
        quizView.setController(takeQuizController);

        // Register the quiz view so HomeView can navigate to it
        masterFrame.registerView(quizView, "quiz");
    }
}
