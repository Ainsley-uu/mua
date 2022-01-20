package src.mua;

import java.util.Stack;
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
            put("erase", 1);
            put("eq", 2);
            put("gt", 2);
            put("lt", 2);
            put("isname", 1);
            put("isword", 1);
            put("isnumber", 1);
            put("islist", 1);
            put("isbool", 1);
            put("isempty", 1);
            put("and", 2);
            put("or", 2);
            put("not", 1);
            put("if", 3);
            put("return", 1);
            put("run", 1);
            put("export", 1);
            put("sentence", 2);
            put("list", 2);
            put("join", 2);
            put("first", 1);
            put("butfirst", 1);
            put("butlast", 1);
            put("save", 1);
            put("load", 1);
            put("erall", 0);
            put("random", 1);
            put("int", 1);
            put("sqrt", 1);
        }
    };

    public Vector<String> splitInstruction(String instruction){
        if (instruction.equals("")) return new Vector<>();

        Vector<String> instArray = new Vector<>();
        instruction = instruction.trim();
        String[] splitStr = instruction.split("\\s+");
        for (int i = 0; i < splitStr.length; i++) {
            if( judgeBrack(splitStr[i]))
                instArray.add(splitStr[i]);
            else
                splitStr[i+1] = splitStr[i] + " " + splitStr[i+1];
        }
        return instArray;
    }

    public boolean isOperation(String str){
        return argNumber.containsKey(str);
    }

    public boolean isFunc(String s){
        if( s.charAt(0) == '\"' || s.charAt(0) == '(' )
            return false;
        Data re = new Data(s);
        // System.out.println("\nf:"+re.getWord());
        return !re.isList() && !re.isBool() && !re.isNumber();
    }

    public boolean judgeBrack(String s){
        Stack<Character> brack = new Stack<Character>();
        for( int i=0 ; i<s.length() ; i++ ){
            Character c = s.charAt(i);
            if( c == '[' || c == '(' ){
                brack.push(c);
            }else if( c==']' ){
                if( brack.empty() || brack.pop() != '[')
                    return false;
            }else if( c == ')' ){
                if( brack.empty() || brack.pop() != '(' )
                    return false;
            }
        }
        return brack.empty();
    }
}
