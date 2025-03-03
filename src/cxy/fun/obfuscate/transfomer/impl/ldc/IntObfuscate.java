package cxy.fun.obfuscate.transfomer.impl.ldc;

import cxy.fun.obfuscate.transfomer.AbsTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Random;

public class IntObfuscate extends AbsTransformer<MethodNode> {
    public IntObfuscate() {
        super(MethodNode.class);
    }

    @Override
    public void transform(MethodNode methodNode) {
        InsnList instructions = methodNode.instructions;
        Random random = new Random();
        int startLocal=methodNode.maxLocals;
        for (AbstractInsnNode insn : methodNode.instructions) {
            // 处理 LDC 指令
            if (insn.getOpcode() == Opcodes.LDC) {
                LdcInsnNode ldcInsn = (LdcInsnNode) insn;
                Object value = ldcInsn.cst;

                // 根据常量类型进行混淆
                if (value instanceof String) {
                    // 字符串常量混淆
                    instructions.insertBefore(ldcInsn, obfuscateString((String) value));
                    instructions.remove(ldcInsn);
                } if (insn.getOpcode() == Opcodes.LDC && ((LdcInsnNode) insn).cst instanceof Integer) {
                    int v2 = (Integer) ldcInsn.cst;

                    // 插入复杂的混淆逻辑
                    instructions.insertBefore(ldcInsn, obfuscateInt(v2, random,startLocal));
                    startLocal+=2;
                    instructions.remove(ldcInsn);
                }
            }

        }
    }
    private static InsnList obfuscateString(String value) {
        InsnList list = new InsnList();

        // 将字符串拆分为两部分
        int splitIndex = value.length() / 2;
        String part1 = value.substring(0, splitIndex);
        String part2 = value.substring(splitIndex);

        // 生成字节码：将两部分字符串拼接
        list.add(new LdcInsnNode(part1));
        list.add(new LdcInsnNode(part2));
        list.add(new MethodInsnNode(
                Opcodes.INVOKEVIRTUAL,
                "java/lang/String",
                "concat",
                "(Ljava/lang/String;)Ljava/lang/String;",
                false
        ));

        return list;
    }
    /**
     * 混淆整数常量
     */
    private static InsnList obfuscateInt(int value, Random random, int startLocal) {
        InsnList list = new InsnList();

        // 生成可逆的混淆参数
        int xorKey = random.nextInt();
        int addKey = random.nextInt(1000);
        int subKey = random.nextInt(1000);

        // 混淆操作序列（需严格可逆）
        // 原始值 → 加 → 异或 → 减 → 最终值
        int encrypted = ((value + addKey) ^ xorKey) - subKey;


        // 生成字节码
        list.add(new LdcInsnNode(encrypted)); // 加载混淆后的值
        list.add(new LdcInsnNode(subKey));    // 加载减法密钥
        list.add(new InsnNode(Opcodes.IADD)); // 抵消减法：encrypted + subKey = (value+addKey)^xorKey
        list.add(new LdcInsnNode(xorKey));    // 加载异或密钥
        list.add(new InsnNode(Opcodes.IXOR)); // 抵消异或：→ value + addKey
        list.add(new LdcInsnNode(addKey));    // 加载加法密钥
        list.add(new InsnNode(Opcodes.ISUB)); // 抵消加法：→ value

        // 使用独立局部变量（避免冲突）
        list.add(new VarInsnNode(Opcodes.ISTORE, startLocal));
        list.add(new VarInsnNode(Opcodes.ILOAD, startLocal));

        return list;
    }
}

