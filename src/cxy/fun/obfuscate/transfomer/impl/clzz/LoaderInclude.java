package cxy.fun.obfuscate.transfomer.impl.clzz;

import cxy.fun.obfuscate.asm.Utils;
import cxy.fun.obfuscate.include.Includer;
import cxy.fun.obfuscate.transfomer.AbsTransformer;
import cxy.fun.obfuscate.utils.FitLoader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class LoaderInclude extends AbsTransformer<ClassNode> {
    public LoaderInclude() {
        super(ClassNode.class);
    }

    @Override
    public void transform(ClassNode classNode) {
        if(Utils.isMainClass(classNode)){
            Includer includer=new Includer();
            for (MethodNode methodNode : classNode.methods) {
                if(methodNode.name.equals("<clinit>")){
                    includer.transform(methodNode);
                    return;
                }
            }
            MethodNode methodNode=new MethodNode();
            methodNode.access=Opcodes.ACC_STATIC;
            methodNode.name="<clinit>";
            methodNode.desc="()V";
            methodNode.instructions.add(new InsnNode(Opcodes.RETURN));
            classNode.methods.add(methodNode);
            includer.transform(methodNode);
        }
    }
    static class Includer extends AbsTransformer<MethodNode> {
        public Includer() {
            super(MethodNode.class);
        }

        @Override
        public void transform(MethodNode methodNode) {
            methodNode.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(FitLoader.class),"load","()V"));
        }
    }
}
