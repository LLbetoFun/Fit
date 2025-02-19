package cxy.fun.obfuscate.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;

public class BlockUtils {
    public static ArrayList<InsnList> getBlocks(InsnList insnList) {
        ArrayList<InsnList> blocks = new ArrayList<>();
        blocks.add(new InsnList());
        for(AbstractInsnNode node:insnList){
            if(canBeforeSplit(node)){
                blocks.add(new InsnList());
            }
            blocks.get(blocks.size()-1).add(node);
            if (canAfterSplit(node)) {
                blocks.add(new InsnList());
            }
        }
        return blocks;
    }
    private static boolean canAfterSplit(AbstractInsnNode node){
        if(node instanceof MethodInsnNode method){
            String desc = method.desc;
            return Type.getReturnType(desc).getSort()==(Type.VOID);
        }
        if(node instanceof FieldInsnNode field){
            return field.getOpcode() == Opcodes.PUTFIELD;
        }
        if(node instanceof VarInsnNode var){
            return var.getOpcode() == Opcodes.ASTORE
                    || var.getOpcode() == Opcodes.ISTORE
                    || var.getOpcode() == Opcodes.LSTORE
                    || var.getOpcode() == Opcodes.FSTORE
                    || var.getOpcode() == Opcodes.DSTORE;
        }

        return false;
    }
    private static boolean canBeforeSplit(AbstractInsnNode node){
        if(node instanceof InsnNode var){
            return var.getOpcode() == Opcodes.ARETURN
                    || var.getOpcode() == Opcodes.IRETURN
                    || var.getOpcode() == Opcodes.LRETURN
                    || var.getOpcode() == Opcodes.FRETURN
                    || var.getOpcode() == Opcodes.DRETURN
                    || var.getOpcode() == Opcodes.RETURN;
        }
        return false;
    }
}
