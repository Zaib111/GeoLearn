package adapters.Collection;

import entity.Country;

import java.util.List;

public class CollectionState {
    private String collectionName;
    private String collectionNameError;
    private List<Country> countriesToAdd;
    private String countriesToAddError;

    public CollectionState(String collectionName, String collectionNameError,
                           List<Country> countriesToAdd, String countriesToAddError) {
        this.collectionName = collectionName;
        this.collectionNameError = collectionNameError;
        this.countriesToAdd = countriesToAdd;
        this.countriesToAddError = countriesToAddError;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public String getCollectionNameError() {
        return collectionNameError;
    }

    public List<Country> getCountriesToAdd() {
        return countriesToAdd;
    }

    public String getCountriesToAddError() {
        return countriesToAddError;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public void setCollectionNameError(String collectionNameError) {
        this.collectionNameError = collectionNameError;
    }

    public void setCountriesToAdd(List<Country> countriesToAdd) {
        this.countriesToAdd = countriesToAdd;
    }

    public void setCountriesToAddError(String countriesToAddError) {
        this.countriesToAddError = countriesToAddError;
    }

}
