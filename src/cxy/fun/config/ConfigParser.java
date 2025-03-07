package cxy.fun.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ConfigParser {
    private final String fileName;
    private final List<String> classes=new ArrayList<String>();
    private final List<String> keepClasses=new ArrayList<String>();
    private final List<String> keepMethods=new ArrayList<String>();
    private final List<String> keepFields=new ArrayList<String>();
    private final List<String> flowExcludeClasses=new ArrayList<String>();
    private final List<String> dictionary=new ArrayList<>();
    private boolean enableBytesEncrypt=true;
    private boolean enableFlow=true;
    private int safeLocalRange=5;

    private String newPackage="";
    public ConfigParser(String fileName) throws IOException {
        this.fileName = fileName;
        List<String> lines= Files.readAllLines(Paths.get(fileName).toAbsolutePath());
        for (String line : lines) {
            if (line.startsWith("-")) {
                if (line.startsWith("-class")) {
                    classes.add(line.split(" ")[1]);
                }
                if (line.startsWith("-keepclass")) {
                    keepClasses.add(line.split(" ")[1]);
                    System.out.println("Keep class:" + line.split(" ")[1]);
                }
                if (line.startsWith("-keepmethod")) {
                    keepMethods.add(line.split(" ")[1]);
                }
                if (line.startsWith("-keepfield")) {
                    keepFields.add(line.split(" ")[1]);
                }
                if (line.startsWith("-bytecodeencrypt")) {
                    enableBytesEncrypt= Boolean.parseBoolean(line.split(" ")[1]);
                }
                if (line.startsWith("-flow")) {
                    if(line.split(" ")[1].equals("exclude")){
                        flowExcludeClasses.add(line.split(" ")[2]);
                    }
                    else enableFlow= Boolean.parseBoolean(line.split(" ")[1]);
                }
                if (line.startsWith("-newpackage")) {
                    newPackage= line.split(" ")[1];
                }
                if(line.toLowerCase().startsWith("-safelocalrange")){
                    safeLocalRange= Integer.parseInt(line.split(" ")[1]);
                }
                if(line.startsWith("-dictionary")){
                    String[] split = line.split(" ");
                    for (int i = 0, splitLength = split.length; i < splitLength; i++) {
                        String s = split[i];
                        if(i<1)continue;
                        dictionary.add(s);
                    }
                }//exclude
            }
        }
    }
    public static ConfigParser Instance;
    public static ConfigParser loadConfig(String fileName) throws IOException {
        Instance = new ConfigParser(fileName);
        return Instance;
    }
    public String getNewPackage() {
        return (newPackage.isEmpty()||newPackage.endsWith("/")?newPackage:newPackage+"/");
    }
    public int getSafeLocalRange(){
        return safeLocalRange;
    }
    public List<String> getDictionary(){
        return dictionary;
    }
    public List<String> getClasses() {
        return classes;
    }

    public boolean isEnableFlow() {
        return enableFlow;
    }

    public List<String> getFlowExcludeClasses() {
        return flowExcludeClasses;
    }

    public boolean isEnableBytesEncrypt() {
        return enableBytesEncrypt;
    }

    public List<String> getKeepClasses() {
        return keepClasses;
    }
    public List<String> getKeepMethods() {
        return keepMethods;
    }
    public List<String> getKeepFields() {
        return keepFields;
    }
    public String getFileName() {
        return fileName;
    }


}
