package models;

import org.jetbrains.annotations.Nullable;

/**
 * All index parameters are of backing data set (original array)
 *
 * Highlight type is always +ve
 * */
public interface HighlighterI {

    boolean containsHighlight(int index);

    @Nullable
    Integer getHighlightType(int index);

    void addHighlight(int index, int type);

    void clearHighlight(int index);

    void clearAllHighlights();

    default void addHighlightRange(int from, int to, int type) {
        for (int i = from; i < to; i++) {
            addHighlight(i, type);
        }
    }

    default void clearHighlightRange(int from, int to) {
        for (int i = from; i < to; i++) {
            clearHighlight(i);
        }
    }

}