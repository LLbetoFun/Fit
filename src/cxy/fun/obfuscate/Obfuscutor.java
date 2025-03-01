package cxy.fun.obfuscate;

import cxy.fun.config.ConfigParser;
import cxy.fun.obfuscate.asm.Utils;
import cxy.fun.obfuscate.include.Includer;
import cxy.fun.obfuscate.rename.Mappings;
import cxy.fun.obfuscate.utils.ByteCodeEncryption;
import jdk.jshell.execution.Util;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static cxy.fun.obfuscate.asm.Utils.isMainClass;

public class Obfuscutor {
    public static Map<String, ClassNode> classes = new HashMap<>();


    public static void obfuscate(String input, String output) throws Exception {
        File targetJar = new File(output);
        File jarIn = new File(input);

        // 创建临时 JAR 文件
        File tempJar = File.createTempFile("temp", ".jar");
        tempJar.deleteOnExit(); // 程序退出时删除临时文件

        try (JarFile jar = new JarFile(jarIn)) {
            if (targetJar.exists()) {
                targetJar.delete();
            }
            Utils.setMainClass(jar.getManifest().getMainAttributes().getValue("Main-Class"));
            cacheJar(jarIn);

            // 第一阶段：按照你的逻辑处理 JAR 文件，写入临时 JAR 文件
            try (JarOutputStream tempJos = new JarOutputStream(Files.newOutputStream(tempJar.toPath()))) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    InputStream is = jar.getInputStream(entry);

                    // 处理 .class 文件
                    if (entry.getName().endsWith(".class")) {
                        byte[] b = ClassTraverser.traverser(Utils.getAllBytes(is));
                        ClassNode cn = Utils.node(b);
                        if (!isMainClass(b) && ConfigParser.Instance.isEnableBytesEncrypt()
                                && Utils.matchRegexes(ConfigParser.Instance.getClasses(), cn.name)
                                && !Utils.matchRegexes(ConfigParser.Instance.getKeepClasses(), cn.name)) {
                            b = ByteCodeEncryption.encrypt(b);
                        }
                        tempJos.putNextEntry(new ZipEntry(cn.name + ".class"));
                        tempJos.write(b);
                    } else {
                        // 处理非 .class 文件
                        tempJos.putNextEntry(entry);
                        byte[] b = Utils.getAllBytes(is);
                        tempJos.write(b);
                    }
                    tempJos.closeEntry();
                }

                // 处理额外的类和文件
                for (byte[] bytes : Includer.includeClasses()) {
                    try {
                        ClassNode cn = Utils.node(bytes);
                        tempJos.putNextEntry(new ZipEntry(cn.name + ".class"));
                        tempJos.write(bytes);
                        tempJos.closeEntry();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                for (String path : Includer.includeFiles().keySet()) {
                    tempJos.putNextEntry(new ZipEntry(path));
                    tempJos.write(Includer.includeFiles().get(path));
                    tempJos.closeEntry();
                }
            }

            // 第二阶段：从临时 JAR 文件中读取内容，清理空条目，写入目标 JAR 文件
            try (JarFile tempJarFile = new JarFile(tempJar);
                 JarOutputStream jos = new JarOutputStream(Files.newOutputStream(targetJar.toPath()))) {

                // 用于记录非空目录
                Set<String> nonEmptyDirectories = new HashSet<>();

                // 第一次遍历：记录所有非空目录
                Enumeration<JarEntry> entries = tempJarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (!entry.isDirectory()) {
                        // 获取文件的父目录
                        String parentDir = getParentDirectory(entry.getName());
                        if (parentDir != null) {
                            nonEmptyDirectories.add(parentDir);
                        }
                    }
                }

                // 第二次遍历：写入非空条目
                entries = tempJarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    InputStream is = tempJarFile.getInputStream(entry);

                    // 如果是目录且为空，则跳过
                    if (entry.isDirectory()) {
                        String dirName = entry.getName();
                        if (!nonEmptyDirectories.contains(dirName)) {
                            continue; // 跳过空目录
                        }
                    }

                    // 写入条目
                    jos.putNextEntry(entry);
                    byte[] b = Utils.getAllBytes(is);
                    jos.write(b);
                    jos.closeEntry();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 删除临时文件
            tempJar.delete();
        }
    }

    /**
     * 获取文件所在的父目录
     */
    private static String getParentDirectory(String entryName) {
        int lastSlashIndex = entryName.lastIndexOf('/');
        if (lastSlashIndex != -1) {
            return entryName.substring(0, lastSlashIndex + 1);
        }
        return null;
    }


    public static void cacheJar(File file) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(file.toPath()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null)
                if (!entry.isDirectory())
                    if (entry.getName().endsWith(".class")){
                        byte[] b=Utils.getAllBytes(zis);

                        classes.put(entry.getName().replace("/", ".").substring(0, entry.getName().length() - 6), Utils.node(b));

                        Mappings.readMapping(b);

                    }
        }
    }

}
