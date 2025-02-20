package cxy.fun.obfuscate.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FitLoader {
    public static native void init();
    public static void loadLibraryFromJar(String dllName) throws Exception {
        // 构造 DLL 文件在 JAR 包中的路径
        String resourcePath = "/native/" + dllName;
        InputStream inputStream = FitLoader.class.getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new RuntimeException("DLL not found in JAR: " + resourcePath);
        }

        // 创建临时文件
        File tempFile = File.createTempFile(dllName, ".dll");
        tempFile.deleteOnExit(); // JVM 退出时删除临时文件

        // 将 DLL 文件写入临时文件
        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        // 加载 DLL 文件
        System.load(tempFile.getAbsolutePath());
    }
    public static void load() throws Exception {
        loadLibraryFromJar("fit.dll");
        //Class.forName(ByteCodeEncryption.class.getName());

        init();
    }
}
