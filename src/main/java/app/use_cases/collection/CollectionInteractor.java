package app.use_cases.collection;

import app.entities.Country;
import app.entities.CountryCollection;
import app.use_cases.country.CountryDataAccessInterface;

import java.util.*;
import java.util.Optional;

public class CollectionInteractor implements CollectionInputBoundary {
    private CollectionDataAccessInterface userDataAccessObject;
    private CollectionOutputBoundary collectionPresenter;
    private CountryDataAccessInterface countryDataAccess;

    public CollectionInteractor(
            CollectionDataAccessInterface userDataAccessObject,
            CollectionOutputBoundary collectionPresenter,
            CountryDataAccessInterface countryDataAccess
    ) {
        this.userDataAccessObject = userDataAccessObject;
        this.collectionPresenter = collectionPresenter;
        this.countryDataAccess = countryDataAccess;
    }

    @Override
    public void addCollection(AddCollectionRequestData collectionInputData) {
        // Validate collection name
        String collectionName = collectionInputData.getCollectionName().trim();
        if (collectionName.isEmpty()) {
            collectionPresenter.prepareErrorView("Collection name cannot be empty.");
            return;
        }

        // Validate country names list
        List<String> countryNames = collectionInputData.getCountryNames();
        if (countryNames == null || countryNames.isEmpty()) {
            collectionPresenter.prepareErrorView("Please add at least one country to the collection.");
            return;
        }

        // Remove empty strings and trim
        List<String> cleanedNames = new ArrayList<>();
        for (String name : countryNames) {
            String trimmed = name.trim();
            if (!trimmed.isEmpty()) {
                cleanedNames.add(trimmed);
            }
        }

        if (cleanedNames.isEmpty()) {
            collectionPresenter.prepareErrorView("Please add at least one valid country name.");
            return;
        }

        // Check for duplicate country names in input
        Set<String> seenNames = new LinkedHashSet<>();
        List<String> duplicates = new ArrayList<>();
        for (String name : cleanedNames) {
            if (!seenNames.add(name)) {
                duplicates.add(name);
            }
        }

        if (!duplicates.isEmpty()) {
            collectionPresenter.prepareErrorView("Duplicate countries in input: " + String.join(", ", duplicates));
            return;
        }

        // Load all countries from the data access layer and create a name-to-country map
        List<Country> allCountries = countryDataAccess.getCountries();
        Map<String, Country> countriesByName = new LinkedHashMap<>();

        for (Country country : allCountries) {
            String name = country.getName();
            if (name != null && !name.isEmpty()) {
                countriesByName.put(name, country);
            }
        }

        // Resolve country names to Country objects
        List<Country> resolvedCountries = new ArrayList<>();
        List<String> notFound = new ArrayList<>();

        for (String name : cleanedNames) {
            Country match = countriesByName.get(name);
            if (match == null) {
                notFound.add(name);
            } else {
                resolvedCountries.add(match);
            }
        }

        // Report any countries that weren't found
        if (!notFound.isEmpty()) {
            collectionPresenter.prepareErrorView("Could not find countries: " + String.join(", ", notFound));
            return;
        }

        // Create and save the collection
        CountryCollection newCollection = new CountryCollection(
                UUID.randomUUID(),
                collectionName,
                resolvedCountries
        );
        userDataAccessObject.createCollection(newCollection);
        List<CountryCollection> collections = userDataAccessObject.getAllCollections();
        collectionPresenter.prepareCollectionsView(collections);
    }

    @Override
    public void fetchAllCollections() {
        List<CountryCollection> collections = userDataAccessObject.getAllCollections();
        collectionPresenter.prepareCollectionsView(collections);
    }

    @Override
    public void deleteCollection(DeleteCollectionRequestData deleteCollectionRequestData) {
        UUID collectionId = deleteCollectionRequestData.getCollectionId();
        
        // Check if collection exists
        Optional<CountryCollection> collectionOpt = userDataAccessObject.getCollectionById(collectionId);
        if (collectionOpt.isEmpty()) {
            collectionPresenter.prepareErrorView("Collection not found.");
            return;
        }

        // Delete the collection
        userDataAccessObject.deleteCollection(collectionId);
        
        // Refresh collections view
        List<CountryCollection> collections = userDataAccessObject.getAllCollections();
        collectionPresenter.prepareCollectionsView(collections);
    }

    @Override
    public void renameCollection(RenameCollectionRequestData renameCollectionRequestData) {
        UUID collectionId = renameCollectionRequestData.getCollectionId();
        String newName = renameCollectionRequestData.getNewName().trim();

        // Validate new name
        if (newName.isEmpty()) {
            collectionPresenter.prepareErrorView("Collection name cannot be empty.");
            return;
        }

        // Check if collection exists
        Optional<CountryCollection> collectionOpt = userDataAccessObject.getCollectionById(collectionId);
        if (collectionOpt.isEmpty()) {
            collectionPresenter.prepareErrorView("Collection not found.");
            return;
        }

        CountryCollection existingCollection = collectionOpt.get();
        
        // Create updated collection with new name
        CountryCollection updatedCollection = new CountryCollection(
                existingCollection.getCollectionId(),
                newName,
                existingCollection.getCountries()
        );
        
        userDataAccessObject.updateCollection(updatedCollection);
        
        // Refresh collections view
        List<CountryCollection> collections = userDataAccessObject.getAllCollections();
        collectionPresenter.prepareCollectionsView(collections);
    }

    @Override
    public void editCollection(EditCollectionRequestData editCollectionRequestData) {
        UUID collectionId = editCollectionRequestData.getCollectionId();
        
        // Check if collection exists
        Optional<CountryCollection> collectionOpt = userDataAccessObject.getCollectionById(collectionId);
        if (collectionOpt.isEmpty()) {
            collectionPresenter.prepareErrorView("Collection not found.");
            return;
        }

        CountryCollection existingCollection = collectionOpt.get();
        List<Country> updatedCountries = new ArrayList<>(existingCollection.getCountries());

        // Handle country removals
        List<String> countryNamesToRemove = editCollectionRequestData.getCountryNamesToRemove();
        if (countryNamesToRemove != null && !countryNamesToRemove.isEmpty()) {
            for (String countryName : countryNamesToRemove) {
                updatedCountries.removeIf(c -> c.getName().equals(countryName.trim()));
            }
        }

        // Handle country additions
        List<String> countryNamesToAdd = editCollectionRequestData.getCountryNamesToAdd();
        if (countryNamesToAdd != null && !countryNamesToAdd.isEmpty()) {
            // Load all countries and create name-to-country map
            List<Country> allCountries = countryDataAccess.getCountries();
            Map<String, Country> countriesByName = new LinkedHashMap<>();

            for (Country country : allCountries) {
                String name = country.getName();
                if (name != null && !name.isEmpty()) {
                    countriesByName.put(name, country);
                }
            }

            // Resolve country names and add them
            List<String> cleanedNames = new ArrayList<>();
            for (String name : countryNamesToAdd) {
                String trimmed = name.trim();
                if (!trimmed.isEmpty()) {
                    cleanedNames.add(trimmed);
                }
            }

            // Check for duplicates in the collection
            Set<String> existingCountryNames = new HashSet<>();
            for (Country country : updatedCountries) {
                existingCountryNames.add(country.getName());
            }

            List<String> notFound = new ArrayList<>();
            List<String> duplicates = new ArrayList<>();

            for (String name : cleanedNames) {
                if (existingCountryNames.contains(name)) {
                    duplicates.add(name);
                    continue;
                }
                
                Country match = countriesByName.get(name);
                if (match == null) {
                    notFound.add(name);
                } else {
                    updatedCountries.add(match);
                    existingCountryNames.add(name);
                }
            }

            if (!duplicates.isEmpty()) {
                collectionPresenter.prepareErrorView("These countries are already in the collection: " + String.join(", ", duplicates));
                return;
            }

            if (!notFound.isEmpty()) {
                collectionPresenter.prepareErrorView("Could not find countries: " + String.join(", ", notFound));
                return;
            }
        }

        // Create updated collection
        CountryCollection updatedCollection = new CountryCollection(
                existingCollection.getCollectionId(),
                existingCollection.getCollectionName(),
                updatedCountries
        );
        
        userDataAccessObject.updateCollection(updatedCollection);
        
        // Refresh collections view
        List<CountryCollection> collections = userDataAccessObject.getAllCollections();
        collectionPresenter.prepareCollectionsView(collections);
    }
}
