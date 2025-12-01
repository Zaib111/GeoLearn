package app.use_cases.country_collection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import app.entities.Country;
import app.entities.CountryCollection;
import app.use_cases.country.CountryDataAccessInterface;

public class CollectionInteractor implements CollectionInputBoundary {
    private static final String COMMA_SEPARATOR = ", ";
    private static final String ERROR_COLLECTION_NOT_FOUND = "Collection not found.";

    private final CollectionDataAccessInterface userDataAccessObject;
    private final CollectionOutputBoundary collectionPresenter;
    private final CountryDataAccessInterface countryDataAccess;

    public CollectionInteractor(
            final CollectionDataAccessInterface userDataAccessObject,
            final CollectionOutputBoundary collectionPresenter,
            final CountryDataAccessInterface countryDataAccess
    ) {
        this.userDataAccessObject = userDataAccessObject;
        this.collectionPresenter = collectionPresenter;
        this.countryDataAccess = countryDataAccess;
    }

    @Override
    public void addCollection(final CollectionAddInputData collectionInputData) {
        final String collectionName = collectionInputData.getCollectionName().trim();
        final List<String> countryNames = collectionInputData.getCountryNames();

        if (collectionName.isEmpty()) {
            collectionPresenter.prepareErrorView("Collection name cannot be empty.");
        }
        else if (countryNames == null || countryNames.isEmpty()) {
            collectionPresenter.prepareErrorView("Please add at least one country to the collection.");
        }
        else {
            final List<String> cleanedNames = new ArrayList<>();
            for (final String name : countryNames) {
                final String trimmed = name.trim();
                if (!trimmed.isEmpty()) {
                    cleanedNames.add(trimmed);
                }
            }

            if (cleanedNames.isEmpty()) {
                collectionPresenter.prepareErrorView("Please add at least one valid country name.");
            }
            else {

                final Set<String> seenNames = new LinkedHashSet<>();
                final List<String> duplicates = new ArrayList<>();
                for (final String name : cleanedNames) {
                    if (!seenNames.add(name)) {
                        duplicates.add(name);
                    }
                }

                if (!duplicates.isEmpty()) {
                    collectionPresenter.prepareErrorView("Duplicate countries in input: "
                            + String.join(COMMA_SEPARATOR, duplicates));
                }
                else {

                    final List<Country> allCountries = countryDataAccess.getCountries();
                    final Map<String, Country> countriesByName = new LinkedHashMap<>();

                    for (final Country country : allCountries) {
                        final String name = country.getName();
                        if (name != null && !name.isEmpty()) {
                            countriesByName.put(name, country);
                        }
                    }

                    final List<Country> resolvedCountries = new ArrayList<>();
                    final List<String> notFound = new ArrayList<>();

                    for (final String name : cleanedNames) {
                        final Country match = countriesByName.get(name);
                        if (match == null) {
                            notFound.add(name);
                        }
                        else {
                            resolvedCountries.add(match);
                        }
                    }

                    if (!notFound.isEmpty()) {
                        collectionPresenter.prepareErrorView("Could not find countries: "
                                + String.join(COMMA_SEPARATOR, notFound));
                    }
                    else {

                        final CountryCollection newCollection = new CountryCollection(
                                UUID.randomUUID(),
                                collectionName,
                                resolvedCountries
                        );
                        userDataAccessObject.createCollection(newCollection);
                        final List<CountryCollection> collections = userDataAccessObject.getAllCollections();
                        collectionPresenter.prepareCollectionsView(new CollectionOutputData(collections));
                    }
                }
            }
        }
    }

    @Override
    public void fetchAllCollections() {
        final List<CountryCollection> collections = userDataAccessObject.getAllCollections();
        collectionPresenter.prepareCollectionsView(new CollectionOutputData(collections));
    }

    @Override
    public void deleteCollection(final CollectionDeleteInputData collectionDeleteInputData) {
        final UUID collectionId = collectionDeleteInputData.getCollectionId();
        final Optional<CountryCollection> collectionOpt = userDataAccessObject.getCollectionById(collectionId);
        if (collectionOpt.isEmpty()) {
            collectionPresenter.prepareErrorView(ERROR_COLLECTION_NOT_FOUND);
        }
        userDataAccessObject.deleteCollection(collectionId);
        final List<CountryCollection> collections = userDataAccessObject.getAllCollections();
        collectionPresenter.prepareCollectionsView(new CollectionOutputData(collections));
    }

    @Override
    public void renameCollection(final CollectionRenameInputData collectionRenameInputData) {
        final UUID collectionId = collectionRenameInputData.getCollectionId();
        final Optional<CountryCollection> collectionOpt = userDataAccessObject.getCollectionById(collectionId);
        final String newName = collectionRenameInputData.getNewName().trim();
        if (newName.isEmpty()) {
            collectionPresenter.prepareErrorView("Collection name cannot be empty.");
        }
        else if (collectionOpt.isEmpty()) {
            collectionPresenter.prepareErrorView(ERROR_COLLECTION_NOT_FOUND);
        }
        else {
            final CountryCollection existingCollection = collectionOpt.get();
            final CountryCollection updatedCollection = new CountryCollection(
                    existingCollection.getCollectionId(),
                    newName,
                    existingCollection.getCountries()
            );
            userDataAccessObject.updateCollection(updatedCollection);
            final List<CountryCollection> collections = userDataAccessObject.getAllCollections();
            collectionPresenter.prepareCollectionsView(new CollectionOutputData(collections));
        }
    }

    @Override
    public void editCollection(final CollectionEditInputData collectionEditInputData) {
        final UUID collectionId = collectionEditInputData.getCollectionId();
        final Optional<CountryCollection> collectionOpt = userDataAccessObject.getCollectionById(collectionId);
        boolean error = false;

        if (collectionOpt.isEmpty()) {
            collectionPresenter.prepareErrorView(ERROR_COLLECTION_NOT_FOUND);
        }
        else {
            final CountryCollection existingCollection = collectionOpt.get();
            final List<Country> updatedCountries = new ArrayList<>(existingCollection.getCountries());

            final List<String> countryNamesToRemove = collectionEditInputData.getCountryNamesToRemove();
            if (countryNamesToRemove != null && !countryNamesToRemove.isEmpty()) {
                for (final String countryName : countryNamesToRemove) {
                    updatedCountries.removeIf(country -> country.getName().equals(countryName.trim()));
                }
            }

            final List<String> countryNamesToAdd = collectionEditInputData.getCountryNamesToAdd();
            if (countryNamesToAdd != null && !countryNamesToAdd.isEmpty()) {
                final List<Country> allCountries = countryDataAccess.getCountries();
                final Map<String, Country> countriesByName = new LinkedHashMap<>();

                for (final Country country : allCountries) {
                    final String name = country.getName();
                    if (name != null && !name.isEmpty()) {
                        countriesByName.put(name, country);
                    }
                }

                final List<String> cleanedNames = new ArrayList<>();
                for (final String name : countryNamesToAdd) {
                    final String trimmed = name.trim();
                    if (!trimmed.isEmpty()) {
                        cleanedNames.add(trimmed);
                    }
                }

                final Set<String> existingCountryNames = new HashSet<>();
                for (final Country country : updatedCountries) {
                    existingCountryNames.add(country.getName());
                }

                final List<String> notFound = new ArrayList<>();
                final List<String> duplicates = new ArrayList<>();

                for (final String name : cleanedNames) {
                    if (existingCountryNames.contains(name)) {
                        duplicates.add(name);
                        continue;
                    }
                    final Country match = countriesByName.get(name);
                    if (match == null) {
                        notFound.add(name);
                    }
                    else {
                        updatedCountries.add(match);
                        existingCountryNames.add(name);
                    }
                }

                if (!duplicates.isEmpty()) {
                    collectionPresenter.prepareErrorView("These countries are already in the collection: "
                            + String.join(COMMA_SEPARATOR, duplicates));
                    error = true;
                }

                if (!notFound.isEmpty()) {
                    collectionPresenter.prepareErrorView("Could not find countries: "
                            + String.join(COMMA_SEPARATOR, notFound));
                    error = true;
                }
            }

            if (!error) {
                updateCollection(existingCollection, updatedCountries);
            }
        }
    }

    private void updateCollection(CountryCollection existingCollection, List<Country> updatedCountries) {
        final CountryCollection updatedCollection = new CountryCollection(
                existingCollection.getCollectionId(),
                existingCollection.getCollectionName(),
                updatedCountries
        );
        userDataAccessObject.updateCollection(updatedCollection);
        final List<CountryCollection> collections = userDataAccessObject.getAllCollections();
        collectionPresenter.prepareCollectionsView(new CollectionOutputData(collections));
    }
}
