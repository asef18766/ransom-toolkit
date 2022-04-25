package com.asef18766.RansomToolkit;

public class Misc {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    public static void LogInfo(String tag, String msg) {
        System.out.println(String.format("I/%s/%s", tag, msg));
    }
    public static void LogWarning(String tag, String msg) {
        System.out.println(String.format("W/%s/%s", tag, msg));
    }
    public static void LogError(String tag, String msg, Throwable e) {
        System.out.println(String.format("E/%s/%s", tag, msg));
        e.printStackTrace();
    }
    public static void LogDebug(String tag, String msg) {
        System.out.println(String.format("D/%s/%s", tag, msg));
    }

}
