package cxy.fun;

import cxy.fun.config.ConfigParser;
import cxy.fun.obfuscate.Obfuscutor;
import cxy.fun.obfuscate.rename.Mappings;

/**
 * 你们好
 *
 * 参数：outJar inJar
 */
public class Main {
    public static void main(String[] args) throws Exception {
        try{
            System.out.println(" /$$$$$$$$ /$$   /$$            /$$$$$$  /$$        /$$$$$$                                           /$$                        \n" +
                                "| $$_____/|__/  | $$           /$$__  $$| $$       /$$__  $$                                         | $$                        \n" +
                                "| $$       /$$ /$$$$$$        | $$  \\ $$| $$$$$$$ | $$  \\__//$$   /$$  /$$$$$$$  /$$$$$$$ /$$   /$$ /$$$$$$    /$$$$$$   /$$$$$$ \n" +
                                "| $$$$$   | $$|_  $$_/        | $$  | $$| $$__  $$| $$$$   | $$  | $$ /$$_____/ /$$_____/| $$  | $$|_  $$_/   /$$__  $$ /$$__  $$\n" +
                                "| $$__/   | $$  | $$          | $$  | $$| $$  \\ $$| $$_/   | $$  | $$|  $$$$$$ | $$      | $$  | $$  | $$    | $$  \\ $$| $$  \\__/\n" +
                                "| $$      | $$  | $$ /$$      | $$  | $$| $$  | $$| $$     | $$  | $$ \\____  $$| $$      | $$  | $$  | $$ /$$| $$  | $$| $$      \n" +
                                "| $$      | $$  |  $$$$/      |  $$$$$$/| $$$$$$$/| $$     |  $$$$$$/ /$$$$$$$/|  $$$$$$$|  $$$$$$/  |  $$$$/|  $$$$$$/| $$      \n" +
                                "|__/      |__/   \\___/         \\______/ |_______/ |__/      \\______/ |_______/  \\_______/ \\______/    \\___/   \\______/ |__/      ");
            String inJar=args[0];
            String outJar=args[1];
            String cfg=args[2];
            ConfigParser.loadConfig(cfg);
            Obfuscutor.obfuscate(inJar, outJar);
            System.out.println("Obfuscated files in " + inJar + " to " + outJar);
        }
        catch(ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            System.err.println("Args Error: " + e);
            System.err.println("Right Args:  \"java -jar Fit.jar <path_to_jarIn> <path_to_out> <path_to_config>\"");
        }

    }
}
