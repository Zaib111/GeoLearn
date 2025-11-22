package app.use_cases.collection;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class EditCollectionRequestData {
    private final UUID collectionId;
    private final List<String> countryNamesToAdd;
    private final List<String> countryNamesToRemove;
}

