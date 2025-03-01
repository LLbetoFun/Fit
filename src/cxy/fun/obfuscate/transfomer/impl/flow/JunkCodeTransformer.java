package cxy.fun.obfuscate.transfomer.impl.flow;

import cxy.fun.obfuscate.asm.BlockUtils;
import cxy.fun.obfuscate.asm.JunkCodeGen;
import cxy.fun.obfuscate.transfomer.AbsTransformer;
import cxy.fun.obfuscate.utils.Pair;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JunkCodeTransformer extends AbsTransformer<MethodNode> {
    public JunkCodeTransformer() {
        super(MethodNode.class);
    }
    private final InsnList junkNodes = new InsnList();


    @Override
    public void transform(MethodNode methodNode) {
        // 获取方法的指令列表
        InsnList instructions = methodNode.instructions;
        AbstractInsnNode[] insnArray = instructions.toArray();

        // 随机插入 label 和 goto 的次数
        int labelCount = 5; // 插入 label 的次数
        int gotoCount = 5;  // 插入不可达 goto 的次数
        Random random = new Random();

        // 用于存储插入的 label
        List<LabelNode> labels = new ArrayList<>();

        // 随机插入 label
        for (int i = 0; i < labelCount; i++) {
            int index = random.nextInt(insnArray.length);
            LabelNode label = new LabelNode();
            instructions.insert(insnArray[index], label);
            labels.add(label); // 保存插入的 label
        }

        // 随机插入不可达的 goto
        for (int i = 0; i < gotoCount; i++) {
            int index = random.nextInt(insnArray.length);
            LabelNode targetLabel = new LabelNode(); // 创建一个永远不会被执行到的 label
            instructions.insert(insnArray[index], new JumpInsnNode(Opcodes.GOTO, targetLabel));
        }

        // 在方法最后插入所有未使用的 label，确保字节码合法
        for (LabelNode label : labels) {
            if (!isLabelUsed(instructions, label)) {
                instructions.add(label);
            }
        }
    }

    /**
     * 检查 label 是否被使用
     */
    private boolean isLabelUsed(InsnList instructions, LabelNode label) {
        // 将 instructions 转换为数组，避免动态修改导致的无限循环
        AbstractInsnNode[] insnArray = instructions.toArray();

        // 遍历数组
        for (AbstractInsnNode insn : insnArray) {
            if (insn instanceof JumpInsnNode) {
                JumpInsnNode jumpInsn = (JumpInsnNode) insn;
                if (jumpInsn.label == label) {
                    return true;
                }
            }
        }
        return false;
    }
}
