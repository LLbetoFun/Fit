package cxy.fun.obfuscate;

import cxy.fun.obfuscate.transfomer.AbsTransformer;
import cxy.fun.obfuscate.transfomer.impl.clzz.LoaderInclude;
import cxy.fun.obfuscate.transfomer.impl.flow.BasicBlockFlow;
import cxy.fun.obfuscate.transfomer.impl.flow.BlurJumpFlow;
import cxy.fun.obfuscate.transfomer.impl.flow.JunkCodeTransformer;
import cxy.fun.obfuscate.transfomer.impl.ldc.BooleanBlur;
import cxy.fun.obfuscate.transfomer.impl.ldc.IntObfuscate;
import cxy.fun.obfuscate.transfomer.impl.ldc.NumberEncyTransformer;
import cxy.fun.obfuscate.transfomer.impl.ldc.StringEncyTransformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Transformer {
    private static final HashMap<Class<?>, List<AbsTransformer<?>>> transformers = new HashMap<>();
    public static void registerTransformer(AbsTransformer<?> transformer) {
        if(!transformers.containsKey(transformer.getTarget())) {
            ArrayList<AbsTransformer<?>> list = new ArrayList<>();
            list.add(transformer);
            transformers.put(transformer.getTarget(), list);
        }
        else {
            transformers.get(transformer.getTarget()).add(transformer);
        }
    }
    public static List<AbsTransformer<?>> getTransformer(Class<?> clazz) {
        return transformers.get(clazz);
    }
    public static <T> void transform(T target) {
        List<AbsTransformer<?>> transformers = getTransformer(target.getClass());
        if(transformers!=null) {
            for (AbsTransformer<?> transformer : transformers) {
                AbsTransformer<T> t = (AbsTransformer<T>) transformer;

                if (transformer != null) {
                    t.transform(target);
                }
            }
        }

    }
    static {
        registerTransformer(new IntObfuscate());
        registerTransformer(new BasicBlockFlow());
        registerTransformer(new StringEncyTransformer());
        registerTransformer(new LoaderInclude());

        registerTransformer(new BlurJumpFlow());
        registerTransformer(new BooleanBlur());
        //registerTransformer(new JunkCodeTransformer());
        registerTransformer(new NumberEncyTransformer());




    }
}
