package models;

import Util.Listeners;
import Util.Pair;
import Util.Util;
import gl.ItemGl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Random;

public class Data implements HighlighterI {

    private static final int REFERENCE_VALUE = 0;

    public static final int SIZE_MIN = 4;
    public static final int SIZE_MAX = 1000;
    public static final int SIZE_STEP = 50;
    public static final int SIZE_DEFAULT = 100;

    public static int constraintSize(int size) {
        return Util.constrain(size, SIZE_MIN, SIZE_MAX);
    }

    public static int stepToNextUnit(int size) {
        size = constraintSize(size);
        return SIZE_STEP - (size % SIZE_STEP);
    }

    public static int stepToPrevUnit(int size) {
        size = constraintSize(size);
        final int del = size % SIZE_STEP;
        return -(del > 0? del: SIZE_STEP);
    }


    @NotNull
    public static final Comparator<Object> COMPARATOR_INT = (one, two) -> ((int) one) - ((int) two);

    public interface Listener {
        void onDataChanged(@NotNull Data data);             // data totally changed

        void onDataRefreshed(@NotNull Data data);           // internal change

        void onComparatorInversionChanged(boolean inverted);

        default void onSwap(@NotNull Data data, int i, int j) {
            onDataRefreshed(data);
        }
    }

    private static Pair<ItemGl, ItemGl> minMax(ItemGl[] items) {
        ItemGl min = items[0], max = min;

        ItemGl cur;
        for (int i=1; i < items.length; i++) {
            cur = items[i];

            if (cur.item.getValue() < min.item.getValue()) {
                min = cur;
            } else if (cur.item.getValue() > max.item.getValue()) {
                max = cur;
            }
        }

        return new Pair<>(min, max);
    }


    @NotNull
    private static ItemGl[] parse(@NotNull Object[] objects, @NotNull Comparator<Object> comparator) {
        final ItemGl[] items = new ItemGl[objects.length];
        if (objects.length == 0)
            return items;

        items[0] = new ItemGl(new Item(objects[0], REFERENCE_VALUE));
        Object cur;
        Item prevItem;

        for (int i=1; i < items.length; i++) {
            prevItem = items[i - 1].item;
            cur = objects[i];

            items[i] = new ItemGl(new Item(cur, prevItem.getValue() - comparator.compare(prevItem.object, cur)));
        }

        return items;
    }

    private static void recomputeValues(@NotNull ItemGl[] items, @NotNull Comparator<Object> comparator) {
        if (items.length == 0)
            return;

        items[0].item.setValue(REFERENCE_VALUE);       // reference

        Item prev;
        Item cur;
        int delta;
        for (int i=1; i < items.length; i++) {
            prev = items[i - 1].item;
            cur = items[i].item;

            delta = comparator.compare(prev.object, cur.object);
            cur.setValue(prev.getValue() - delta);
        }
    }


    @NotNull
    public static Integer[] createRandomIntArray(int size) {
        final Integer[] items = new Integer[size];
        final Random rand = new Random();

        for (int i=0; i < size; i++) {
            items[i] = rand.nextInt(size);
        }

        return items;
    }

    @NotNull
    public static Data createRandom(int size) {
        final ItemGl[] items = new ItemGl[size];
        final Random rand = new Random();

        for (int i=0; i < size; i++) {
            int val = rand.nextInt(size);
            items[i] = new ItemGl(new Item(val, val));
        }

        return new Data(items, COMPARATOR_INT);
    }


    @NotNull
    private ItemGl[] mItems;
    @NotNull
    private Comparator<Object> mComparator;
    private boolean mComparatorInverted;
    @Nullable
    private ItemGl min, max;

    private final Map<Integer, Integer> mHighlightIndices = new IdentityHashMap<>();     // Index -> Highlight type (+ve)

    @NotNull
    private final Listeners<Listener> mListeners = new Listeners<Listener>();


    private Data(@NotNull ItemGl[] items, @NotNull Comparator<Object> comparator) {
        mComparator = comparator;
        setItemsInternal(items, false);
    }

    public Data(@NotNull Object[] items, @NotNull Comparator<Object> comparator) {
        this(parse(items, comparator), comparator);
    }

    /* Objects */

    public final int size() {
        return mItems.length;
    }

    public final boolean isEmpty() {
        return size() == 0;
    }

    @Nullable
    public ItemGl min() {
        return min;
    }

    @Nullable
    public ItemGl max() {
        return max;
    }

    public final int minValue() {
        return min != null? min.item.getValue(): 0;
    }

    public final int maxValue() {
        return max != null? max.item.getValue(): 0;
    }

    public final int valueRange() {
        return maxValue() - minValue();
    }


    @NotNull
    public final ItemGl itemGlAt(int index) {
        return mItems[index];
    }

    @NotNull
    public final Item itemAt(int index) {
        return mItems[index].item;
    }

    public final int valueAt(int index) {
        return mItems[index].item.getValue();
    }

    public final void swap(int i, int j) {
        final ItemGl temp  = mItems[i];
        mItems[i] = mItems[j];
        mItems[j] = temp;

        mListeners.forEachListener(l -> l.onSwap(Data.this, i, j));
    }

    public final void setItemAr(int index, @NotNull ItemGl item) {
        mItems[index] = item;
        mListeners.forEachListener(l -> l.onDataRefreshed(Data.this));
    }


    public void setObjects(@NotNull Object[] objects, @NotNull Comparator<Object> comparator) {
        mComparator = comparator;
        setItemsInternal(parse(objects, comparator), true);
    }

    public final void setObjects(@NotNull Object[] objects) {
        setItemsInternal(parse(objects, mComparator), true);
    }

    private void setItemsInternal(@NotNull ItemGl[] items, boolean notify) {
        mItems = items;
        if (items.length > 1) {
            final Pair<ItemGl, ItemGl> min_max = minMax(items);
            min = min_max.first;
            max = min_max.sec;
        } else {
            min = max = null;
        }

        clearAllHighlights();
        if (notify) {
            onItemsChanged();
        }
    }

    protected void onItemsChanged() {
        mListeners.forEachListener(l -> l.onDataChanged(Data.this));
    }


    /* Comparator */

    @NotNull
    public final Comparator<Object> getComparator() {
        return mComparator;
    }

    public void setComparator(@NotNull Comparator<Object> comparator) {
        if (mComparator == comparator)
            return;

        final Comparator<Object> old = mComparator;
        mComparator = comparator;
        onComparatorChanged(old, comparator);
    }

    public final boolean isComparatorInverted() {
        return mComparatorInverted;
    }

    public final void setComparatorInverted(boolean comparatorInverted) {
        if (mComparatorInverted == comparatorInverted)
            return;

        mComparatorInverted = comparatorInverted;
        onComparatorInversionChanged(comparatorInverted);
    }

    protected void onComparatorChanged(@NotNull Comparator<Object> old, @NotNull Comparator<Object> _new) {
        recomputeValues(mItems, _new);
        mListeners.forEachListener(l -> l.onDataRefreshed(Data.this));
    }

    protected void onComparatorInversionChanged(boolean inverted) {
        mListeners.forEachListener(l -> l.onComparatorInversionChanged(inverted));
    }


    /* Listeners */

    public final void addListener(@NotNull Listener l) {
        mListeners.addListener(l);
    }

    public final boolean removeListener(@NotNull Listener l) {
        return mListeners.removeListener(l);
    }



    /* Highlight */

    @Override
    public boolean containsHighlight(int index) {
        return mHighlightIndices.containsKey(index);
    }

    @Override
    @Nullable
    public Integer getHighlightType(int index) {
        return mHighlightIndices.get(index);
    }

    @Override
    public void addHighlight(int index, int type) {
        mHighlightIndices.put(index, type);
    }

    @Override
    public void clearHighlight(int index) {
        mHighlightIndices.remove(index);
    }

    @Override
    public void clearAllHighlights() {
        mHighlightIndices.clear();
    }

}
