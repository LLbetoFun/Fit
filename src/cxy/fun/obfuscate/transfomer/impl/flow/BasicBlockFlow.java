package cxy.fun.obfuscate.transfomer.impl.flow;

import cxy.fun.obfuscate.asm.BlockUtils;
import cxy.fun.obfuscate.asm.JunkCodeGen;
import cxy.fun.obfuscate.transfomer.AbsTransformer;
import cxy.fun.obfuscate.utils.Pair;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BasicBlockFlow extends AbsTransformer<MethodNode> {
    public BasicBlockFlow() {
        super(MethodNode.class);
    }

    @Override
    public void transform(MethodNode methodNode) {
        List<InsnList> blocks= BlockUtils.getBlocks(methodNode.instructions);

        for(AbstractInsnNode insn: methodNode.instructions) {
            if (Math.random()<0.01) {
                Pair<InsnList, LabelNode> pair = JunkCodeGen.invalidBlock();
                InsnList insnList = new InsnList();
                insnList.add(pair.getFirst());
                insnList.add(getRandomElement(blocks));
                insnList.add(pair.getSecond());
                methodNode.instructions.insert(insn, insnList);
                break;
            }
        }

    }
    public static <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List cannot be null or empty");
        }
        Random random = new Random();
        int randomIndex = random.nextInt(list.size()); // 生成随机索引
        return list.get(randomIndex); // 返回随机元素
    }
}
