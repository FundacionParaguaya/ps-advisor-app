package android.util;

import static java.lang.String.format;

/**
 * A replacement logger during testing.
 */

public class Log {
    public static int d(String tag, String msg) {
        print("DEBUG", tag, msg);
        return 0;
    }

    public static int d(String tag, String msg, Throwable e) {
        return d(tag, msg);
    }

    public static int i(String tag, String msg) {
        print("INFO", tag, msg);
        return 0;
    }

    public static int i(String tag, String msg, Throwable e) {
        return i(tag, msg);
    }

    public static int w(String tag, String msg) {
        print("WARN", tag, msg);
        return 0;
    }

    public static int w(String tag, String msg, Throwable e) {
        return w(tag, msg);
    }

    public static int e(String tag, String msg) {
        print("ERROR", tag, msg);
        return 0;
    }

    public static int e(String tag, String msg, Throwable e) {
        return e(tag, msg);
    }

    private static void print(String level, String tag, String msg) {
        System.out.println(format("%s: %s - %s", level, tag, msg));
    }
}
