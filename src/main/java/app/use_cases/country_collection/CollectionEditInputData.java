package app.use_cases.country_collection;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CollectionEditInputData {
    private final UUID collectionId;
    private final List<String> countryNamesToAdd;
    private final List<String> countryNamesToRemove;
}

