package app.use_cases.country_collection;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CollectionDeleteInputData {
    private final UUID collectionId;
}

