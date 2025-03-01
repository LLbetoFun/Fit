package cxy.fun.obfuscate.transfomer.impl.flow;

import cxy.fun.config.ConfigParser;
import cxy.fun.obfuscate.asm.BlockUtils;
import cxy.fun.obfuscate.asm.Utils;
import cxy.fun.obfuscate.transfomer.AbsTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AnalyzerAdapter;
import org.objectweb.asm.tree.*;

import java.io.ObjectInputFilter;
import java.util.*;


public class BasicBlockFlow extends AbsTransformer<MethodNode> {
    public BasicBlockFlow() {
        super(MethodNode.class);
    }
    private static final String STATE_VARIABLE_NAME = "state";

    private static void flattenControlFlow(MethodNode methodNode) {
        //if(methodNode.desc.endsWith("V"))System.out.println(methodNode.name);
        if(methodNode.localVariables==null|| !methodNode.desc.endsWith("V")|| !ConfigParser.Instance.isEnableFlow())return;

        // 1. 分析控制流，获取基本块
        Map<LabelNode, Integer> blockToState = new HashMap<>();
        List<LabelNode> blackList=new ArrayList<>();
        for(TryCatchBlockNode tryCatchBlockNode:methodNode.tryCatchBlocks){
            blackList.add(tryCatchBlockNode.handler);
        }
        for (AbstractInsnNode instruction : methodNode.instructions){
            if(instruction instanceof JumpInsnNode&&instruction.getOpcode()==Opcodes.GOTO){
                blackList.add(((JumpInsnNode) instruction).label);
            }
        }
        int state = 0;

        for (AbstractInsnNode instruction : methodNode.instructions) {
            if (instruction instanceof LabelNode&&!blackList.contains(instruction)) {
                blockToState.put((LabelNode) instruction, state++);
            }
        }

        // 2. 插入状态变量
        int stateVarIndex = methodNode.maxLocals+1;
        LabelNode switchStart = new LabelNode();//-noverify
        LabelNode switchEnd = new LabelNode();

        // 3. 在方法开头插入状态变量初始化和 switch 结构
        InsnList newInstructions = new InsnList();


        // 插入状态变量初始化
        newInstructions.add(new LdcInsnNode(0)); // 初始状态为 0
        newInstructions.add(new VarInsnNode(Opcodes.ISTORE, stateVarIndex)); // 存储状态变量
        newInstructions.add(switchStart);
        // 插入 switch 结构
        newInstructions.add(new VarInsnNode(Opcodes.ILOAD, stateVarIndex)); // 加载状态变量


        LabelNode[] labels = new LabelNode[blockToState.size()];
        for (Map.Entry<LabelNode, Integer> entry : blockToState.entrySet()) {
            labels[entry.getValue()] = entry.getKey();
        }
        newInstructions.add(new TableSwitchInsnNode(
                0, blockToState.size() - 1, switchEnd, labels
        ));

        // 将新指令插入到方法开头
        methodNode.instructions.insert(newInstructions);
        AbstractInsnNode lastReturn=null;


        AnalyzerAdapter analyzer = new AnalyzerAdapter(
                "java/lang/Object",
                methodNode.access,
                methodNode.name,
                methodNode.desc,
                null
        );
        // 4. 在每个基本块的开头插入状态变量的赋值和跳转
        for (AbstractInsnNode instruction : methodNode.instructions.toArray()) {
            instruction.accept(analyzer);
            //

            if (instruction instanceof LabelNode) {
                LabelNode label= (LabelNode) instruction;
                Integer blockState = blockToState.get(label);
                if (blockState != null) {

                    InsnList blockInstructions = new InsnList();
                    blockInstructions.add(new LdcInsnNode(blockState)); // 加载状态值
                    blockInstructions.add(new VarInsnNode(Opcodes.ISTORE, stateVarIndex)); // 存储状态变量
                    blockInstructions.add(new JumpInsnNode(Opcodes.GOTO, switchStart)); // 跳转到 switch

                    // 找到当前指令的位置
                    methodNode.instructions.insertBefore(instruction, blockInstructions);

                }
            }

            //instruction.accept(analyzer);

            if(BlockUtils.isReturn(instruction))lastReturn=instruction;
        }
        //methodNode.instructions.accept(analyzer);

        // 5. 在方法末尾插入 switchEnd 标签
        if(lastReturn!=null)methodNode.instructions.insertBefore(lastReturn,switchEnd);
        else methodNode.instructions.insertBefore(methodNode.instructions.getLast(),switchEnd);
    }

    @Override
    public void transform(MethodNode methodNode) {

        /*Stack<LabelNode> stack=new Stack<>();
        for(AbstractInsnNode ain : methodNode.instructions){
            if(Math.random()>0.8&&BlockUtils.canAfterSplit(ain)){
                //Pair<InsnList, LabelNode> p = JunkCodeGen.invalidJudgments();
                LabelNode labelNode1=new LabelNode();
                stack.push(labelNode1);
                methodNode.instructions.insert(ain,labelNode1);//
                //methodNode.instructions.insert(ain,p.getSecond());
            }
            if(Math.random()<0.4&&BlockUtils.canAfterSplit(ain)&& !stack.empty()){

                methodNode.instructions.insert(ain,stack.pop());
            }


        }*/
        flattenControlFlow(methodNode);

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
