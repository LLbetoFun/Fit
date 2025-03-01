package cxy.fun.obfuscate.rename;

import org.objectweb.asm.commons.Remapper;

public class RenameMapper extends Remapper {
    @Override
    public String map(String internalName) {
        return Mappings.classMap.get(internalName);
    }

    @Override
    public String mapMethodName(String owner, String name, String descriptor) {
        return Mappings.methodMap.getOrDefault(name+" "+descriptor,name);
    }

    @Override
    public String mapFieldName(String owner, String name, String descriptor) {
        return Mappings.fieldMap.getOrDefault(name+" "+descriptor,name);
    }

}
