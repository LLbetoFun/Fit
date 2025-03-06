package cxy.fun.obfuscate.transfomer.impl.flow;

import cxy.fun.obfuscate.transfomer.AbsTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AnalyzerAdapter;
import org.objectweb.asm.tree.*;

public class ExceptionFlow extends AbsTransformer<MethodNode> {
    public ExceptionFlow() {
        super(MethodNode.class);
    }

    @Override
    public void transform(MethodNode methodNode) {
        AnalyzerAdapter analyzer = new AnalyzerAdapter(
                "java/lang/Object",
                methodNode.access,
                methodNode.name,
                methodNode.desc,
                null
        );
        int expLocal=methodNode.maxLocals+1;
        InsnList list = new InsnList();
        String type = "java/lang/RuntimeException";
        list.add(new TypeInsnNode(Opcodes.NEW, type));
        list.add(new InsnNode(Opcodes.DUP));
        list.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,
                type,
                "<init>",
                "()V",
                false));
        list.add(new VarInsnNode(Opcodes.ASTORE, expLocal));


        methodNode.instructions.insert(list);
        for(AbstractInsnNode insn : methodNode.instructions) {
            if(insn instanceof JumpInsnNode&&insn.getOpcode()==Opcodes.GOTO&&(analyzer.stack==null||analyzer.stack.isEmpty())) {
                InsnList insnList = exceptions(methodNode,((JumpInsnNode) insn).label,expLocal);
                methodNode.instructions.insertBefore(insn, insnList);
            }
            insn.accept(analyzer);
        }
    }

    public InsnList exceptions(MethodNode mn,LabelNode label,int local) {
        InsnList list = new InsnList();
        String type = "java/lang/RuntimeException";
        LabelNode start = new LabelNode();
        LabelNode end = new LabelNode();
        TryCatchBlockNode tryCatchBlockNode=new TryCatchBlockNode(start,end,label,type);
        mn.tryCatchBlocks.add(tryCatchBlockNode);
        list.add(start);
        list.add(new VarInsnNode(Opcodes.ALOAD,local));
        //list.add(new VarInsnNode(Opcodes.ALOAD,local));
        list.add(new InsnNode(Opcodes.ATHROW));
        list.add(end);
        return list;
    }
}
