package cxy.fun.obfuscate;

import cxy.fun.config.ConfigParser;
import cxy.fun.obfuscate.asm.Utils;
import cxy.fun.obfuscate.rename.RenameMapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class ClassTraverser {
    public static byte[] traverser(byte[] input) {
        ClassNode cn = Utils.node(input);
        System.out.println(cn.name);
        if(Utils.matchRegexes(ConfigParser.Instance.getClasses(),cn.name)){
            Transformer.transform(cn);
            if(!Utils.matchRegexes(ConfigParser.Instance.getKeepClasses(),cn.name))traverserClassMembers(cn);
            cn.sourceDebug=null;
            cn.sourceFile=null;

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS);
            ClassRemapper mapper = new ClassRemapper(cw,new RenameMapper());

            cn.accept(mapper);
            return cw.toByteArray();
        }
        return input;
    }
    private static void traverserClassMembers(ClassNode cn) {
        for(Object m:cn.methods){
            MethodNode mn = (MethodNode)m;
            if(!Utils.matchRegexes(ConfigParser.Instance.getKeepMethods(),mn.name))transformMethod(mn);
        }
        for(Object m:cn.fields){
            FieldNode fn = (FieldNode)m;
            transformField(fn);
        }
    }
    private static void transformMethod(MethodNode mn) {
        Transformer.transform(mn);
        /*for(AbstractInsnNode ain:mn.instructions) {
            Transformer.transform(ain);
        }*/
    }
    private static void transformField(FieldNode fn) {
        Transformer.transform(fn);
    }
}
