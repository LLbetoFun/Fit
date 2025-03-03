package cxy.fun.obfuscate.transfomer.impl.flow;

import cxy.fun.obfuscate.transfomer.AbsTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AnalyzerAdapter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import java.awt.*;

public class FrameComputer extends AbsTransformer<MethodNode>{

    public FrameComputer() {
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
        );        for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
            insn.accept(analyzer);
            if (insn instanceof LabelNode) {
                // 插入显式帧信息

                Object[] locals = analyzer.locals.toArray();
                Object[] stacks = analyzer.stack.toArray();
                methodNode.visitFrame(Opcodes.F_NEW, locals.length, locals, stacks.length, stacks);
            }
        }
    }
}
