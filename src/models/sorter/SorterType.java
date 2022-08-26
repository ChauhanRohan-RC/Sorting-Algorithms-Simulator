package models.sorter;

import models.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.KeyEvent;
import java.util.StringJoiner;

public enum SorterType {

    BUBBLE("Bubble Sort", KeyEvent.VK_B, "B"),
    INSERTION("Insertion Sort", KeyEvent.VK_I, "I"),
    SELECTION("Selection Sort", KeyEvent.VK_S, "S"),
    MERGE("Merge Sort", KeyEvent.VK_M, "M"),
    QUICK("Quick Sort", KeyEvent.VK_Q, "Q"),
    ;

    @NotNull
    public final String displayName;
    public final int keyCode;
    @NotNull
    public final String keyChar;

    SorterType(@NotNull String displayName, int keyCode, @NotNull String keyChar) {
        this.displayName = displayName;
        this.keyCode = keyCode;
        this.keyChar = keyChar;
    }

    @Override
    public String toString() {
        return displayName;
    }


    @NotNull
    public SorterI createSorter(@NotNull Data data) {
        return switch (this) {
            case BUBBLE -> new BubbleSort(data);
            case INSERTION -> new InsertionSort(data);
            case SELECTION -> new SelectionSort(data);
            case MERGE -> new MergeSort(data);
            case QUICK -> new QuickSort(data);
        };
    }


    @Nullable
    private static SorterType[] sSharedValues;

    public static SorterType[] getSharedValues() {
        if (sSharedValues == null) {
            sSharedValues = values();
        }

        return sSharedValues;
    }

    @Nullable
    public static SorterType fromKeyCode(int keyCode) {
        for (SorterType s: getSharedValues()) {
            if (s.keyCode == keyCode)
                return s;
        }

        return null;
    }

    @Nullable
    public static SorterType fromKeyChar(@NotNull String _char, boolean caseSensitive) {
        for (SorterType s: getSharedValues()) {
            if (caseSensitive? s.keyChar.equals(_char): s.keyChar.toLowerCase().equals(_char.toLowerCase()))
                return s;
        }

        return null;
    }

    @NotNull
    public static String createKeyInstructions(@NotNull String keyDelimiter, @NotNull String algoDelimiter) {
        final StringJoiner str = new StringJoiner(algoDelimiter);

        for (SorterType s: getSharedValues()) {
            str.add(s.keyChar + keyDelimiter + s.displayName);
        }

        return str.toString();
    }
}

