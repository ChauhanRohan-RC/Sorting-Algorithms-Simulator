package gl;

import models.AbsMain;
import models.Item;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;

public class ItemGl {

    @NotNull
    public final Item item;

    public ItemGl(@NotNull Item item) {
        this.item = item;
    }

    public void draw(@NotNull AbsMain p, int i) {
        p.pushMatrix();
        p.pushStyle();

        final float w = p.getItemWidth();
        float h = (item.getValue() - p.getData().minValue()) * p.getItemUnitHeight();
        final float cr = w * GLConfig.ITEM_CORNER_WEIGHT, stroke = w * GLConfig.ITEM_STROKE_WEIGHT;

        h = Math.max(h, stroke * 2);
        p.translate(i * w, -h);      // top lef

        p.rectMode(PApplet.CORNER);
        p.noStroke();

        // Bg
//        p.fill(new Color(210, 210, 210, 220).getRGB());
//        p.rect(0, 0, w, h);

        p.fill(p.getFillForIndex(i).getRGB());
        p.rect(stroke, stroke, w - stroke, h - stroke, cr, cr, cr, cr);

        p.popStyle();
        p.popMatrix();
    }
}
