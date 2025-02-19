package cxy.fun.obfuscate.transfomer.impl.ldc;

import cxy.fun.obfuscate.transfomer.AbsTransformer;
import cxy.fun.obfuscate.utils.NumberEncryption;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class NumberEncyTransformer extends AbsTransformer<MethodNode> {
    public NumberEncyTransformer() {
        super(MethodNode.class);
    }

    @Override
    public void transform(MethodNode methodNode) {
        for (AbstractInsnNode insn : methodNode.instructions) {
            if (insn instanceof LdcInsnNode) {
                if(((LdcInsnNode) insn).cst instanceof Integer) {
                    methodNode.instructions.insert(insn,generateNumberEncy((Integer) ((LdcInsnNode) insn).cst));
                    methodNode.instructions.remove(insn);
                }
            }
        }
    }
    private static InsnList generateNumberEncy(Integer number) {
        InsnList list = new InsnList();
        list.add(new LdcInsnNode(NumberEncryption.encrypt(number)));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(NumberEncryption.class),"decrypt", "(I)I", false));
        return list;
    }
}
