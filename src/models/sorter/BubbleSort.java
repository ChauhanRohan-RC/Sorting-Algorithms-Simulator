package models.sorter;

import models.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class BubbleSort implements SorterI {

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
    int i, j;

    public BubbleSort(@NotNull Data data) {
        this.data = data;
    }

    @Override
    public boolean hasNextStep() {
        return i < data.size() - 1 && j < (data.size() - 1 - i);
    }

    @Override
    public void nextStep() {
        data.clearAllHighlights();

        final int curValue = data.valueAt(j), nextValue = data.valueAt(j + 1);

        if (data.isComparatorInverted()? curValue < nextValue: curValue > nextValue) {
            data.swap(j, j + 1);

//            data.addHighlight(j, 0);
//            data.addHighlight(j + 1, 1);
        }

        data.addHighlight(j, 0);
        data.addHighlight(j + 1, 1);
        j++;
        if (j == data.size() - 1 - i) {
            j = 0;
            i++;
        }
    }




    @Override
    public Map<Integer, String> highlightInfo() {
        return getHighlightInfo();
    }
}
