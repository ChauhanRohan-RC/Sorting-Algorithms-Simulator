package models.sorter;

import models.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class InsertionSort implements SorterI {

    @Nullable
    private static Map<Integer, String> sHighlightInfo;

    @NotNull
    public static Map<Integer, String> getHighlightInfo() {
        if (sHighlightInfo == null) {
            final Map<Integer, String> map = new LinkedHashMap<>();
            map.put(0, "Value");
            map.put(1, "Test value");

            sHighlightInfo = map;
        }

        return sHighlightInfo;
    }


    @NotNull
    private final Data data;
    private int i = 1, j = 0;

    @Nullable
    private Integer iVal;

    public InsertionSort(@NotNull Data data) {
        this.data = data;
    }

    @Override
    public boolean hasNextStep() {
        return i < data.size() && j >= 0;
    }

    @Override
    public void nextStep() {
        data.clearAllHighlights();

        if (iVal == null) {
            iVal = data.valueAt(i);
        }

        data.addHighlight(i, 0);

        boolean swap = false;
        if (j >= 0) {
            data.addHighlight(j, 1);
            swap = data.isComparatorInverted()? data.valueAt(j) < iVal: data.valueAt(j) > iVal;
            if (swap) {
                data.swap(j, j + 1);
    //            if (j + 1 != i) {
    //                data.addHighlight(j + 1, 2);
    //            }
                j--;
            }
        }

        if (j < 0 || !swap) {
            i++; iVal = null; j = i - 1;
        }
    }


    @Override
    public Map<Integer, String> highlightInfo() {
        return getHighlightInfo();
    }
}
