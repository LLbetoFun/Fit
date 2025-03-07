package cxy.fun.obfuscate.rename;

import cxy.fun.config.ConfigParser;
import cxy.fun.obfuscate.Obfuscutor;
import cxy.fun.obfuscate.asm.Utils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static cxy.fun.obfuscate.asm.Utils.isMainClass;

public class Mappings {
    public static HashMap<String,String> classMap = new HashMap<>();
    public static HashMap<String,String> methodMap = new HashMap<>();
    public static HashMap<String,String> fieldMap = new HashMap<>();
    public static void readMapping(byte[] bytes) throws ClassNotFoundException {
        ClassNode classNode = Utils.node(bytes);
        readMapping(classNode);
    }
    public static void readMapping(ClassNode classNode) throws ClassNotFoundException {
        readMapping(classNode.superName);

        if(!isMainClass(classNode)&&!Utils.matchRegexes(ConfigParser.Instance.getKeepClasses(),classNode.name)){
            for(MethodNode methodNode : classNode.methods) {
                if(!methodNode.name.equals("<init>")&&!methodNode.name.equals("<clinit>")
                        &&!(methodNode.name.equals("main")&&methodNode.desc.equals("([Ljava/lang/String;)V"))
                        &&!methodMap.containsKey(methodNode.name+" "+methodNode.desc)
                        &&!Utils.matchRegexes(ConfigParser.Instance.getKeepMethods(),methodNode.name))
                            methodMap.put(methodNode.name+" "+methodNode.desc, generateRandomString(3));
            }
            for(FieldNode fieldNode : classNode.fields) {
                if(!Utils.matchRegexes(ConfigParser.Instance.getKeepMethods(),fieldNode.name))fieldMap.put(fieldNode.name+" "+fieldNode.desc, generateRandomString(3));
            }
            classMap.put(classNode.name,ConfigParser.Instance.getNewPackage()+generateRandomString(3));//.replace('.','/')
        }
        else classMap.put(classNode.name,classNode.name);
    }
    public static void readMapping(String className) throws ClassNotFoundException {
        ClassNode classNode = Obfuscutor.classes.get(className);
        if(classNode == null){
            readLibrary(className);
        }
        else readMapping(classNode);
    }
    public static void readLibrary(String clzz) throws ClassNotFoundException {
        if (clzz==null)return;
        try{
            Class<?> klz=Class.forName(clzz.replace('/','.'));
            if(klz.getSuperclass() != null){
                readLibrary(klz.getSuperclass().getName());
            }
            for(Class<?> itf:klz.getInterfaces()){
                readLibrary(itf.getName());
            }
            for(Method method:klz.getDeclaredMethods()){
                methodMap.put(method.getName()+" "+Utils.getMethodDescriptor(method),method.getName());
                //System.out.println(method.getName()+" "+Utils.getMethodDescriptor(method));
            }

            for(Field field:klz.getDeclaredFields()){
                fieldMap.put(field.getName()+" "+Utils.getFieldDescriptor(field),field.getName());
            }
            classMap.put(clzz.replace('.','/'),klz.getName().replace('.','/'));
        }catch (Exception e){
            System.err.println(e.getMessage());
            classMap.put(clzz.replace('.','/'),clzz.replace('.','/'));

        }

    }
    public static String generateRandomString(int length) {
        char[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        List<String> dictionary=ConfigParser.Instance.getDictionary();
        for(char c:characters){
            if(dictionary.size()< 26) dictionary.add(0,String.valueOf(c));
        }
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(dictionary.size());
            sb.append(dictionary.get(index));
        }

        return sb.toString();
    }
}
