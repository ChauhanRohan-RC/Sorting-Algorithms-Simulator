import util.Util;
import gl.GLConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import processing.core.PApplet;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class R {

    public static final boolean FROZEN = false;         // TODO set to true before packaging

    public static final Path DIR_MAIN = (FROZEN? Path.of("app"): Path.of("")).toAbsolutePath();
    public static final Path DIR_RES = DIR_MAIN.resolve("res");
    public static final Path DIR_IMAGE = DIR_RES.resolve("image");
//    public static final Path DIR_FONT = DIR_RES.resolve("font");
//
    public static final Path APP_ICON = DIR_IMAGE.resolve("icon.png");
//    @Nullable
//    public static final Path IMAGE_BG = DIR_IMAGE.resolve("deep_space_2.jpg");

//    public static final Path FONT_PD_SANS_REGULAR = DIR_FONT.resolve("product_sans_regular.ttf");
//    public static final Path FONT_PD_SANS_MEDIUM = DIR_FONT.resolve("product_sans_medium.ttf");


    public static final String APP_NAME = "Sorting Algorithms Simulator";

    @NotNull
    public static String getDataSizeText(int dataSize) {
        return "Size: " + dataSize;
    }

    @NotNull
    public static String getSpeedText(float fps) {
        return String.format("Speed: %d%% (%d fps)", (int) Math.ceil(GLConfig.fpsToPercent(fps)), (int) Math.ceil(fps));
    }

    @NotNull
    public static String getSortOrderText(boolean inverted) {
        return "Order: " + (inverted? "INVERTED": "ORIGINAL");
    }

    @NotNull
    public static String getStatusText(int dataSize, boolean orderInverted, float fps) {
        return  getDataSizeText(dataSize) + "  |  " + getSortOrderText(orderInverted) + "  |  " + getSpeedText(fps);
    }


    public static final String STATUS_SORTING = "SORTING";
    public static final String STATUS_PAUSED = "PAUSED [SPACE]";
    public static final String DOT = ".";

    @NotNull
    public static String getSortingStatusText(@NotNull PApplet app, boolean paused) {
        if (paused) {
            return STATUS_PAUSED;
        }

        final float rate = app.frameRate / 3;       // 3 = (dots per sec)
        final float count = (app.frameCount % (rate * 4)) / rate;           // 4 = no of dots + 1
        return STATUS_SORTING + DOT.repeat((int) count);
    }


    /* ........................................... CONFIGURATIONS ....................................... */

    public static final Path FILE_CONFIG = DIR_MAIN.resolve("config.ini");
    public static final char CONFIG_SEP_CHAR = '=';

    @Nullable
    private static Map<String, String> sConfigMap;

    @NotNull
    private static Map<String, String> loadConfig() {
        final Map<String, String> map = new HashMap<>();
        try (Stream<String> stream = Files.lines(FILE_CONFIG, StandardCharsets.UTF_8)) {
            stream.forEach(line -> {
                int sepIndex;
                if (Util.notEmpty(line) && (sepIndex = line.indexOf(CONFIG_SEP_CHAR)) != -1) {
                    String key = line.substring(0, sepIndex), value = line.substring(sepIndex + 1);
                    if (Util.notEmpty(key) && Util.notEmpty(value)) {
                        map.put(key, value);
                    }
                }
            });
        } catch (Throwable exc) {
            exc.printStackTrace();
        }

        return map;
    }

    @NotNull
    public static Map<String, String> getConfig() {
        if (sConfigMap == null) {
            sConfigMap = Collections.synchronizedMap(loadConfig());
        }

        return sConfigMap;
    }

    @Nullable
    public static String getConfigValueRaw(@NotNull String key) {
        return getConfig().get(key);
    }

    public static <T> T getConfigValue(@NotNull String key, T defValue, @NotNull Function<String, T> caster) {
        final String val = getConfig().get(key);
        if (Util.notEmpty(val)) {
            try {
                return caster.apply(val);
            } catch (ClassCastException ignored) {
            }
        }

        return defValue;
    }

    public static int getConfigValueInt(@NotNull String key, int defValue) {
        final String val = getConfig().get(key);
        if (Util.notEmpty(val)) {
            try {
                return Integer.parseInt(val);
            } catch (ClassCastException ignored) {
            }
        }

        return defValue;
    }

    public static float getConfigValueFloat(@NotNull String key, float defValue) {
        final String val = getConfig().get(key);
        if (Util.notEmpty(val)) {
            try {
                return Float.parseFloat(val);
            } catch (ClassCastException ignored) {
            }
        }

        return defValue;
    }

    public static long getConfigValueLong(@NotNull String key, long defValue) {
        final String val = getConfig().get(key);
        if (Util.notEmpty(val)) {
            try {
                return Long.parseLong(val);
            } catch (ClassCastException ignored) {
            }
        }

        return defValue;
    }

    public static double getConfigValueDouble(@NotNull String key, double defValue) {
        final String val = getConfig().get(key);
        if (Util.notEmpty(val)) {
            try {
                return Integer.parseInt(val);
            } catch (ClassCastException ignored) {
            }
        }

        return defValue;
    }

    public static boolean getConfigValueBool(@NotNull String key, boolean defValue) {
        return getConfigValueInt(key, defValue? 1: 0) > 0;
    }


    public static boolean createReadme(@NotNull String instructions) {
        try (PrintWriter w = new PrintWriter("readme.txt", StandardCharsets.UTF_8)) {
            w.print(instructions);
            w.flush();
            return true;
        } catch (Throwable exc) {
            exc.printStackTrace();
        }

        return false;
    }

}
