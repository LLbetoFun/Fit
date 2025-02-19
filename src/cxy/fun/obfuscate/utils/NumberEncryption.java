package cxy.fun.obfuscate.utils;

public class NumberEncryption {

    // 加密密钥（可以自定义）
    private static final int KEY = 0xAA; // 示例密钥

    /**
     * 加密数字
     *
     * @param number 原始数字
     * @return 加密后的数字
     */
    public static Integer encrypt(Integer number) {
        // 1. 与密钥进行异或操作
        int encrypted = number ^ KEY;
        // 2. 左移 4 位
        encrypted = encrypted << 4;
        return encrypted;
    }

    /**
     * 解密数字
     *
     * @param encryptedNumber 加密后的数字
     * @return 解密后的原始数字
     */
    public static int decrypt(int encryptedNumber) {
        // 1. 右移 4 位
        int decrypted = encryptedNumber >>> 4;
        // 2. 与密钥进行异或操作
        decrypted = decrypted ^ KEY;
        return decrypted;
    }


}