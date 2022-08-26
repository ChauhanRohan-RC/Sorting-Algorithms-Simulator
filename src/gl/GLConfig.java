package gl;

import Util.Util;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;

import java.awt.*;

public class GLConfig {

    public static final boolean DEFAULT_FULLSCREEN = false;
    public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
//    public static final Dimension SCREEN_SIZE = new Dimension(1920, 1080);

    @NotNull
    public static Dimension windowSize(int displayW, int displayH) {
        return new Dimension(Math.round(displayW / 1.6f), Math.round(displayH / 1.6f));
    }

    @NotNull
    public static Rectangle windowBoundsCenterScreen(int width, int height) {
        return new Rectangle((SCREEN_SIZE.width - width) / 2, (SCREEN_SIZE.height - height) / 2, width, height);
    }




    public static final float MAIN_FRAME_PADDING_FACTOR_TOP = 0.2f;
    public static final float MAIN_FRAME_PADDING_FACTOR_BOTTOM = 0.040f;
    public static final float MAIN_FRAME_PADDING_FACTOR_LEFT = 0.03f;
    public static final float MAIN_FRAME_PADDING_FACTOR_RIGHT = 0.03f;

    public static final float HUD_PADDING_FACTOR_TOP = 0.010f;
    public static final float HUD_PADDING_FACTOR_LEFT = 0.010f;
    public static final float HUD_PADDING_FACTOR_RIGHT = 0.010f;

    public static final float TEXT_SIZE_ALGO_NAME = 0.024f;
    public static final float TEXT_SIZE_HIGHLIGHT_INFO = 0.018f;
    public static final float LINE_SPACING_HIGHLIGHT_INFO = 0.008f;
    public static final float TEXT_SIZE_STATUS = 0.022f;

    public static final float ITEM_CORNER_WEIGHT = 0.16f;
    public static final float ITEM_STROKE_WEIGHT = 0.06f;

    public static final float FPS_MIN = 5;
    public static final float FPS_MAX = 240;
    public static final float FPS_DEFAULT = 30;

    public static final float FPS_IN_PERCENT = (FPS_MAX - FPS_MIN) / 100;       // fps in 1%
    public static final float PERCENT_IN_FPS = 1 / FPS_IN_PERCENT;       // % in 1 fps

    public static final float FPS_STEP = 5 * FPS_IN_PERCENT;

    // Controls Des
    public static final boolean SHOW_CONTROLS_DES = true;
    public static final boolean DEFAULT_CONTROLS_SHOWN = true;      // only when SHOW_CONTROLS_DES = true
    public static final float TEXT_SIZE_CONTROLS_DES_TITLE = 0.024f;
    public static final float TEXT_SIZE_CONTROLS_DES = 0.022f;

    public static float constraintFps(float fps) {
        return Util.constrain(fps, FPS_MIN, FPS_MAX);
    }

    public static float percentToFps(float percent) {
        return FPS_MIN + (Util.constrainPercent(percent) * FPS_IN_PERCENT);
    }

    public static float fpsToPercent(float fps) {
        return (constraintFps(fps) - FPS_MIN) * PERCENT_IN_FPS;
    }




    /* ................................... THEME ........................................... */

    private static boolean sLightTheme = false;

    public static boolean isLightTheme() {
        return sLightTheme;
    }

    public static void setLightTheme(boolean lightTheme) {
        if (sLightTheme == lightTheme)
            return;

        sLightTheme = lightTheme;
        onThemeChanged();
    }

    public static void toggleLightTheme() {
        sLightTheme = !sLightTheme;
        onThemeChanged();
    }


    private static void onThemeChanged() {

    }



    /* Light Theme */

    private static final Color LIGHT__BG = new Color(255, 255, 255);
    private static final Color LIGHT__FG_DARK = new Color(5, 5, 5);
    private static final Color LIGHT__FG_MEDIUM = new Color(30, 30, 30);
    private static final Color LIGHT__FG_LIGHT = new Color(50, 50, 50);
    private static final Color LIGHT__ACCENT = new Color(34, 166, 255, 255);
    private static final Color LIGHT__ACCENT_HIGHLIGHT = new Color(161, 11, 255, 255);

    private static final Color[] LIGHT__FG_COLORS = {
            new Color(128, 128, 128),
            new Color(160, 160, 160),
            new Color(190, 190, 190)
    };

    private static final Color[] LIGHT__HIGHLIGHT_COLORS = {
            new Color(66, 253, 26, 255),
            new Color(255, 67, 43, 255),
            new Color(42, 126, 255, 255),
            new Color(220, 20, 255, 255)
    };

    /* Dark Theme */

    private static final Color DARK__BG = new Color(7, 7, 7);
    private static final Color DARK__FG_DARK = new Color(255, 255, 255);
    private static final Color DARK__FG_MEDIUM = new Color(235, 235, 235);
    private static final Color DARK__FG_LIGHT = new Color(210, 210, 210);
    private static final Color DARK__ACCENT = new Color(99, 188, 255, 255);
    private static final Color DARK__ACCENT_HIGHLIGHT = new Color(197, 97, 255, 255);

    private static final Color[] DARK__FG_COLORS = {
            new Color(65, 65, 65),
            new Color(95, 95, 95),
            new Color(127, 127, 127),
    };

    private static final Color[] DARK__HIGHLIGHT_COLORS = {
            new Color(131, 255, 103, 255),
            new Color(255, 118, 99, 255),
            new Color(92, 156, 255, 255),
            new Color(234, 100, 255, 255)
    };


    @NotNull
    public static Color bg() {
        return isLightTheme()? LIGHT__BG: DARK__BG;
    }

    @NotNull
    public static Color fgDark() {
        return isLightTheme()? LIGHT__FG_DARK: DARK__FG_DARK;
    }

    @NotNull
    public static Color fgMedium() {
        return isLightTheme()? LIGHT__FG_MEDIUM: DARK__FG_MEDIUM;
    }

    @NotNull
    public static Color fgLight() {
        return isLightTheme()? LIGHT__FG_LIGHT: DARK__FG_LIGHT;
    }

    @NotNull
    public static Color accentColor() {
        return isLightTheme()? LIGHT__ACCENT: DARK__ACCENT;
    }

    @NotNull
    public static Color oppositeAccentColor() {
        return isLightTheme()? DARK__ACCENT: LIGHT__ACCENT;
    }


    @NotNull
    public static Color accentHighlightColor() {
        return isLightTheme()? LIGHT__ACCENT_HIGHLIGHT: DARK__ACCENT_HIGHLIGHT;
    }


    @NotNull
    public static Color fgColor(int i) {
//        int _i = i % (FG_SHADES.length * 2);
//        if (_i >= FG_SHADES.length) {
//            _i = (FG_SHADES.length * 2) - _i - 1;
//        }

        final Color[] arr = isLightTheme()? LIGHT__FG_COLORS: DARK__FG_COLORS;
        return arr[i % arr.length];
    }


    @NotNull
    public static Color highlightColor(int typeCode) {
        final Color[] arr = isLightTheme()? LIGHT__HIGHLIGHT_COLORS: DARK__HIGHLIGHT_COLORS;
        return arr[typeCode % arr.length];
    }

    public static Color grey(int grey) {
        return new Color(grey, grey, grey);
    }



    public static float getTextSize(float width, float height, float size) {
        return Math.min(width, height) * size;
    }

    public static float getTextSize(@NotNull PApplet app, float size) {
        return getTextSize(app.width, app.height, size);
    }

}
