package cxy.fun.obfuscate.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class ByteCodeEncryption {
    private static final String KEY = "GfQuJCKFJlSt0fX8KZDloA==";

    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // 128位密钥k
        return keyGen.generateKey();
    }

    public static byte[] encrypt(byte[] data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedData);
    }
    public static byte[] encrypt(byte[] data) throws Exception {
        // 动态生成 AES 密钥
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // 128 位密钥
        SecretKey key = keyGen.generateKey();
        byte[] keyBytes = key.getEncoded();

        // 使用生成的密钥加密数据
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = cipher.doFinal(data);

        // 将密钥附加到加密数据的末尾
        byte[] result = new byte[encryptedData.length + keyBytes.length];
        System.arraycopy(encryptedData, 0, result, 0, encryptedData.length);
        System.arraycopy(keyBytes, 0, result, encryptedData.length, keyBytes.length);

        return result;
    }

    public static byte[] decrypt(byte[] combinedData) throws Exception {
        // 从 combinedData 中提取密钥和加密数据
        if(combinedData[0]==(byte)0xCA&&combinedData[1]==(byte)0xFE)return null;

        int keySize = 16; // AES 128 位密钥的字节长度
        byte[] encryptedData = Arrays.copyOfRange(combinedData, 0, combinedData.length - keySize);
        byte[] keyBytes = Arrays.copyOfRange(combinedData, combinedData.length - keySize, combinedData.length);

        // 使用提取的密钥解密数据
        SecretKey key = new javax.crypto.spec.SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedData);
    }

    public static SecretKey stringToKey(String keyString) {
        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(keyBytes, "AES");
    }
    public static String keyToString(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}