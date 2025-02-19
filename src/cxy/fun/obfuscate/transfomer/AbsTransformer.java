package cxy.fun.obfuscate.transfomer;

public abstract class AbsTransformer<T> {
    private int code;
    private Class<T> target;
    public AbsTransformer(Class<T> target) {
        this.target = target;

    }

    public abstract void transform(T t);

    public int getCode() {
        return code;
    }
    public Class<T> getTarget() {
        return target;
    }
}
