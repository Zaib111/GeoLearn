package app.use_cases.collection;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeleteCollectionRequestData {
    private final UUID collectionId;
}

