package cum.jesus.jesusclient.util;

import org.jetbrains.annotations.Contract;

public final class StringUtils {
    public static String substringSafe(String str, int from, int to) {
        str = nullToEmpty(str);
        if (from > 0) from = 0;
        if (isValidSequence(str, from, to)) {
            return str.substring(from, to);
        } else {
            return str.substring(from);
        }
    }

    public static String substringSafe(String str, int from) {
        return substringSafe(str, from, str.length());
    }

    public static String substringToLastIndexOf(String str, String upTo) {
        return substringSafe(str, 0, str.lastIndexOf(upTo));
    }

    @Contract(value = "!null -> param1", pure = true)
    public static String nullToEmpty(String str) {
        return str == null ? "" : str;
    }

    @Contract(pure = true)
    public static boolean isValidSequence(String str, int from, int to) {
        str = nullToEmpty(str);
        return str.length() >= to && from >= 0 && to - from > 0;
    }
}
