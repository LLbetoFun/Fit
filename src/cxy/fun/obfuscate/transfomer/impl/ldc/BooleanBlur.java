package cxy.fun.obfuscate.transfomer.impl.ldc;

import cxy.fun.obfuscate.transfomer.AbsTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Random;

public class BooleanBlur extends AbsTransformer<MethodNode> {
    public BooleanBlur() {
        super(MethodNode.class);
    }

    @Override
    public void transform(MethodNode methodNode) {
       for(AbstractInsnNode instruction: methodNode.instructions){
           // 替换 ICONST_1 (true)
           if (instruction.getOpcode() == Opcodes.ICONST_1) {
               int randomNumber = generateRandomNumber();
               methodNode.instructions.insertBefore(instruction, new LdcInsnNode(randomNumber)); // 加载随机数
               methodNode.instructions.insertBefore(instruction, new InsnNode(Opcodes.ICONST_1)); // 加载 1
               methodNode.instructions.insertBefore(instruction, new InsnNode(Opcodes.IXOR));     // 随机数 ^ 1
               methodNode.instructions.insertBefore(instruction, new LdcInsnNode(randomNumber)); // 加载随机数
               methodNode.instructions.insertBefore(instruction, new InsnNode(Opcodes.IXOR));     // (随机数 ^ 1) ^ 随机数 = 1
               methodNode.instructions.remove(instruction); // 移除原始的 ICONST_1
           }

           // 替换 ICONST_0 (false)
           if (instruction.getOpcode() == Opcodes.ICONST_0) {
               int randomNumber = generateRandomNumber();
               methodNode.instructions.insertBefore(instruction, new LdcInsnNode(randomNumber)); // 加载随机数
               methodNode.instructions.insertBefore(instruction, new InsnNode(Opcodes.ICONST_0)); // 加载 0
               methodNode.instructions.insertBefore(instruction, new InsnNode(Opcodes.IXOR));     // 随机数 ^ 0
               methodNode.instructions.insertBefore(instruction, new LdcInsnNode(randomNumber)); // 加载随机数
               methodNode.instructions.insertBefore(instruction, new InsnNode(Opcodes.IXOR));     // (随机数 ^ 0) ^ 随机数 = 0
               methodNode.instructions.remove(instruction); // 移除原始的 ICONST_0
           }
       }
    }
    private static int generateRandomNumber() {
        return random.nextInt(100) + 1; // 生成 1 到 100 的随机数
    }
    private static final Random random = new Random();


}
