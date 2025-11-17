package adapters.Collection;

import adapters.ViewModel;

public class CollectionViewModel extends ViewModel<CollectionState> {
    public static final String TITLE_LABEL = "Create Collection View";
    public static final String COLLECTION_LABEL = "Enter Collection Name";
    public static final String COUNTRIES_LABEL = "Enter Countries to Add";

    public static final String CREATE_COLLECTION_BUTTON_LABEL = "Create Collection";

    public CollectionViewModel() {
        super("create collection");
        setState(new CollectionState());
    }
}
