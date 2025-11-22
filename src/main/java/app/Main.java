package app;

import app.controllers.CollectionController;
import app.controllers.CompareController;
import app.controllers.ExploreMapController;
import app.controllers.FilterCountriesController;
import app.controllers.SettingsController;
import app.data_access.APICountryDataAccessObject;
import app.data_access.ExploreMapDataAccessObject;
import app.data_access.UserDataInMemoryDataAccessObject;
import app.presenters.CollectionPresenter;
import app.presenters.ComparePresenter;
import app.presenters.ExploreMapPresenter;
import app.presenters.FilterCountriesPresenter;
import app.presenters.SettingsPresenter;
import app.use_cases.collection.CollectionInteractor;
import app.use_cases.compare.CompareInteractor;
import app.use_cases.compare.CompareViewModel;
import app.use_cases.explore_map.ExploreMapInteractor;
import app.use_cases.filter_country.FilterCountriesInteractor;
import app.use_cases.settings.SettingsInteractor;
import app.views.ViewModel;
import app.views.collection.CollectionState;
import app.views.collection.CollectionView;
import app.views.compare.CompareView;
import app.views.explore_map.ExploreMapState;
import app.views.explore_map.ExploreMapView;
import app.views.filter_countries.FilterCountriesState;
import app.views.filter_countries.FilterCountriesView;
import app.views.home.HomeView;
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
        final UserDataInMemoryDataAccessObject inMemoryUserDataStorage =
                new UserDataInMemoryDataAccessObject();

        setupHomeModule(masterFrame, navigator);
        setupCompareModule(masterFrame, navigator, countryDataApi);
        setupCollectionModule(masterFrame, inMemoryUserDataStorage,
                countryDataApi);
        setupSettingsModule(masterFrame, inMemoryUserDataStorage);
        setupFilterCountriesModule(masterFrame, countryDataApi);
        setupExploreMapModule(masterFrame);

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
            UserDataInMemoryDataAccessObject inMemoryUserDataStorage,
            APICountryDataAccessObject countryDataApi) {
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
                new CollectionView(collectionViewModel, collectionController);
        masterFrame.registerView(collectionView, "collection");
    }

    private static void setupSettingsModule(
            MasterFrame masterFrame,
            UserDataInMemoryDataAccessObject inMemoryUserDataStorage) {
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
            APICountryDataAccessObject countryDataApi) {
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
                        filterCountriesController);
        masterFrame.registerView(filterCountriesView, "filter_countries");
    }

    private static void setupExploreMapModule(MasterFrame masterFrame) {
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
                new ExploreMapView(exploreMapViewModel);
        exploreMapView.setController(exploreMapController);
        masterFrame.registerView(exploreMapView, "explore_map");
    }
}
