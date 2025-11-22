package app.use_cases.collection;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class RenameCollectionRequestData {
    private final UUID collectionId;
    private final String newName;
}

