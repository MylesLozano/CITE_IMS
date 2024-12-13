package com.example.cite_ims;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class EncryptionUtils {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int IV_SIZE = 16;

    public static String encrypt(String input, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        byte[] iv = generateIV();
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] encryptedBytes = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));

        // Combine IV and encrypted bytes
        byte[] combined = new byte[IV_SIZE + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, IV_SIZE);
        System.arraycopy(encryptedBytes, 0, combined, IV_SIZE, encryptedBytes.length);

        return Base64.encodeToString(combined, Base64.DEFAULT);
    }

    public static String decrypt(String input, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        byte[] combined = Base64.decode(input, Base64.DEFAULT);

        byte[] iv = new byte[IV_SIZE];
        byte[] encryptedBytes = new byte[combined.length - IV_SIZE];

        System.arraycopy(combined, 0, iv, 0, IV_SIZE);
        System.arraycopy(combined, IV_SIZE, encryptedBytes, 0, encryptedBytes.length);

        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    private static byte[] generateIV() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    public static SecretKey generateKeyFromPassword(String password) {
        byte[] key = new byte[16];
        System.arraycopy(password.getBytes(StandardCharsets.UTF_8), 0, key, 0, Math.min(password.length(), 16));
        return new SecretKeySpec(key, ALGORITHM);
    }
}
