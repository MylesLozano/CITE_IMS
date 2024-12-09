package com.example.cite_ims;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;
import java.nio.charset.StandardCharsets;

public class EncryptionUtils {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    public static String encrypt(String input, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    public static String decrypt(String input, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.decode(input, Base64.DEFAULT);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static SecretKey generateKeyFromPassword(String password) {
        byte[] key = password.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(key, 0, 16, ALGORITHM); // use first 16 bytes (128 bits)
    }
}
