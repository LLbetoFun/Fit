package cxy.fun.obfuscate.transfomer.impl.flow;


import cxy.fun.obfuscate.transfomer.AbsTransformer;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class BlurJumpFlow extends AbsTransformer<MethodNode> {
    public BlurJumpFlow() {//模糊jump指令判断条件
        super(MethodNode.class);
    }

    @Override
    public void transform(MethodNode methodNode) {
        for (AbstractInsnNode instruction : methodNode.instructions.toArray()) {
            if (instruction instanceof JumpInsnNode jumpInsn) {

                // 获取上一个指令
                AbstractInsnNode previousInstruction = jumpInsn.getPrevious();

                // 判断上一个操作是否是 boolean 类型
                try{
                    if (isBooleanOperation(previousInstruction)) {
                        // 反转 if 条件
                        //int invertedOpcode = invertJumpOpcode(jumpInsn.getOpcode());
                        //jumpInsn.setOpcode(invertedOpcode);

                        // 反转前面的 boolean
                        methodNode.instructions.insertBefore(jumpInsn, new InsnNode(Opcodes.ICONST_0)); // 加载 true
                        methodNode.instructions.insertBefore(jumpInsn, new InsnNode(Opcodes.IXOR));      // 对 boolean 进行 XOR 操作（反转）
                    }
                }catch (Exception e){
                }

            }
        }
    }
    private static boolean isBooleanOperation(AbstractInsnNode instruction) {
        if (instruction == null) {
            return false;
        }

        // 检查上一个操作是否是加载 int 类型（boolean 在字节码中用 int 表示）
        return switch (instruction.getOpcode()) {  // 加载局部变量（int/boolean）
            // 加载常量 0（false）
            // 加载常量 1（true）
            // int 加法
            // int 减法
            // int 乘法
            // int 除法
            // int 取余
            // int 取反
            // int 异或
            // int 与
            case Opcodes.ILOAD, Opcodes.ICONST_0, Opcodes.ICONST_1, Opcodes.IADD, Opcodes.ISUB, Opcodes.IMUL,
                 Opcodes.IDIV, Opcodes.IREM, Opcodes.INEG, Opcodes.IXOR, Opcodes.IAND, Opcodes.IOR ->     // int 或
                    true;
            default -> false;
        };
    }
    private static int invertJumpOpcode(int opcode) {

        return switch (opcode) {
            case Opcodes.IFEQ -> Opcodes.IFNE;
            case Opcodes.IFNE -> Opcodes.IFEQ;
            case Opcodes.IFLT -> Opcodes.IFGE;
            case Opcodes.IFGE -> Opcodes.IFLT;
            case Opcodes.IFGT -> Opcodes.IFLE;
            case Opcodes.IFLE -> Opcodes.IFGT;
            case Opcodes.IFNULL -> Opcodes.IFNONNULL;
            case Opcodes.IFNONNULL -> Opcodes.IFNULL;
            case Opcodes.IF_ICMPEQ -> Opcodes.IF_ICMPNE;
            case Opcodes.IF_ICMPNE -> Opcodes.IF_ICMPEQ;
            default -> throw new IllegalArgumentException("Unsupported jump opcode: " + opcode);
        };
    }

    /*
     遇到jump-> 其形式是boolean jump(code) -> label
     我们将其模糊:
        boolean || true jump(code) -> label
        这里的true 是人为制造的
     我们进一步进行模糊
        boolean(假设这是我们已经处理好的一个boolean值) ! jump(!code) -> label
        !:反转boolean 以及jumpcode 如ifeq->ifne ifne->ifeq ifnull->ifnonnull...


     */
}
