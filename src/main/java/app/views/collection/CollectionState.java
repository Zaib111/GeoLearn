package app.views.collection;

import app.entities.CountryCollection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionState {
    private List<CountryCollection> allCollections = new ArrayList<>();
}
