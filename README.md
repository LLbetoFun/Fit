# **Fit Obfuscutor**

A Java ByteCode Obfuscutor

## Docs

How to Use?

1.Download a version or compile it from source

2.Run this obfuscation program with java -jar Fit.jar "inJar" "outJar" "Config" (the path can be absolute or relative)

#### Configs

-class some/class --------Specify the classes you need to obfuscate

-keep some/class --------Specify the classes you need to keep/not obfuscate

-keepmethod method --------Specify the methods you need to keep/not obfuscate

-keepfield field --------Specify the fields you need to keep/not obfuscate

-bytecodeencrypt boolean(true/false) Enable/disable bytecode encryption

-flow boolean(true/false) Enable/disable control flow obfuscation

-safelocalrange integer it will make sure that the stack frame is calculated correctly.The default value is 5, and you can leave this field if you do not have special requirements

#### Libraray

Yes, it's easy to add dependencies to Fit Obfuscutor. All you need to do is add the classpath to the JVM



Good life

love by cxy



  