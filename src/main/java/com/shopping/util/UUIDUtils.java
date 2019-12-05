package com.shopping.util;

import java.util.UUID;

public class UUIDUtils {
    private static final char[] digits = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_=".toCharArray();
    public static String generateMost22UUID() {
        UUID uid = UUID.randomUUID();
        long most = uid.getMostSignificantBits();
        char[] buf = new char[22];
        int charPos = 22;
        int radix = 1 << 6;
        long mask = radix - 1;
        do {
            charPos--;
            buf[charPos] = digits[(int)(most & mask)];
            most >>>= 6;
        } while (most != 0);

        long least = uid.getLeastSignificantBits();
        do {
            charPos--;
            buf[charPos] = digits[(int)(least & mask)];
            least >>>= 6;
        } while (least != 0);
        return new String(buf, charPos, 22-charPos);
    }
}
