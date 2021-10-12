package src.mua;

import java.util.TreeMap;
import java.util.Vector;

public class Util {
    public TreeMap<String, Integer> argNumber = new TreeMap<String, Integer>(){
        {
            put("make", 2);
            put("thing", 1);
            put(":", 1);
            put("print", 1);
            put("read", 0);
            put("add", 2);
            put("sub", 2);
            put("mul", 2);
            put("div", 2);
            put("mod", 2);
        }
    };

    public Vector<String> splitInstruction(String instruction){
        if (instruction.equals("")) return new Vector<>();

        Vector<String> instArray = new Vector<>();
        instruction = instruction.trim();
        String[] splitStr = instruction.split("\\s+");
        for (int i = 0; i < splitStr.length; i++) {
                instArray.add(splitStr[i]);
        }
        return instArray;
    }

    public boolean isOperation(String str){
        return argNumber.containsKey(str);
    }
}
