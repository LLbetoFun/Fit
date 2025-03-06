package cxy.fun.obfuscate.asm;

import cxy.fun.config.ConfigParser;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class BlockUtils {
    public static ArrayList<List<AbstractInsnNode>> getBlocks(InsnList insnList) {
        ArrayList<List<AbstractInsnNode>> blocks = new ArrayList<>();
        blocks.add(new ArrayList<>());
        for(AbstractInsnNode node:insnList){
            if(isReturn(node)){
                blocks.add(new ArrayList<>());
            }
            blocks.get(blocks.size()-1).add(node);
            if (canAfterSplit(node)) {
                blocks.add(new ArrayList<>());
            }
        }
        return blocks;
    }
//    public static ArrayList<AbstractInsnNode> getLocalBlockNodes(MethodNode methodNode){
//        ArrayList<AbstractInsnNode> list=new ArrayList<>();
//        AbstractInsnNode[] insnList=methodNode.instructions.toArray();
//        VarInsnNode  latestStore = null;
//        VarInsnNode latestLoad = null;
//        for (AbstractInsnNode node : insnList) {
//            if (isStore(node)&&node instanceof VarInsnNode) {
//                latestStore=((VarInsnNode)node);
//                for(AbstractInsnNode node2 : insnList){
//                    if(isLoad(node2)&&node2 instanceof VarInsnNode&&((VarInsnNode) node2).var==latestStore.var){
//                        latestStore=((VarInsnNode) node2);
//                        latestLoad= (VarInsnNode) node2;
//                    }
//                }
//                list.add(latestLoad);
//            }
//        }
//        return list;
//    }
    private static int getStartLocalIndex(MethodNode method) {
        int start = 0;
        // 非静态方法跳过this指针
        if ((method.access & Opcodes.ACC_STATIC) == 0) {
            start = 1;
        }
        // 跳过方法参数
        Type[] args = Type.getArgumentTypes(method.desc);
        return start + args.length-1;
    }
    public static boolean isLocalVar(MethodNode mn,int local){
        return getStartLocalIndex(mn)<local;
    }
    public static Map<Integer, LocalVarRange> analyzeLocalVarRanges(MethodNode method) {
        Map<Integer, LocalVarRange> ranges = new HashMap<>();

        for (int i = 0; i < method.instructions.size(); i++) {
            AbstractInsnNode insn = method.instructions.get(i);
            if (insn instanceof VarInsnNode varInsn) {
                int varIndex = varInsn.var;
                if(!isLocalVar(method,varIndex))continue;

                // 更新局部变量范围
                LocalVarRange range = ranges.getOrDefault(varIndex, new LocalVarRange());
                range.start = Math.min(range.start, i);
                range.end = Math.max(range.end, i);
                ranges.put(varIndex, range);
            }
        }
        return ranges;
    }
    public static boolean isLocalSafeInsnNode(Map<Integer, BlockUtils.LocalVarRange> map,AbstractInsnNode insnNode,int index){
        for(LocalVarRange localVarRange:map.values()){
            if(index>=localVarRange.start- ConfigParser.Instance.getSafeLocalRange() &&index<=localVarRange.end+ConfigParser.Instance.getSafeLocalRange())return false;
        }
        return true;
    }
    public static class LocalVarRange {
        int start = Integer.MAX_VALUE;
        int end = Integer.MIN_VALUE;
    }
    public static boolean canAfterSplit(AbstractInsnNode node){
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
                    || var.getOpcode() == Opcodes.DSTORE
                    ;
        }

        return false;
    }
    public static boolean isStore(AbstractInsnNode node){
        if(node instanceof VarInsnNode var){
            return var.getOpcode() == Opcodes.ASTORE
                    || var.getOpcode() == Opcodes.ISTORE
                    || var.getOpcode() == Opcodes.LSTORE
                    || var.getOpcode() == Opcodes.FSTORE
                    || var.getOpcode() == Opcodes.DSTORE
                    ;
        }
        return false;
    }
    public static boolean isLoad(AbstractInsnNode node){
        if(node instanceof VarInsnNode var){
            return var.getOpcode() == Opcodes.ALOAD
                    || var.getOpcode() == Opcodes.ILOAD
                    || var.getOpcode() == Opcodes.LSTORE
                    || var.getOpcode() == Opcodes.FSTORE
                    || var.getOpcode() == Opcodes.DSTORE
                    ;
        }
        return false;
    }
    public static boolean isReturn(AbstractInsnNode node){
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
