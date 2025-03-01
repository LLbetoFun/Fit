package cxy.fun.obfuscate.asm;

import cxy.fun.obfuscate.utils.Pair;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JunkCodeGen {

    public static Pair<InsnList,LabelNode> invalidJudgments(){
        int i= (int) (Math.random()*114514);
        InsnList list=new InsnList();
        list.add(new LdcInsnNode(i));
        list.add(new LdcInsnNode(i*2-678));
        LabelNode label=new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IF_ICMPNE,label));
        LabelNode label1=new LabelNode();
        list.add(new JumpInsnNode(Opcodes.GOTO,label1));
        list.add(label);
        return new Pair<>(list,label1);
    }

    public static Pair<InsnList,LabelNode> invalidFollow(){
        int i= (int) (Math.random()*114514);
        InsnList list=new InsnList();
        list.add(new LdcInsnNode(i));
        list.add(new LdcInsnNode(i*2-678));
        LabelNode label=new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IF_ICMPEQ,label));


        return new Pair<>(list,label);
    }
    public static InsnList invalidFollow(LabelNode label){
        int i= (int) (Math.random()*114514);
        InsnList list=new InsnList();
        /*list.add(new LdcInsnNode(i));
        list.add(new LdcInsnNode(i*2-678));*/

        list.add(new JumpInsnNode(Opcodes.GOTO,label));


        return list;
    }

    public static Pair<InsnList,LabelNode> invalidBlock(){
        int i= (int) (Math.random()*114514);
        InsnList list=new InsnList();
        list.add(new LdcInsnNode(i));
        list.add(new LdcInsnNode(i*2-678));
        LabelNode label=new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IF_ICMPNE,label));


        return new Pair<>(list,label);
    }
}
