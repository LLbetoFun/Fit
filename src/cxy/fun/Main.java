package cxy.fun;

import cxy.fun.obfuscate.Obfuscutor;

/**
 * 你们好
 *
 * 参数：outJar inJar
 */
public class Main {
    public static void main(String[] args) throws Exception {
        String inJar=args[0];
        String outJar=args[1];
        Obfuscutor.obfuscate(inJar, outJar);
    }
}
