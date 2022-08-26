package models;

import org.jetbrains.annotations.NotNull;

public class Item {

    @NotNull
    public final Object object;
    private int mValue;

    public Item(@NotNull Object object, int value) {
        this.object = object;
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        if (mValue == value)
            return;

        final int t = mValue;
        mValue = value;
        onValueChanged(t, value);
    }

    protected void onValueChanged(int oldValue, int newVal) {

    }


}
