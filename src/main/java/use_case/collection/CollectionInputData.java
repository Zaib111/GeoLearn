package use_case.collection;

import entity.Country;
import java.util.*;

public class CollectionInputData {
    private final String collectionName;
    private final List<Country> countriesToAdd;

    public CollectionInputData(String collectionName, List<Country> countriesToAdd) {
        this.collectionName = collectionName;
        this.countriesToAdd = countriesToAdd;
    }

    String getCollectionName() { return collectionName; }
    List<Country> getCountriesToAdd() { return countriesToAdd; }
}
