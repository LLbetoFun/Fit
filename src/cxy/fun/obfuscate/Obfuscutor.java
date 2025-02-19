package cxy.fun.obfuscate;

import cxy.fun.obfuscate.asm.Utils;
import cxy.fun.obfuscate.include.Includer;
import cxy.fun.obfuscate.utils.ByteCodeEncryption;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static cxy.fun.obfuscate.asm.Utils.isMainClass;

public class Obfuscutor {
    public static Map<String, ClassNode> classes = new HashMap<>();


    public static void obfuscate(String input,String output) throws Exception {
        File targetJar = new File(output);
        File jarIn=new File(input);
        cacheJar(jarIn);
        try(JarFile jar = new JarFile(jarIn)){
            if(targetJar.exists()){
                targetJar.delete();
            }
            Utils.setMainClass(jar.getManifest().getMainAttributes().getValue("Main-Class"));
            targetJar.createNewFile();
            JarOutputStream jos=new JarOutputStream(Files.newOutputStream(targetJar.toPath()));
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if(entry.getName().endsWith(".class")){
                    InputStream is=jar.getInputStream(entry);
                    byte[] b= ClassTraverser.traverser(Utils.getAllBytes(is));
                    if(!isMainClass(b))b= ByteCodeEncryption.encrypt(b);
                    jos.putNextEntry(new ZipEntry(entry.getName()));
                    jos.write(b);
                    jos.closeEntry();

                }
                else {
                    InputStream is=jar.getInputStream(entry);
                    jos.putNextEntry(entry);
                    byte[] b= Utils.getAllBytes(is);
                    jos.write(b);
                    jos.closeEntry();
                }
            }

            for(byte[] bytes: Includer.includeClasses()){
                ClassNode cn=Utils.node(bytes);
                jos.putNextEntry(new ZipEntry(cn.name+".class"));
                jos.write(bytes);
                jos.closeEntry();
            }
            for(String path: Includer.includeFiles().keySet()){
                jos.putNextEntry(new ZipEntry(path));
                jos.write(Includer.includeFiles().get(path));
                jos.closeEntry();
            }
            jos.close();

        }
        catch(IOException e){
            e.printStackTrace();
        }

    }
    public static void cacheJar(File file) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(file.toPath()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null)
                if (!entry.isDirectory())
                    if (entry.getName().endsWith(".class"))
                        classes.put(entry.getName().replace("/", ".").substring(0, entry.getName().length() - 6), Utils.node(Utils.getAllBytes(zis)));
        }
    }

}
