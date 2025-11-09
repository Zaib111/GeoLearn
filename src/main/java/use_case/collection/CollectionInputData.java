package use_case.collection;

public class CollectionInputData {
    private String collectionCreateOrDelete;
    private String countryToAddRemove;

    public CollectionInputData(String collectionCreateOrDelete, String countryToAddRemove) {
        this.collectionCreateOrDelete = collectionCreateOrDelete;
        this.countryToAddRemove = countryToAddRemove;
    }

    String getCollectionCreateOrDelete() { return collectionCreateOrDelete; }
    String getCountryToAddRemove() { return countryToAddRemove; }
}
