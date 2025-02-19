package cxy.fun.obfuscate.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {
    private static String mainClass;

    public static String getMainClass() {
        return mainClass;
    }
    public static void setMainClass(String mainClass) {
        Utils.mainClass = mainClass;
    }
    public static boolean isMainClass(byte[] bytes) {
        ClassNode classNode = node(bytes);
        return classNode.name.equals(mainClass.replace('.', '/'));
    }
    public static boolean isMainClass(ClassNode classNode) {
        return classNode.name.equals(mainClass.replace('.', '/'));
    }

    public static ClassNode node(byte[] bytes){
        ClassReader cr = new ClassReader(bytes);
        ClassNode cn = new ClassNode();
        cr.accept(cn, ClassReader.EXPAND_FRAMES);
        return cn;
    }
    public static byte[] getClassBytes(Class<?> c) throws IOException {
        String className = c.getName();
        String classAsPath = className.replace('.', '/') + ".class";
        InputStream stream = c.getClassLoader().getResourceAsStream(classAsPath);
        byte[] bs= stream == null ? null : getAllBytes(stream);
        return bs;
    }
    public static byte[] getClassBytes(String className) throws IOException {
        String classAsPath = className.replace('.', '/') + ".class";
        InputStream stream = Utils.class.getClassLoader().getResourceAsStream(classAsPath);
        byte[] bs= stream == null ? null : getAllBytes(stream);
        return bs;
    }
    public static byte[] getPathFileBytes(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }
    public static byte[] getAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();

        return buffer.toByteArray();
    }
}
