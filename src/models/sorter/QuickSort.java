package models.sorter;

import models.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class QuickSort implements SorterI {

    @Nullable
    private static Map<Integer, String> sHighlightInfo;

    @NotNull
    public static Map<Integer, String> getHighlightInfo() {
        if (sHighlightInfo == null) {
            final Map<Integer, String> map = new LinkedHashMap<>();
            map.put(0, "Pivot");
            map.put(1, "Test value");
            map.put(2, "Sorted pivot");

            sHighlightInfo = map;
        }

        return sHighlightInfo;
    }



    @NotNull
    private final Data data;
    @NotNull
    private final int[] stack;
    private int stackTop;

    // todo reset in every new partition to low
    private int pivotIndexSorted = -1;
    private int pivotJ = -1;

    public QuickSort(@NotNull Data data) {
        this.data = data;

        final int n = data.size();
        stack = new int[n];

        if (n > 1) {
            stack[0] = 0;           // low
            stack[1] = n - 1;       // high (inclusive)
            stackTop = 1;
        } else {
            stackTop = -1;
        }
    }

    @Override
    public boolean hasNextStep() {
        return stackTop > 0 && pivotJ < stack[stackTop];
    }

    @Override
    public void nextStep() {
        data.clearAllHighlights();

        int high = stack[stackTop], low = stack[stackTop - 1];
        int pivotIndex = high, pivot = data.valueAt(pivotIndex);

        if (pivotIndexSorted == -1) {
            pivotIndexSorted = low;
        }

        if (pivotJ == -1) {
            pivotJ = low;
        }

        data.addHighlight(pivotIndex, 0);
        data.addHighlight(pivotJ, 1);
        if (pivotIndexSorted != pivotIndex) {
            data.addHighlight(pivotIndexSorted, 2);
        }

        if (data.isComparatorInverted()? data.valueAt(pivotJ) >= pivot: data.valueAt(pivotJ) <= pivot) {
            if (pivotIndexSorted != pivotJ) {
                data.swap(pivotIndexSorted, pivotJ);
            }

            pivotIndexSorted++;
        }

        pivotJ++;
        if (pivotJ >= high) {
            if (pivotIndexSorted != pivotIndex) {
                data.swap(pivotIndexSorted, pivotIndex);
            }

            // stack
            stackTop -= 2;
            // If there are elements on left side of pivot,
            // then push left side to stack
            if (pivotIndexSorted - 1 > low) {
                stack[++stackTop] = low;
                stack[++stackTop] = pivotIndexSorted - 1;
            }

            // If there are elements on right side of pivot,
            // then push right side to stack
            if (pivotIndexSorted + 1 < high) {
                stack[++stackTop] = pivotIndexSorted + 1;
                stack[++stackTop] = high;
            }

            pivotJ = pivotIndexSorted = -1;
        }
    }

    @Override
    public Map<Integer, String> highlightInfo() {
        return getHighlightInfo();
    }

    //    static int partition(int arr[], int low, int high)
//    {
//        int pivot = arr[high];
//
//        // index of smaller element
//        int i = (low - 1);
//        for (int j = low; j <= high - 1; j++) {
//            // If current element is smaller than or
//            // equal to pivot
//            if (arr[j] <= pivot) {
//                i++;
//
//                // swap arr[i] and arr[j]
//                int temp = arr[i];
//                arr[i] = arr[j];
//                arr[j] = temp;
//            }
//        }
//
//        // swap arr[i+1] and arr[high] (or pivot)
//        int temp = arr[i + 1];
//        arr[i + 1] = arr[high];
//        arr[high] = temp;
//
//        return i + 1;
//    }
//
//    /* A[] --> Array to be sorted,
//   l  --> Starting index,
//   h  --> Ending index */
//    static void quickSortIterative(int arr[], int l, int h)
//    {
//        // Create an auxiliary stack
//        int[] stack = new int[h - l + 1];
//
//        // initialize top of stack
//        int top = -1;
//
//        // push initial values of l and h to stack
//        stack[++top] = l;
//        stack[++top] = h;
//
//        // Keep popping from stack while is not empty
//        while (top >= 0) {
//            // Pop h and l
//            h = stack[top--];
//            l = stack[top--];
//
//            // Set pivot element at its correct position
//            // in sorted array
//            int p = partition(arr, l, h);
//
//            // If there are elements on left side of pivot,
//            // then push left side to stack
//            if (p - 1 > l) {
//                stack[++top] = l;
//                stack[++top] = p - 1;
//            }
//
//            // If there are elements on right side of pivot,
//            // then push right side to stack
//            if (p + 1 < h) {
//                stack[++top] = p + 1;
//                stack[++top] = h;
//            }
//        }
//    }

}
