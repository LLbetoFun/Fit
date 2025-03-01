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
                    instructions.insertBefore(ldcInsn, obfuscateInt(v2, random));
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
    private static InsnList obfuscateInt(int value, Random random) {
        InsnList list = new InsnList();

        // 生成多个随机密钥和操作数
        int key1 = random.nextInt();
        int key2 = random.nextInt();
        int key3 = random.nextInt();
        int addValue = random.nextInt(100); // 随机加减操作数

        // 混淆逻辑
        list.add(new LdcInsnNode(value ^ key1)); // 第一次异或
        list.add(new LdcInsnNode(key1));
        list.add(new InsnNode(Opcodes.IXOR));

        list.add(new VarInsnNode(Opcodes.ISTORE, 1999)); // 保存到局部变量 1
        list.add(new VarInsnNode(Opcodes.ILOAD, 1999));  // 从局部变量 1 读取

        list.add(new LdcInsnNode(key2)); // 第二次异或
        list.add(new InsnNode(Opcodes.IXOR));

        list.add(new LdcInsnNode(addValue)); // 加法
        list.add(new InsnNode(Opcodes.IADD));

        list.add(new VarInsnNode(Opcodes.ISTORE, 2001)); // 保存到局部变量 2
        list.add(new VarInsnNode(Opcodes.ILOAD, 2001));  // 从局部变量 2 读取

        list.add(new LdcInsnNode(key3)); // 第三次异或
        list.add(new InsnNode(Opcodes.IXOR));

        return list;
    }}

