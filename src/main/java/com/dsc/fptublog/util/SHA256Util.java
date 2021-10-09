package com.dsc.fptublog.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Util {

    public static byte[] encode(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String convertByteToHex(byte[] encodedHash) {
        StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
        for (byte hash : encodedHash) {
            String hex = Integer.toHexString(0xff & hash);
            if (hex.length() == 1) {
                hexString.append(0);
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String getEncryptedPassword(String input) throws NoSuchAlgorithmException {
        return convertByteToHex(encode(input));
    }
}
