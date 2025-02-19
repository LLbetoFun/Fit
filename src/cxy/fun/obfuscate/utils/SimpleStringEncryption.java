package cxy.fun.obfuscate.utils;

public class SimpleStringEncryption {

    /**
     * 加密字符串
     *
     * @param input 原始字符串
     * @return 加密后的字符串（Base64编码）
     */
    public static String encrypt(String input) {
        String key="love.cxy";
        // 将输入字符串和密钥转换为字节数组
        byte[] inputBytes = input.getBytes();
        byte[] keyBytes = key.getBytes();

        // 对每个字节进行XOR操作
        byte[] encryptedBytes = new byte[inputBytes.length];
        for (int i = 0; i < inputBytes.length; i++) {
            encryptedBytes[i] = (byte) (inputBytes[i] ^ keyBytes[i % keyBytes.length]);
        }

        // 返回Base64编码的加密结果
        return java.util.Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 解密字符串
     *
     * @param encryptedInput 加密后的字符串（Base64编码）
     * @return 解密后的原始字符串
     */
    public static String decrypt(String encryptedInput) {
        String key="love.cxy";

        // 将Base64编码的字符串解码为字节数组
        byte[] encryptedBytes = java.util.Base64.getDecoder().decode(encryptedInput);
        byte[] keyBytes = key.getBytes();

        // 对每个字节进行XOR操作
        byte[] decryptedBytes = new byte[encryptedBytes.length];
        for (int i = 0; i < encryptedBytes.length; i++) {
            decryptedBytes[i] = (byte) (encryptedBytes[i] ^ keyBytes[i % keyBytes.length]);
        }

        // 返回解密后的字符串
        return new String(decryptedBytes);
    }


}