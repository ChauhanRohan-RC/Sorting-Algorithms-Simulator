import Util.Util;
import com.jogamp.newt.opengl.GLWindow;
import gl.GLConfig;
import models.AbsMain;
import models.Data;
import models.sorter.SorterI;
import models.sorter.SorterType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import processing.awt.PSurfaceAWT;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.opengl.PJOGL;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Scanner;

public class Main extends AbsMain implements Data.Listener {

    @NotNull
    public static final SorterType DEFAULT_SORTER = SorterType.QUICK;
    public static final boolean DEFAULT_LOCK_CONTROLS_WHILE_SORTING = true;

    private static final String DES_CONTROLS = "Space..................Play/Pause\nCtrl-[Shift]-R.......Reset [force]\nCtrl-I...................Invert Order\n+/-.......................Speed\nCtrl +/-................Data Size\nCtrl-T..................Theme";
    private static final String DES_ALGORITHMS = SorterType.createKeyInstructions(".....", "\n");
    private static final String DES_TO_DISPLAY = DES_ALGORITHMS + "\n\n" + DES_CONTROLS;
    private static final String DES_TITLE = "Controls [Ctrl-C]";



    /* Ui */
    private int _w, _h;
    private float itemWidth, itemUnitHeight;

    private final Data data;
    private boolean mPaused, mSorting;

    @NotNull
    private SorterType mSorterType = DEFAULT_SORTER;
    @Nullable
    private SorterI mSorter;

    private boolean mShowControls = GLConfig.DEFAULT_CONTROLS_SHOWN;
    private float mControlsDesWidthFactor = -1;


    private volatile float mPendingFps = -1;
    private int mFpsPrintCounter = -1;

    public Main(@NotNull Data data) {
        this.data = data;
        data.addListener(this);
    }

    public Main(int n) {
        this(Data.createRandom(n));
    }

    public Main() {
        this(Data.SIZE_DEFAULT);
    }

    @NotNull
    @Override
    public Data getData() {
        return data;
    }

    @Override
    public float getItemWidth() {
//        final float paddedW = width * (1 - GlConfig.MAIN_FRAME_PADDING_FACTOR_LEFT - GlConfig.MAIN_FRAME_PADDING_FACTOR_RIGHT);
//
//        final int n = data.size();
//        return paddedW / Math.max(1, n);

        return itemWidth;
    }

    @Override
    public float getItemUnitHeight() {
//        final float paddedH = height * (1 - GlConfig.MAIN_FRAME_PADDING_FACTOR_TOP - GlConfig.MAIN_FRAME_PADDING_FACTOR_BOTTOM);
//
//        final int range = data.valueRange();
//        return paddedH / Main.max(1, range);

        return itemUnitHeight;
    }


    /* Settings */

    protected void onResized(int w, int h) {
        computeItemDimensions();
    }

    @Override
    public void settings() {
        if (R.getConfigValueBool("fullscreen", GLConfig.DEFAULT_FULLSCREEN)) {
            fullScreen();         // does not work with P2D
        } else {
            final Dimension size = GLConfig.windowSize(displayWidth, displayHeight);
            size(size.width, size.height, P2D);
        }

        _w = width; _h = height;
        PJOGL.setIcon(R.APP_ICON.toString());       // app icon
        smooth(4);
    }


    /* Setup-draw */

    @Override
    public void setup() {
        super.setup();

        surface.setTitle(R.APP_NAME);
        surface.setResizable(true);
        frameRate(GLConfig.FPS_DEFAULT);

        computeItemDimensions();
    }


    public void preDraw() {
        if (_w != width || _h != height) {
            _w = width; _h = height;
            onResized(width, height);
        }

//        if (sketchFullScreen() && (width != GLConfig.SCREEN_SIZE.width || height != GLConfig.SCREEN_SIZE.height)) {
//            surface.setSize(GLConfig.SCREEN_SIZE.width, GLConfig.SCREEN_SIZE.height);
//        }

        final float pendingFps = mPendingFps;
        if (pendingFps > 0) {
            mPendingFps = -1;
            frameRate(pendingFps);
            mFpsPrintCounter = ((int) pendingFps) + 4;
        }

        if (mSorter != null && isSorting()) {
            if (mSorter.hasNextStep()) {
                mSorter.nextStep();
            } else {
                stopSort(true);
            }
        }
    }



    @Override
    public void draw() {
        preDraw();

        background(GLConfig.bg().getRGB());

        // Main Data
        pushMatrix();

        final float mainPadLeft = width * GLConfig.MAIN_FRAME_PADDING_FACTOR_LEFT,
                mainPadRight = width * GLConfig.MAIN_FRAME_PADDING_FACTOR_RIGHT,
                mainPadBottom = height * GLConfig.MAIN_FRAME_PADDING_FACTOR_BOTTOM;

        translate(mainPadLeft, height - mainPadBottom);

        for (int i=0; i < data.size(); i++) {
            data.itemGlAt(i).draw(this, i);
        }

        popMatrix();

        // Hud
        final float hudTopPad = height * GLConfig.HUD_PADDING_FACTOR_TOP,
                hudLeftPad = width * GLConfig.HUD_PADDING_FACTOR_LEFT,
                hudRightPad = width * GLConfig.HUD_PADDING_FACTOR_RIGHT;

        // Controls
        textAlign(LEFT, TOP);
        textSize(getTextSize(GLConfig.TEXT_SIZE_CONTROLS_DES_TITLE));
        fill(GLConfig.accentColor().getRGB());
        text(DES_TITLE, hudLeftPad * 2, hudTopPad * 2);

        if (controlsShown()) {
            pushStyle();
            noStroke();

            final float titleH = textAscent() + textDescent();

            textAlign(LEFT, TOP);
            textSize(getTextSize(GLConfig.TEXT_SIZE_CONTROLS_DES));

            final float desW;
            if (mControlsDesWidthFactor < 0) {
                desW = textWidth(DES_TO_DISPLAY);
                mControlsDesWidthFactor = desW / width;
            } else {
                desW = mControlsDesWidthFactor * width;
            }

            final float boxW = desW + hudRightPad * 3;
            final float boxc = hudTopPad;

            final Color fg = GLConfig.fgDark();
            fill(new Color(fg.getRed(), fg.getBlue(), fg.getGreen(), 210).getRGB());

            final float top = (hudTopPad * 4) + titleH;
            rect(hudLeftPad, top, boxW, height - top - mainPadBottom, boxc, boxc, boxc, boxc);

            fill(GLConfig.bg().getRGB());
            text(DES_TO_DISPLAY, hudLeftPad + (boxW - desW) / 2, top + hudTopPad);
            popStyle();
        }


        // Algorithm
        textAlign(RIGHT, TOP);
        textSize(getTextSize(GLConfig.TEXT_SIZE_ALGO_NAME));
        fill(GLConfig.accentColor().getRGB());
        text(mSorterType.displayName, width - hudRightPad, hudTopPad);

        // Highlight info
        final Map<Integer, String> hInfo;
        if (mSorter != null && (hInfo = mSorter.highlightInfo()) != null && !hInfo.isEmpty()) {
            final float algoNameTextHeight = textAscent() + textDescent();
            final float lineSpacing = height * GLConfig.LINE_SPACING_HIGHLIGHT_INFO;

            pushStyle();
            noStroke();
            textAlign(LEFT, TOP);
            textSize(getTextSize(GLConfig.TEXT_SIZE_HIGHLIGHT_INFO));
            rectMode(CORNER);

            final float textHeight = textAscent() + textDescent();
            float maxTextW = 0;

            for (String v: hInfo.values()) {
                maxTextW = Math.max(maxTextW, textWidth(v));
            }

            final float textLeft = width - maxTextW - hudRightPad;
            float textY = hudTopPad + algoNameTextHeight + lineSpacing * 1.4f;
            Color color;
            String value;

            for (Map.Entry<Integer, String> e: hInfo.entrySet()) {
                color = GLConfig.highlightColor(e.getKey());
                value = e.getValue();

                fill(color.getRGB());
                rect(textLeft - textHeight - lineSpacing, textY + (lineSpacing / 2) - 2, textHeight, textHeight, 2, 2, 2, 2);

                fill(GLConfig.fgDark().getRGB());
                text(value, textLeft, textY);
                textY += textHeight + lineSpacing;
            }

            popStyle();
        }

        // Status Bar
        pushStyle();
        textSize(getTextSize(GLConfig.TEXT_SIZE_STATUS));
        final float statusTextY = height - ((mainPadBottom + textAscent() + textDescent()) / 2);

        if (mSorting || mPaused) {
            final String sortStatusText = R.getSortingStatusText(this, mPaused);
            if (Util.notEmpty(sortStatusText)) {
                textAlign(LEFT, TOP);

                fill(GLConfig.accentColor().getRGB());
                text(sortStatusText, mainPadRight, statusTextY);
            }
        }

        final String status = R.getStatusText(data.size(), isComparatorInverted(), frameRate);
        textAlign(RIGHT, TOP);
        fill(GLConfig.fgMedium().getRGB());
        text(status, width - mainPadLeft, statusTextY);
        popStyle();

        postDraw();
    }

    public void postDraw() {
        if (!(mSorter == null || mSorter.hasNextStep())) {        // done
            data.clearAllHighlights();
        }

        if (mFpsPrintCounter > 0) {
            mFpsPrintCounter--;
        } else if (mFpsPrintCounter == 0) {
            Util.v("\n" + SHELL_SPEED, R.getSpeedText(frameRate));
            mFpsPrintCounter = -1;
        }
    }

    public float getTextSize(float size) {
        return GLConfig.getTextSize(this, size);
    }

    @Override
    public void keyPressed(KeyEvent event) {
        super.keyPressed(event);

        final int keyCode = event.getKeyCode();

        switch (keyCode) {
            case java.awt.event.KeyEvent.VK_SPACE -> toggleSorting();
            case java.awt.event.KeyEvent.VK_DEAD_CEDILLA, java.awt.event.KeyEvent.VK_PLUS -> {
                if (event.isControlDown()) {
                    changeDataSizeByUnit(true, true);
                } else {
                    changeSpeedByUnit(true);
                }
            }
            case java.awt.event.KeyEvent.VK_DEAD_OGONEK, java.awt.event.KeyEvent.VK_MINUS -> {
                if (event.isControlDown()) {
                    changeDataSizeByUnit(true, false);
                } else {
                    changeSpeedByUnit(false);
                }
            }

            default -> {
                boolean handled = false;
                if (event.isControlDown()) {
                    if (keyCode == java.awt.event.KeyEvent.VK_T) {
                        GLConfig.toggleLightTheme();
                        handled = true;
                    } else if (keyCode == java.awt.event.KeyEvent.VK_C) {
                        toggleShowControls();
                        handled = true;
                    } else if (keyCode == java.awt.event.KeyEvent.VK_I) {
                        toggleComparatorInverted();
                        handled = true;
                    } else if (keyCode == java.awt.event.KeyEvent.VK_R) {
                        reset(event.isShiftDown());
                        handled = true;
                    }
                }

                if (!handled) {
                    final SorterType sorterType = SorterType.fromKeyCode(keyCode);
                    if (sorterType != null) {
                        setSorterType(sorterType);
                    }
                }
            }
        }

    }



    private void computeItemDimensions() {
        final float paddedW = width * (1 - GLConfig.MAIN_FRAME_PADDING_FACTOR_LEFT - GLConfig.MAIN_FRAME_PADDING_FACTOR_RIGHT);
        final float paddedH = height * (1 - GLConfig.MAIN_FRAME_PADDING_FACTOR_TOP - GLConfig.MAIN_FRAME_PADDING_FACTOR_BOTTOM);

        final int n = data.size();
        itemWidth = paddedW / Math.max(1, n);

        final int range = data.valueRange();
        itemUnitHeight = paddedH / Main.max(1, range);
    }


    /* Sort */

    protected void onSortingStart() {

    }

    protected void onSortingPaused() {

    }

    protected void onSortingStopped(boolean ended) {
        data.clearAllHighlights();

        if (ended) {
            data.addHighlightRange(0, data.size(), 0);
        }
    }

    protected void onSorterTypeChanged(@NotNull SorterType old, @NotNull SorterType _new) {
    }


    public final boolean startSort() {
        if (mSorting)
            return false;

        if (mSorter == null || !mSorter.hasNextStep()) {
            mSorter = mSorterType.createSorter(data);
        }

        mSorting = true;
        mPaused = false;
        return true;
    }

    public final boolean pauseSort() {
        if (mPaused || !mSorting)
            return false;

        mSorting = false;
        mPaused = true;
        return true;
    }

    public final void toggleSorting() {
        if (isSorting()) {
            pauseSort();
        } else {
            startSort();
        }
    }


    private boolean stopSort(boolean ended) {
        if (mSorting || mPaused) {
            mSorting = false;
            mSorter = null;
            mPaused = false;

            onSortingStopped(ended);
            return true;
        }

        return false;
    }

    public final boolean stopSort() {
       return stopSort(false);
    }

    public final boolean isSorting() {
        return mSorting && !mPaused;
    }

    public final boolean isPaused() {
        return mPaused;
    }

    public final boolean areControlsLocked() {
        return DEFAULT_LOCK_CONTROLS_WHILE_SORTING && isSorting();
    }

    @NotNull
    public final SorterType getSorterType() {
        return mSorterType;
    }

    public final boolean setSorterType(@NotNull SorterType sorterType) {
        if (mSorterType == sorterType || areControlsLocked())
            return false;

        stopSort();
        final SorterType old = mSorterType;
        mSorterType = sorterType;
        mSorter = null;

        onSorterTypeChanged(old, sorterType);
        return true;
    }

    public final boolean isComparatorInverted() {
        return data.isComparatorInverted();
    }

    public final boolean setComparatorInverted(boolean inverted) {
        if (areControlsLocked())
            return false;

        data.setComparatorInverted(inverted);
        return true;
    }

    public final boolean toggleComparatorInverted() {
        return setComparatorInverted(!data.isComparatorInverted());
    }


    public final boolean reset(boolean force, int newSize) {
        if (force || !areControlsLocked()) {
            stopSort();
            data.setObjects(Data.createRandomIntArray(newSize), Data.COMPARATOR_INT);
            return true;
        }

        return false;
    }

    public final boolean reset(boolean force) {
        return reset(force, data.size());      // same size
    }

    public final void setSpeedPercent(float percent) {
        frameRate(GLConfig.percentToFps(percent));
    }

    public final float getSpeedPercent() {
        return GLConfig.fpsToPercent(frameRate);
    }

    public final float computeNewFrameRate(boolean inc) {
        return GLConfig.constraintFps(frameRate + ((inc? 1: -1) * GLConfig.FPS_STEP));
    }

    public final void changeSpeedByUnit(boolean inc) {
        frameRate(computeNewFrameRate(inc));
    }

    public final void setPendingFps(float pendingFps) {
        mPendingFps = pendingFps;
    }

    public final boolean setDataSize(boolean force, int newSize) {
        newSize = Data.constraintSize(newSize);
        if (newSize != data.size()) {
            return reset(force, newSize);
        }

        return false;
    }

    public final boolean changeDataSizeByUnit(boolean force, boolean inc) {
        final int curSize = data.size();
        final int newSize = curSize + (inc? Data.stepToNextUnit(curSize): Data.stepToPrevUnit(curSize));
        return setDataSize(force, newSize);
    }

    public void setShowControls(boolean showControls) {
        mShowControls = showControls;
    }

    public void toggleShowControls() {
        setShowControls(!mShowControls);
    }

    public boolean controlsShown() {
        return GLConfig.SHOW_CONTROLS_DES && mShowControls && !isSorting();
    }


    /* Listeners */

    @Override
    public void onDataChanged(@NotNull Data data) {
        computeItemDimensions();
    }

    @Override
    public void onDataRefreshed(@NotNull Data data) {

    }

    @Override
    public void onSwap(@NotNull Data data, int i, int j) {

    }

    @Override
    public void onComparatorInversionChanged(boolean inverted) {
        stopSort();
        data.clearAllHighlights();
    }




    /* ......................................... SHELL ...........................................*/

    public static final String DES_SHELL_ALGORITHMS = "# ALGORITHMS [key -> algorithm]\n  " + SorterType.createKeyInstructions(" -> ", "\n  ");
    public static final String DES_SHELL_COMMANDS = "# COMMANDS\n -> algo [key] -> set sorting algorithm\n -> size [+/-/count] : increase/decrease/set data size\n -> speed [+/-/percent] : increase/decrease/set sorting speed\n -> invert : Invert sorting order\n -> reset [f] : Reset [force reset]\n -> theme : toggle ui theme [LIGHT/DARK]\n -> start/resume : start/resume sorting\n -> pause : pause sorting\n ->  stop : stop sorting\n -> exit/quit : quit\n";
    public static final String SHELL_INSTRUCTIONS = "\n............................. RC SORT .. ..............................\n\n" + DES_SHELL_ALGORITHMS + "\n\n\n" + DES_SHELL_COMMANDS;

    public static final String SHELL_ROOT_NS = "cube:RC";       // Name Space

    @NotNull
    public static String shellPath(@Nullable String child) {
        return (Util.isEmpty(child)? SHELL_ROOT_NS: SHELL_ROOT_NS + "\\" + child) + ">";
    }

    public static final String SHELL_ROOT = shellPath(null);
    public static final String SHELL_DATA_SIZE = shellPath("size");
    public static final String SHELL_SPEED = shellPath("speed");
    public static final String SHELL_RESET = shellPath("reset");
    public static final String SHELL_ALGO = shellPath("algo");
    public static final String SHELL_THEME = shellPath("theme");

    private static final String DATA_RANGE_REPR = "[" + Data.SIZE_MIN + ", " + Data.SIZE_MAX + "]";
    private static final String SPEED_RANGE_REPR = "(0, 100)";

    public static boolean createReadme() {
        return R.createReadme(SHELL_INSTRUCTIONS);
    }


    public static void startMain(String[] args) {
        final Main app = new Main();
        PApplet.runSketch(concat(new String[] { app.getClass().getName() }, args), app);

        println(SHELL_INSTRUCTIONS);
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        do {
            print(SHELL_ROOT);
            final String in = sc.nextLine().toLowerCase();
            if (in.isEmpty())
                continue;

            if (in.startsWith("size")) {
                final String ops = in.substring(4).replace(" ", "");
                boolean done = false;

                if (ops.equals("+")) {
                    if (!app.changeDataSizeByUnit(true, true)) {
                        Util.e(SHELL_DATA_SIZE, "Data size is already maximum, must be in range " + DATA_RANGE_REPR);
                    } else {
                        done = true;
                    }
                } else if (ops.equals("-")) {
                    if (!app.changeDataSizeByUnit(true, false)) {
                        Util.e(SHELL_DATA_SIZE, "Data size is already minimum, must be in range " + DATA_RANGE_REPR);
                    } else {
                        done = true;
                    }
                } else {
                    try {
                        final int n = Integer.parseInt(ops);
                        if (n < Data.SIZE_MIN || n > Data.SIZE_MAX) {
                            Util.e(SHELL_DATA_SIZE, "Data size must be in range " + DATA_RANGE_REPR);
                        } else {
                            done = app.setDataSize(true, n);
                        }
                    } catch (NumberFormatException ignored) {
                        Util.e(SHELL_DATA_SIZE, "Data size must be an integer in range " + DATA_RANGE_REPR);
                    }
                }

                if (done) {
                    Util.v(SHELL_DATA_SIZE, R.getDataSizeText(app.data.size()));
                }
            } else if (in.startsWith("speed")) {
                final String ops = in.substring(5).replace(" ", "");
//                boolean done = false;

                if (ops.equals("+")) {
                    app.setPendingFps(app.computeNewFrameRate(true));
//                    app.changeSpeedByUnit(true);
//                    done = true;
                } else if (ops.equals("-")) {
                    app.setPendingFps(app.computeNewFrameRate(false));
//                    app.changeSpeedByUnit(false);
//                    done = true;
                } else {
                    try {
                        final int percent = Integer.parseInt(ops);
                        if (percent < 0 || percent > 100) {
                            Util.e(SHELL_SPEED, "Speed must be in range " + SPEED_RANGE_REPR);
                        } else {
//                            app.setSpeedPercent(percent);
//                            done = true;
                            app.setPendingFps(GLConfig.percentToFps(percent));
                        }
                    } catch (NumberFormatException ignored) {
                        Util.e(SHELL_SPEED, "Speed must be in percentage, in range " + SPEED_RANGE_REPR);
                    }
                }

//                if (done) {
//                    Util.v(SHELL_SPEED, R.getSpeedText(app.frameRate));
//                }
            } else if (in.startsWith("reset")) {
                final String ops = in.substring(5).replace(" ", "");
                final boolean force = ops.equals("f");
                if (app.reset(force)) {
                    Util.v(SHELL_RESET, "Reset successful, force: " + force);
                } else {
                    Util.e(SHELL_RESET, "Reset failed! Pause sorting first...");
                }
            } else if (in.startsWith("algo")){
                final String ops = in.substring(4).replace(" ", "");
                final SorterType st = SorterType.fromKeyChar(ops, false);

                if (st != null) {
                    if (app.setSorterType(st)) {
                        Util.v(SHELL_ALGO, "Sorting algorithm changed to " + st.displayName);
                    } else {
                        Util.e(SHELL_ALGO, st == app.getSorterType()? st.displayName + "is already in use": "Failed tp set " + st.displayName + " algorithm. Pause the ongoing sorting or reset");
                    }
                } else {
                    Util.e(SHELL_ALGO, "Invalid algorithm [" + ops + "]. Must be one of\n" + DES_SHELL_ALGORITHMS);
                }
            } else if (in.equals("theme")) {
                GLConfig.toggleLightTheme();
                Util.v(SHELL_THEME, "Switched to " + (GLConfig.isLightTheme()? "LIGHT": "DARK") + " mode");
            }else if (in.equals("invert")) {
                if (app.toggleComparatorInverted()) {
                    Util.v(SHELL_ALGO, "Sorting order: " + (app.isComparatorInverted()? "INVERTED": "ORIGINAL"));
                } else {
                    Util.e(SHELL_ALGO, "Failed to invert sorting order. Pause sorting first...");
                }
            } else if (in.equals("start") || in.equals("resume")) {
                if (app.startSort()) {
                    Util.v(SHELL_ALGO, "Sorting started");
                } else {
                    Util.e(SHELL_ALGO, "Already sorting...");
                }
            } else if (in.equals("pause")) {
                if (app.pauseSort()) {
                    Util.v(SHELL_ALGO, "Sorting paused");
                } else {
                    Util.e(SHELL_ALGO, "Not sorting yet!");
                }
            } else if (in.equals("stop")) {
                if (app.stopSort()) {
                    Util.v(SHELL_ALGO, "Sorting stopped");
                } else {
                    Util.e(SHELL_ALGO, "Not sorting yet!");
                }
            } else if (in.equals("quit") || in.equals("exit")) {
                running = false;
                app.exit();
            } else {
                Util.v(SHELL_ROOT, "Invalid command [" + in + "]");
            }

            sc = new Scanner(System.in);
        } while (running);
    }


    public static void testMain(String[] args) {
    }

    public static void main(String[] args) {
        startMain(args);
//        testMain(args);
    }
}
