package app;

import app.controllers.CollectionController;
import app.controllers.FilterCountriesController;
import app.controllers.SettingsController;
import app.data_access.APICountryDataAccessObject;
import app.data_access.UserDataInMemoryDataAccessObject;
import app.presenters.CollectionPresenter;
import app.presenters.FilterCountriesPresenter;
import app.presenters.SettingsPresenter;
import app.use_cases.collection.CollectionInteractor;
import app.use_cases.filter_country.FilterCountriesInteractor;
import app.use_cases.settings.SettingsInteractor;
import app.views.ViewModel;
import app.views.collection.CollectionState;
import app.views.collection.CollectionView;
import app.views.filter_countries.FilterCountriesState;
import app.views.filter_countries.FilterCountriesView;
import app.views.home.HomeView;
import app.views.settings.SettingsState;
import app.views.settings.SettingsView;
import app.controllers.CompareController;
import app.presenters.ComparePresenter;
import app.use_cases.compare.CompareInteractor;
import app.views.compare.CompareView;
import app.use_cases.compare.CompareViewModel;


public class Main {
    public static void main(String[] args) {
        MasterFrame masterFrame = new MasterFrame("GeoLearn");
        Navigator navigator = new Navigator();
        navigator.subscribeToNavigationEvents(masterFrame);
        APICountryDataAccessObject countryDataAPI = new APICountryDataAccessObject();
        UserDataInMemoryDataAccessObject inMemoryUserDataStorage = new UserDataInMemoryDataAccessObject();

        // Setup Home Module
        HomeView homeView = new HomeView(navigator);
        masterFrame.registerView(homeView, "home");

        // Setup Compare Module
        CompareViewModel compareViewModel = new CompareViewModel();
        ComparePresenter comparePresenter = new ComparePresenter(compareViewModel);
        CompareInteractor compareInteractor = new CompareInteractor(countryDataAPI, comparePresenter);
        CompareController compareController = new CompareController(compareInteractor);
        CompareView compareView = new CompareView(compareViewModel, compareController, navigator);
        masterFrame.registerView(compareView, "compare_countries");

        // Setup Collection Module
        ViewModel<CollectionState> collectionViewModel = new ViewModel<>(new CollectionState());
        CollectionPresenter collectionPresenter = new CollectionPresenter(collectionViewModel);
        CollectionInteractor collectionInteractor = new CollectionInteractor(inMemoryUserDataStorage, collectionPresenter);
        CollectionController collectionController = new CollectionController(collectionInteractor);
        CollectionView collectionView = new CollectionView(collectionViewModel, collectionController);
        masterFrame.registerView(collectionView, "collection");

        // Setup Settings Module
        ViewModel<SettingsState> settingsViewModel = new ViewModel<>(new SettingsState());
        SettingsPresenter settingsPresenter = new SettingsPresenter(settingsViewModel);
        SettingsInteractor settingsInteractor = new SettingsInteractor(settingsPresenter, inMemoryUserDataStorage);
        SettingsController settingsController = new SettingsController(settingsInteractor);
        SettingsView settingsView = new SettingsView(settingsViewModel, settingsController);
        masterFrame.registerView(settingsView, "settings");

        // Setup Filter Country Module
        ViewModel<FilterCountriesState> filterCountriesViewModel = new ViewModel<>(new FilterCountriesState());
        FilterCountriesPresenter filterCountriesPresenter = new FilterCountriesPresenter(filterCountriesViewModel);
        FilterCountriesInteractor filterCountriesInteractor = new FilterCountriesInteractor(countryDataAPI, filterCountriesPresenter);
        FilterCountriesController filterCountriesController = new FilterCountriesController(filterCountriesInteractor);
        FilterCountriesView filterCountriesView = new FilterCountriesView(filterCountriesViewModel, filterCountriesController);
        masterFrame.registerView(filterCountriesView, "filter_countries");


        // Start the application at Home View
        masterFrame.navigateTo("home");
    }
}
