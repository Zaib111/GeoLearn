package app.views.collection;

import java.util.ArrayList;
import java.util.List;

import app.entities.CountryCollection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionState {
    private List<CountryCollection> allCollections = new ArrayList<>();
    private String errorMessage;
}
