package cxy.fun.obfuscate.transfomer.impl.flow;

import cxy.fun.obfuscate.asm.JunkCodeGen;
import cxy.fun.obfuscate.transfomer.AbsTransformer;
import cxy.fun.obfuscate.utils.Pair;
import org.objectweb.asm.tree.*;

import java.util.List;
import java.util.Random;

public class JunkCodeTransformer extends AbsTransformer<MethodNode> {
    public JunkCodeTransformer() {
        super(MethodNode.class);
    }
    private final InsnList junkNodes = new InsnList();

    public static <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List cannot be null or empty");
        }
        Random random = new Random();
        int randomIndex = random.nextInt(list.size()); // 生成随机索引
        return list.get(randomIndex); // 返回随机元素
    }
    @Override
    public void transform(MethodNode methodNode) {
        junkNodes.clear();
        for (AbstractInsnNode insn : methodNode.instructions) {
            if (Math.random()<0.05) {
                Pair<InsnList, LabelNode> pair = JunkCodeGen.invalidJudgments();
                methodNode.instructions.insert(insn, pair.getFirst());
                junkNodes.add(pair.getFirst());
                methodNode.instructions.insert(insn.getNext().getNext().getNext(), pair.getSecond());
                continue;
            }
            if (Math.random()<0.05) {
                Pair<InsnList, LabelNode> pair = JunkCodeGen.invalidFollow();
                methodNode.instructions.insert(insn, pair.getFirst());
                junkNodes.add(pair.getFirst());
                AbstractInsnNode n=insn.getNext().getNext().getNext();

                methodNode.instructions.insert(n, pair.getSecond());
            }

        }


    }
}
