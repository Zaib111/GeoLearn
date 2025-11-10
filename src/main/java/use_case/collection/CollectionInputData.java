package use_case.collection;

import entity.Country;
import java.util.*;

public class CollectionInputData {
    private String collectionName;
    private List<Country> countriesToAdd;

    public CollectionInputData(String collectionName, List<Country> countriesToAdd) {
        this.collectionName = collectionName;
        this.countriesToAdd = countriesToAdd;
    }

    String getCollectionName() { return collectionName; }
    List<Country> getCountriesToAdd() { return countriesToAdd; }
}
