package cxy.fun.obfuscate.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static String getFieldDescriptor(Field field) {
        // 使用 ASM 的 Type 类获取字段描述符
        return Type.getDescriptor(field.getType());
    }
    public static String getMethodDescriptor(Method method) {
        // 获取参数类型
        Class<?>[] parameterTypes = method.getParameterTypes();
        StringBuilder parameterDescriptors = new StringBuilder("(");
        for (Class<?> parameterType : parameterTypes) {
            parameterDescriptors.append(Type.getDescriptor(parameterType));
        }
        parameterDescriptors.append(")");

        // 获取返回类型
        Class<?> returnType = method.getReturnType();
        parameterDescriptors.append(Type.getDescriptor(returnType));

        return parameterDescriptors.toString();
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
    public static boolean matchRegex(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    public static boolean matchRegexes(List<String> regex, String str) {
        for(String regexStr : regex) {
            if(matchRegex(regexStr, str)) {
                return true;
            }
        }
        return false;
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
