package adapters.Collection;

import entity.Country;

import java.util.List;

public class CollectionState {
    private String collectionName;
    private String collectionError;
    private List<Country> countriesToAdd;

    public String getCollectionName() {
        return collectionName;
    }

    public String getCollectionError() {
        return collectionError;
    }

    public List<Country> getCountriesToAdd() {
        return countriesToAdd;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public void setCollectionError(String collectionError) {
        this.collectionError = collectionError;
    }

    public void setCountriesToAdd(List<Country> countriesToAdd) {
        this.countriesToAdd = countriesToAdd;
    }

}
