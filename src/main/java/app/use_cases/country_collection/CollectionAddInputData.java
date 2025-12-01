package app.use_cases.country_collection;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CollectionAddInputData {
    private final String collectionName;
    private final List<String> countryNames;
}
