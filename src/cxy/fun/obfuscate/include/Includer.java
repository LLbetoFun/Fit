package cxy.fun.obfuscate.include;

import cxy.fun.obfuscate.asm.Utils;
import cxy.fun.obfuscate.utils.ByteCodeEncryption;
import cxy.fun.obfuscate.utils.FitLoader;
import cxy.fun.obfuscate.utils.NumberEncryption;
import cxy.fun.obfuscate.utils.SimpleStringEncryption;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Includer {
    private final static List<byte[]> includeClasses=new ArrayList<>();
    private final static HashMap<String,byte[]> includeFiles=new HashMap<>();

    public static List<byte[]> includeClasses(){
        return includeClasses;
    }
    public static Map<String,byte[]> includeFiles(){
        return includeFiles;
    }
    public static void registryIncludeClass(String name) throws IOException {
        includeClasses.add(Utils.getClassBytes(name));
    }
    public static void registryIncludeClass(Class clzz) throws IOException {
        includeClasses.add(Utils.getClassBytes(clzz.getName()));
    }
    public static void registryIncludeClass(Class clzz,boolean encrypt) throws Exception {
        includeClasses.add(encrypt?ByteCodeEncryption.encrypt(Utils.getClassBytes(clzz.getName())):Utils.getClassBytes(clzz.getName()));
    }
    public static void registryPathFile(String path,String destPath) throws IOException {
        includeFiles.put(destPath,Utils.getPathFileBytes(path));
    }
    static {
        try {
            registryPathFile("./native/build/libnative.dll","native/fit.dll");
            registryIncludeClass(SimpleStringEncryption.class);
            registryIncludeClass(NumberEncryption.class);
            //registryIncludeClass(ByteCodeEncryption.class);
            registryIncludeClass(FitLoader.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
