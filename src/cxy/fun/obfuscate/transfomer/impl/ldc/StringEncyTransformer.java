package cxy.fun.obfuscate.transfomer.impl.ldc;

import cxy.fun.obfuscate.transfomer.AbsTransformer;
import cxy.fun.obfuscate.utils.SimpleStringEncryption;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class StringEncyTransformer extends AbsTransformer<MethodNode> {
    public StringEncyTransformer() {
        super(MethodNode.class);
    }

    @Override
    public void transform(MethodNode methodNode) {
        for (AbstractInsnNode insn : methodNode.instructions) {
            if (insn instanceof LdcInsnNode) {
                if(((LdcInsnNode) insn).cst instanceof String) {
                    methodNode.instructions.insert(insn,generateStringEncy((String) ((LdcInsnNode) insn).cst));
                    methodNode.instructions.remove(insn);
                }
            }
        }
    }
    private static InsnList generateStringEncy(String str) {
        InsnList list = new InsnList();
        list.add(new LdcInsnNode(SimpleStringEncryption.encrypt(str)));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(SimpleStringEncryption.class),"decrypt", "(Ljava/lang/String;)Ljava/lang/String;", false));
        return list;
    }
}
