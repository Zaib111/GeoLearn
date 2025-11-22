package app.use_cases.collection;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EditCollectionRequestData {
    private final UUID collectionId;
    private final List<String> countryNamesToAdd;
    private final List<String> countryNamesToRemove;
}

