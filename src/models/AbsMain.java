package models;

import gl.GLConfig;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;

import java.awt.*;

public abstract class AbsMain extends PApplet {

    @NotNull
    public abstract Data getData();

    public abstract float getItemWidth();

    public abstract float getItemUnitHeight();

    @NotNull
    public Color getFillForIndex(int index) {
        final Integer type = getData().getHighlightType(index);
        return type != null? GLConfig.highlightColor(type): GLConfig.fgColor(index);
    }

}
