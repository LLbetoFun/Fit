package cxy.fun.obfuscate.include;

import cxy.fun.obfuscate.asm.Utils;
import cxy.fun.obfuscate.utils.SimpleStringEncryption;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Includer {
    private final static List<byte[]> includeClasses=new ArrayList<>();
    public static List<byte[]> include(){
        return includeClasses;
    }
    public static void registryIncludeClass(String name) throws IOException {
        includeClasses.add(Utils.getClassBytes(name));
    }
    public static void registryIncludeClass(Class clzz) throws IOException {
        includeClasses.add(Utils.getClassBytes(clzz.getName()));
    }
    static {
        try {
            registryIncludeClass(SimpleStringEncryption.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
