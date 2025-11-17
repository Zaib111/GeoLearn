package adapters.Collection;
import entity.Country;
import use_case.collection.CollectionInputBoundary;
import use_case.collection.CollectionInputData;

import java.util.List;


public class CollectionController {
    private final CollectionInputBoundary collectionUseCaseInteractor;

    public CollectionController(CollectionInputBoundary collectionUseCaseInteractor) {
        this.collectionUseCaseInteractor = collectionUseCaseInteractor;
    }

    /**
     * Executes the Collection Use Case.
     * @param countryName the new password
     * @param countriesToAdd the user whose password to change
     */
    public void execute(String countryName, List<Country> countriesToAdd) {
        final CollectionInputData collectionInputData = new CollectionInputData(countryName, countriesToAdd);

        collectionUseCaseInteractor.execute(collectionInputData);
    }

}
