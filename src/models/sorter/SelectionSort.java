package models.sorter;

import models.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class SelectionSort implements SorterI {

    @Nullable
    private static Map<Integer, String> sHighlightInfo;

    @NotNull
    public static Map<Integer, String> getHighlightInfo() {
        if (sHighlightInfo == null) {
            final Map<Integer, String> map = new LinkedHashMap<>();
            map.put(0, "Min Value");
            map.put(1, "Test value");

            sHighlightInfo = map;
        }

        return sHighlightInfo;
    }


    @NotNull
    private final Data data;
    private int i = 0, j = 1;

    private int minIndex = i;

    public SelectionSort(@NotNull Data data) {
        this.data = data;
    }

    @Override
    public boolean hasNextStep() {
        return i < data.size() - 1 && j < data.size();
    }

    @Override
    public void nextStep() {
        data.clearAllHighlights();

        data.addHighlight(minIndex, 0);
        data.addHighlight(j, 1);
        if (data.isComparatorInverted()? data.valueAt(j) > data.valueAt(minIndex): data.valueAt(j) < data.valueAt(minIndex)) {
            minIndex = j;
        }

        j++;
        if (j >= data.size()) {
            if (minIndex != i) {
                data.swap(minIndex, i);
            }

            i++; j = i + 1; minIndex = i;
        }
    }


    @Override
    public Map<Integer, String> highlightInfo() {
        return getHighlightInfo();
    }
}
