package app;

import app.controllers.CollectionController;
import app.controllers.ExploreMapController;
import app.controllers.SettingsController;
import app.data_access.APICountryDataAccessObject;
import app.data_access.ExploreMapDataAccessObject;
import app.data_access.UserDataInMemoryDataAccessObject;
import app.presenters.CollectionPresenter;
import app.presenters.ExploreMapPresenter;
import app.presenters.SettingsPresenter;
import app.use_cases.collection.CollectionInteractor;
import app.use_cases.explore_map.ExploreMapInteractor;
import app.use_cases.settings.SettingsInteractor;
import app.views.ViewModel;
import app.views.collection.CollectionState;
import app.views.collection.CollectionView;
import app.views.explore_map.ExploreMapState;
import app.views.explore_map.ExploreMapView;
import app.views.home.HomeView;
import app.views.settings.SettingsState;
import app.views.settings.SettingsView;

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

        // Setup Explore Map Module
        ViewModel<ExploreMapState> exploreMapViewModel = new ViewModel<>(new ExploreMapState());
        ExploreMapPresenter exploreMapPresenter = new ExploreMapPresenter(exploreMapViewModel);
        ExploreMapDataAccessObject exploreMapDataAccess = new ExploreMapDataAccessObject();
        ExploreMapInteractor exploreMapInteractor = new ExploreMapInteractor(exploreMapDataAccess, exploreMapPresenter);
        ExploreMapController exploreMapController = new ExploreMapController(exploreMapInteractor);
        ExploreMapView exploreMapView = new ExploreMapView(exploreMapViewModel);
        exploreMapView.setController(exploreMapController);
        masterFrame.registerView(exploreMapView, "explore_map");

        // Start the application at Home View
        masterFrame.navigateTo("home");
    }
}
