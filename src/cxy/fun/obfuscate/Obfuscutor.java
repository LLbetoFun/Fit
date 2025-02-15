package cxy.fun.obfuscate;

import cxy.fun.obfuscate.asm.Utils;
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

public class Obfuscutor {


    public static void obfuscate(String input,String output) throws Exception {
        File targetJar = new File(output);
        File jarIn=new File(input);

        try(JarFile jar = new JarFile(jarIn)){
            if(targetJar.exists()){
                targetJar.delete();
            }
            targetJar.createNewFile();
            JarOutputStream jos=new JarOutputStream(Files.newOutputStream(targetJar.toPath()));
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if(entry.getName().endsWith(".class")){
                    InputStream is=jar.getInputStream(entry);
                    byte[] b= ClassTraverser.traverser(getAllBytes(is));
                    jos.putNextEntry(new ZipEntry(entry.getName()));
                    jos.write(b);
                    jos.closeEntry();

                }
                else {
                    InputStream is=jar.getInputStream(entry);
                    jos.putNextEntry(entry);
                    byte[] b= getAllBytes(is);
                    jos.write(b);
                    jos.closeEntry();
                }
            }
            jos.close();

        }
        catch(IOException e){
            e.printStackTrace();
        }

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
