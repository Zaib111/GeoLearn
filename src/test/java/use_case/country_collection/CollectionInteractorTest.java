package use_case.country_collection;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;

import app.entities.Country;
import app.entities.CountryCollection;
import app.use_cases.country_collection.AddCollectionRequestData;
import app.use_cases.country_collection.CollectionDataAccessInterface;
import app.use_cases.country_collection.CollectionInteractor;
import app.use_cases.country_collection.CollectionOutputBoundary;
import app.use_cases.country_collection.CollectionOutputData;
import app.use_cases.country_collection.DeleteCollectionRequestData;
import app.use_cases.country_collection.EditCollectionRequestData;
import app.use_cases.country_collection.RenameCollectionRequestData;
import app.use_cases.country.CountryDataAccessInterface;

public class CollectionInteractorTest {

    // Helper method to create test countries
    private List<Country> createTestCountries() {
        return Arrays.asList(
            new Country("CAN", "Canada", "Ottawa", "Americas", "Northern America",
                38000000L, 9984670.0, Arrays.asList("USA"), "https://flagcdn.com/ca.svg",
                Arrays.asList("English", "French"), Arrays.asList("Canadian Dollar"),
                Arrays.asList("UTC-05:00")),
            new Country("BRA", "Brazil", "Brasília", "Americas", "South America",
                215000000L, 8515767.0, Arrays.asList(), "https://flagcdn.com/br.svg",
                Arrays.asList("Portuguese"), Arrays.asList("Brazilian Real"),
                Arrays.asList("UTC-03:00")),
            new Country("USA", "United States", "Washington, D.C.", "Americas", "Northern America",
                331000000L, 9833520.0, Arrays.asList("CAN", "MEX"), "https://flagcdn.com/us.svg",
                Arrays.asList("English"), Arrays.asList("United States Dollar"),
                Arrays.asList("UTC-05:00"))
        );
    }

    // Helper method to create a collection DAO mock
    private CollectionDataAccessInterface createCollectionDAO(List<CountryCollection> collections) {
        return new CollectionDataAccessInterface() {
            @Override
            public void createCollection(CountryCollection countryCollection) {
                collections.add(countryCollection);
            }

            @Override
            public List<CountryCollection> getAllCollections() {
                return new ArrayList<>(collections);
            }

            @Override
            public Optional<CountryCollection> getCollectionById(UUID collectionId) {
                return collections.stream()
                    .filter(c -> c.getCollectionId().equals(collectionId))
                    .findFirst();
            }

            @Override
            public void deleteCollection(UUID collectionId) {
                collections.removeIf(c -> c.getCollectionId().equals(collectionId));
            }

            @Override
            public void updateCollection(CountryCollection updatedCollection) {
                for (int i = 0; i < collections.size(); i++) {
                    if (collections.get(i).getCollectionId().equals(updatedCollection.getCollectionId())) {
                        collections.set(i, updatedCollection);
                        return;
                    }
                }
            }
        };
    }

    // ============ addCollection() Tests ============

    @Test
    public void testAddCollectionSuccess() {
        List<CountryCollection> collections = new ArrayList<>();
        final boolean[] successCalled = new boolean[1];

        CollectionDataAccessInterface collectionDAO = createCollectionDAO(collections);
        CollectionOutputBoundary collectionOB = new CollectionOutputBoundary() {
            @Override
            public void prepareCollectionsView(CollectionOutputData outputData) {
                successCalled[0] = true;
                assertEquals(1, outputData.getCollections().size());
            }

            @Override
            public void prepareErrorView(String message) {
                fail("Should not call prepareErrorView: " + message);
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(collectionDAO, collectionOB,
            new CountryDataAccessInterface() {
                @Override
                public List<Country> getCountries() {
                    return createTestCountries();
                }
            });
        interactor.addCollection(new AddCollectionRequestData("Test Collection", Arrays.asList("Canada", "Brazil")));

        assertTrue(successCalled[0]);
        assertEquals(1, collections.size());
    }

    @Test
    public void testAddCollectionEmptyName() {
        final String[] errorMessage = new String[1];
        CollectionDataAccessInterface collectionDAO = createCollectionDAO(new ArrayList<>());
        CollectionOutputBoundary collectionOB = new CollectionOutputBoundary() {
            @Override
            public void prepareCollectionsView(CollectionOutputData outputData) {
                fail("Should not call prepareCollectionsView");
            }

            @Override
            public void prepareErrorView(String message) {
                errorMessage[0] = message;
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(collectionDAO, collectionOB,
            new CountryDataAccessInterface() {
                @Override
                public List<Country> getCountries() {
                    return createTestCountries();
                }
            });
        interactor.addCollection(new AddCollectionRequestData("   ", Arrays.asList("Canada")));

        assertEquals("Collection name cannot be empty.", errorMessage[0]);
    }

    @Test
    public void testAddCollectionNullCountryNames() {
        final String[] errorMessage = new String[1];
        CollectionDataAccessInterface collectionDAO = createCollectionDAO(new ArrayList<>());
        CollectionOutputBoundary collectionOB = new CollectionOutputBoundary() {
            @Override
            public void prepareCollectionsView(CollectionOutputData outputData) {
                fail("Should not call prepareCollectionsView");
            }

            @Override
            public void prepareErrorView(String message) {
                errorMessage[0] = message;
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(collectionDAO, collectionOB,
            new CountryDataAccessInterface() {
                @Override
                public List<Country> getCountries() {
                    return createTestCountries();
                }
            });
        interactor.addCollection(new AddCollectionRequestData("Test", null));

        assertEquals("Please add at least one country to the collection.", errorMessage[0]);
    }

    @Test
    public void testAddCollectionEmptyCountryList() {
        final String[] errorMessage = new String[1];
        CollectionDataAccessInterface collectionDAO = createCollectionDAO(new ArrayList<>());
        CollectionOutputBoundary collectionOB = new CollectionOutputBoundary() {
            @Override
            public void prepareCollectionsView(CollectionOutputData outputData) {
                fail("Should not call prepareCollectionsView");
            }

            @Override
            public void prepareErrorView(String message) {
                errorMessage[0] = message;
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(collectionDAO, collectionOB,
            new CountryDataAccessInterface() {
                @Override
                public List<Country> getCountries() {
                    return createTestCountries();
                }
            });
        interactor.addCollection(new AddCollectionRequestData("Test", new ArrayList<>()));

        assertEquals("Please add at least one country to the collection.", errorMessage[0]);
    }

    @Test
    public void testAddCollectionAllEmptyNamesAfterTrim() {
        final String[] errorMessage = new String[1];
        CollectionDataAccessInterface collectionDAO = createCollectionDAO(new ArrayList<>());
        CollectionOutputBoundary collectionOB = new CollectionOutputBoundary() {
            @Override
            public void prepareCollectionsView(CollectionOutputData outputData) {
                fail("Should not call prepareCollectionsView");
            }

            @Override
            public void prepareErrorView(String message) {
                errorMessage[0] = message;
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(collectionDAO, collectionOB,
            new CountryDataAccessInterface() {
                @Override
                public List<Country> getCountries() {
                    return createTestCountries();
                }
            });
        interactor.addCollection(new AddCollectionRequestData("Test", Arrays.asList("   ", "  ", "")));

        assertEquals("Please add at least one valid country name.", errorMessage[0]);
    }

    @Test
    public void testAddCollectionDuplicateCountryNames() {
        final String[] errorMessage = new String[1];
        CollectionDataAccessInterface collectionDAO = createCollectionDAO(new ArrayList<>());
        CollectionOutputBoundary collectionOB = new CollectionOutputBoundary() {
            @Override
            public void prepareCollectionsView(CollectionOutputData outputData) {
                fail("Should not call prepareCollectionsView");
            }

            @Override
            public void prepareErrorView(String message) {
                errorMessage[0] = message;
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(collectionDAO, collectionOB,
            new CountryDataAccessInterface() {
                @Override
                public List<Country> getCountries() {
                    return createTestCountries();
                }
            });
        interactor.addCollection(new AddCollectionRequestData("Test", Arrays.asList("Canada", "Brazil", "Canada")));

        assertTrue(errorMessage[0].contains("Duplicate countries in input"));
    }

    @Test
    public void testAddCollectionCountriesNotFound() {
        final String[] errorMessage = new String[1];
        CollectionDataAccessInterface collectionDAO = createCollectionDAO(new ArrayList<>());
        CollectionOutputBoundary collectionOB = new CollectionOutputBoundary() {
            @Override
            public void prepareCollectionsView(CollectionOutputData outputData) {
                fail("Should not call prepareCollectionsView");
            }

            @Override
            public void prepareErrorView(String message) {
                errorMessage[0] = message;
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(collectionDAO, collectionOB,
            new CountryDataAccessInterface() {
                @Override
                public List<Country> getCountries() {
                    return createTestCountries();
                }
            });
        interactor.addCollection(new AddCollectionRequestData("Test", Arrays.asList("Canada", "FakeCountry")));

        assertTrue(errorMessage[0].contains("Could not find countries"));
    }

    // ============ fetchAllCollections() Tests ============

    @Test
    public void testFetchAllCollectionsSuccess() {
        List<CountryCollection> collections = new ArrayList<>();
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        collections.add(new CountryCollection(id1, "Collection 1", createTestCountries().subList(0, 1)));
        collections.add(new CountryCollection(id2, "Collection 2", createTestCountries().subList(1, 2)));

        final boolean[] successCalled = new boolean[1];
        CollectionDataAccessInterface collectionDAO = createCollectionDAO(collections);
        CollectionOutputBoundary collectionOB = new CollectionOutputBoundary() {
            @Override
            public void prepareCollectionsView(CollectionOutputData outputData) {
                successCalled[0] = true;
                assertEquals(2, outputData.getCollections().size());
            }

            @Override
            public void prepareErrorView(String message) {
                fail("Should not call prepareErrorView: " + message);
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(collectionDAO, collectionOB,
            new CountryDataAccessInterface() {
                @Override
                public List<Country> getCountries() {
                    return createTestCountries();
                }
            });
        interactor.fetchAllCollections();

        assertTrue(successCalled[0]);
    }

    // ============ deleteCollection() Tests ============

    @Test
    public void testDeleteCollectionNotFound() {
        List<CountryCollection> collections = new ArrayList<>();
        final String[] errorMessage = new String[1];
        CollectionDataAccessInterface collectionDAO = createCollectionDAO(collections);
        CollectionOutputBoundary collectionOB = new CollectionOutputBoundary() {
            @Override
            public void prepareCollectionsView(CollectionOutputData outputData) {
                // Still called even if not found
            }

            @Override
            public void prepareErrorView(String message) {
                errorMessage[0] = message;
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(collectionDAO, collectionOB,
            new CountryDataAccessInterface() {
                @Override
                public List<Country> getCountries() {
                    return createTestCountries();
                }
            });
        interactor.deleteCollection(new DeleteCollectionRequestData(UUID.randomUUID()));

        assertEquals("Collection not found.", errorMessage[0]);
    }

    @Test
    public void testDeleteCollectionSuccess() {
        List<CountryCollection> collections = new ArrayList<>();
        UUID id = UUID.randomUUID();
        collections.add(new CountryCollection(id, "Test Collection", createTestCountries().subList(0, 1)));

        final boolean[] successCalled = new boolean[1];
        CollectionDataAccessInterface collectionDAO = createCollectionDAO(collections);
        CollectionOutputBoundary collectionOB = new CollectionOutputBoundary() {
            @Override
            public void prepareCollectionsView(CollectionOutputData outputData) {
                successCalled[0] = true;
                assertEquals(0, outputData.getCollections().size());
            }

            @Override
            public void prepareErrorView(String message) {
                fail("Should not call prepareErrorView: " + message);
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(collectionDAO, collectionOB,
            new CountryDataAccessInterface() {
                @Override
                public List<Country> getCountries() {
                    return createTestCountries();
                }
            });
        interactor.deleteCollection(new DeleteCollectionRequestData(id));

        assertTrue(successCalled[0]);
        assertEquals(0, collections.size());
    }

    // ============ renameCollection() Tests ============

    @Test
    public void testRenameCollectionEmptyName() {
        final String[] errorMessage = new String[1];
        CollectionDataAccessInterface collectionDAO = createCollectionDAO(new ArrayList<>());
        CollectionOutputBoundary collectionOB = new CollectionOutputBoundary() {
            @Override
            public void prepareCollectionsView(CollectionOutputData outputData) {
                fail("Should not call prepareCollectionsView");
            }

            @Override
            public void prepareErrorView(String message) {
                errorMessage[0] = message;
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(collectionDAO, collectionOB,
            new CountryDataAccessInterface() {
                @Override
                public List<Country> getCountries() {
                    return createTestCountries();
                }
            });
        interactor.renameCollection(new RenameCollectionRequestData(UUID.randomUUID(), "   "));

        assertEquals("Collection name cannot be empty.", errorMessage[0]);
    }

    @Test
    public void testRenameCollectionNotFound() {
        List<CountryCollection> collections = new ArrayList<>();
        final String[] errorMessage = new String[1];
        CollectionDataAccessInterface collectionDAO = createCollectionDAO(collections);
        CollectionOutputBoundary collectionOB = new CollectionOutputBoundary() {
            @Override
            public void prepareCollectionsView(CollectionOutputData outputData) {
                fail("Should not call prepareCollectionsView");
            }

            @Override
            public void prepareErrorView(String message) {
                errorMessage[0] = message;
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(collectionDAO, collectionOB,
            new CountryDataAccessInterface() {
                @Override
                public List<Country> getCountries() {
                    return createTestCountries();
                }
            });
        interactor.renameCollection(new RenameCollectionRequestData(UUID.randomUUID(), "New Name"));

        assertEquals("Collection not found.", errorMessage[0]);
    }

    @Test
    public void testRenameCollectionSuccess() {
        List<CountryCollection> collections = new ArrayList<>();
        UUID id = UUID.randomUUID();
        collections.add(new CountryCollection(id, "Old Name", createTestCountries().subList(0, 1)));

        final boolean[] successCalled = new boolean[1];
        CollectionDataAccessInterface collectionDAO = createCollectionDAO(collections);
        CollectionOutputBoundary collectionOB = new CollectionOutputBoundary() {
            @Override
            public void prepareCollectionsView(CollectionOutputData outputData) {
                successCalled[0] = true;
                assertEquals("New Name", outputData.getCollections().get(0).getCollectionName());
            }

            @Override
            public void prepareErrorView(String message) {
                fail("Should not call prepareErrorView: " + message);
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(collectionDAO, collectionOB,
            new CountryDataAccessInterface() {
                @Override
                public List<Country> getCountries() {
                    return createTestCountries();
                }
            });
        interactor.renameCollection(new RenameCollectionRequestData(id, "New Name"));

        assertTrue(successCalled[0]);
        assertEquals("New Name", collections.get(0).getCollectionName());
    }

    // ============ editCollection() Tests ============

    @Test
    public void testEditCollectionNotFound() {
        List<CountryCollection> collections = new ArrayList<>();
        final String[] errorMessage = new String[1];
        CollectionDataAccessInterface collectionDAO = createCollectionDAO(collections);
        CollectionOutputBoundary collectionOB = new CollectionOutputBoundary() {
            @Override
            public void prepareCollectionsView(CollectionOutputData outputData) {
                fail("Should not call prepareCollectionsView");
            }

            @Override
            public void prepareErrorView(String message) {
                errorMessage[0] = message;
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(collectionDAO, collectionOB,
            new CountryDataAccessInterface() {
                @Override
                public List<Country> getCountries() {
                    return createTestCountries();
                }
            });
        interactor.editCollection(new EditCollectionRequestData(UUID.randomUUID(), null, null));

        assertEquals("Collection not found.", errorMessage[0]);
    }

    @Test
    public void testEditCollectionRemoveCountriesOnly() {
        List<CountryCollection> collections = new ArrayList<>();
        UUID id = UUID.randomUUID();
        collections.add(new CountryCollection(id, "Test Collection", createTestCountries()));

        final boolean[] successCalled = new boolean[1];
        CollectionDataAccessInterface collectionDAO = createCollectionDAO(collections);
        CollectionOutputBoundary collectionOB = new CollectionOutputBoundary() {
            @Override
            public void prepareCollectionsView(CollectionOutputData outputData) {
                successCalled[0] = true;
                assertEquals(2, outputData.getCollections().get(0).getCountries().size());
            }

            @Override
            public void prepareErrorView(String message) {
                fail("Should not call prepareErrorView: " + message);
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(collectionDAO, collectionOB,
            new CountryDataAccessInterface() {
                @Override
                public List<Country> getCountries() {
                    return createTestCountries();
                }
            });
        interactor.editCollection(new EditCollectionRequestData(id, null, Arrays.asList("Canada")));

        assertTrue(successCalled[0]);
        assertEquals(2, collections.get(0).getCountries().size());
    }

    @Test
    public void testEditCollectionAddCountriesOnly() {
        List<CountryCollection> collections = new ArrayList<>();
        UUID id = UUID.randomUUID();
        collections.add(new CountryCollection(id, "Test Collection", createTestCountries().subList(0, 1)));

        final boolean[] successCalled = new boolean[1];
        CollectionDataAccessInterface collectionDAO = createCollectionDAO(collections);
        CollectionOutputBoundary collectionOB = new CollectionOutputBoundary() {
            @Override
            public void prepareCollectionsView(CollectionOutputData outputData) {
                successCalled[0] = true;
                assertEquals(2, outputData.getCollections().get(0).getCountries().size());
            }

            @Override
            public void prepareErrorView(String message) {
                fail("Should not call prepareErrorView: " + message);
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(collectionDAO, collectionOB,
            new CountryDataAccessInterface() {
                @Override
                public List<Country> getCountries() {
                    return createTestCountries();
                }
            });
        interactor.editCollection(new EditCollectionRequestData(id, Arrays.asList("Brazil"), null));

        assertTrue(successCalled[0]);
        assertEquals(2, collections.get(0).getCountries().size());
    }

    @Test
    public void testEditCollectionNoAddNoRemove() {
        List<CountryCollection> collections = new ArrayList<>();
        UUID id = UUID.randomUUID();
        collections.add(new CountryCollection(id, "Test Collection", createTestCountries().subList(0, 1)));

        final boolean[] successCalled = new boolean[1];
        CollectionDataAccessInterface collectionDAO = createCollectionDAO(collections);
        CollectionOutputBoundary collectionOB = new CollectionOutputBoundary() {
            @Override
            public void prepareCollectionsView(CollectionOutputData outputData) {
                successCalled[0] = true;
            }

            @Override
            public void prepareErrorView(String message) {
                fail("Should not call prepareErrorView: " + message);
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(collectionDAO, collectionOB,
            new CountryDataAccessInterface() {
                @Override
                public List<Country> getCountries() {
                    return createTestCountries();
                }
            });
        interactor.editCollection(new EditCollectionRequestData(id, null, null));

        assertTrue(successCalled[0]);
    }

    @Test
    public void testEditCollectionBothAddAndRemove() {
        List<CountryCollection> collections = new ArrayList<>();
        UUID id = UUID.randomUUID();
        collections.add(new CountryCollection(id, "Test Collection", createTestCountries().subList(0, 1)));

        final boolean[] successCalled = new boolean[1];
        CollectionDataAccessInterface collectionDAO = createCollectionDAO(collections);
        CollectionOutputBoundary collectionOB = new CollectionOutputBoundary() {
            @Override
            public void prepareCollectionsView(CollectionOutputData outputData) {
                successCalled[0] = true;
                assertEquals(1, outputData.getCollections().get(0).getCountries().size());
                assertEquals("Brazil", outputData.getCollections().get(0).getCountries().get(0).getName());
            }

            @Override
            public void prepareErrorView(String message) {
                fail("Should not call prepareErrorView: " + message);
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(collectionDAO, collectionOB,
            new CountryDataAccessInterface() {
                @Override
                public List<Country> getCountries() {
                    return createTestCountries();
                }
            });
        interactor.editCollection(new EditCollectionRequestData(id, Arrays.asList("Brazil"), Arrays.asList("Canada")));

        assertTrue(successCalled[0]);
        assertEquals("Brazil", collections.get(0).getCountries().get(0).getName());
    }

    @Test
    public void testEditCollectionDuplicateCountries() {
        List<CountryCollection> collections = new ArrayList<>();
        UUID id = UUID.randomUUID();
        collections.add(new CountryCollection(id, "Test Collection", createTestCountries().subList(0, 1)));

        final String[] errorMessage = new String[1];
        CollectionDataAccessInterface collectionDAO = createCollectionDAO(collections);
        CollectionOutputBoundary collectionOB = new CollectionOutputBoundary() {
            @Override
            public void prepareCollectionsView(CollectionOutputData outputData) {
                fail("Should not call prepareCollectionsView");
            }

            @Override
            public void prepareErrorView(String message) {
                errorMessage[0] = message;
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(collectionDAO, collectionOB,
            new CountryDataAccessInterface() {
                @Override
                public List<Country> getCountries() {
                    return createTestCountries();
                }
            });
        interactor.editCollection(new EditCollectionRequestData(id, Arrays.asList("Canada"), null));

        assertTrue(errorMessage[0].contains("already in the collection"));
    }

    @Test
    public void testEditCollectionCountriesNotFound() {
        List<CountryCollection> collections = new ArrayList<>();
        UUID id = UUID.randomUUID();
        collections.add(new CountryCollection(id, "Test Collection", createTestCountries().subList(0, 1)));

        final String[] errorMessage = new String[1];
        CollectionDataAccessInterface collectionDAO = createCollectionDAO(collections);
        CollectionOutputBoundary collectionOB = new CollectionOutputBoundary() {
            @Override
            public void prepareCollectionsView(CollectionOutputData outputData) {
                fail("Should not call prepareCollectionsView");
            }

            @Override
            public void prepareErrorView(String message) {
                errorMessage[0] = message;
            }
        };

        CollectionInteractor interactor = new CollectionInteractor(collectionDAO, collectionOB,
            new CountryDataAccessInterface() {
                @Override
                public List<Country> getCountries() {
                    return createTestCountries();
                }
            });
        interactor.editCollection(new EditCollectionRequestData(id, Arrays.asList("FakeCountry"), null));

        assertTrue(errorMessage[0].contains("Could not find countries"));
    }

}
