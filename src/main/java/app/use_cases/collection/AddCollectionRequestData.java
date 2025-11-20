package app.use_cases.collection;

import app.entities.Country;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AddCollectionRequestData {
    private final String collectionName;
    private final List<Country> countriesToAdd;
}
