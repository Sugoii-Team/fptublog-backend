package com.dsc.fptublog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Data
public class AdminEntity {
    private String username = "Admin";
    private String password = "c1c224b03cd9bc7b6a86d77f5dace40191766c485cd55dc48caf9ac873335d6f";

    private byte[] getByte(String inputPassword) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(inputPassword.getBytes(StandardCharsets.UTF_8));
        return encodedHash;
    }

    private String convertByteToHex(byte[] encodedHash){
        StringBuilder hexString = new StringBuilder(2*encodedHash.length);
        for (int i = 0; i < encodedHash.length; i++) {
            String hex = Integer.toHexString(0xff & encodedHash[i]);
            if(hex.length() == 1){
                hexString.append(0);
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String getEncryptedPassword(String input){
        try {
            return convertByteToHex(getByte(input));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
