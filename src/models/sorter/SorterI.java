package models.sorter;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface SorterI {

    boolean hasNextStep();

    void nextStep();

    /**
     * @return map of highlight type code -> info
     * */
    @Nullable
    default Map<Integer, String> highlightInfo() {
        return null;
    }
}
