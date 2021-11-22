package src.mua;

import java.util.Scanner;
import java.util.Stack;

public class Read {
    Scanner in = new Scanner(System.in);

    public String getLine(){
        String str = new String();
        Stack<Character> brackets = new Stack<>();

        while( in.hasNext() ){
            String tempStr = in.nextLine();
            str += tempStr + ' ';
            for (int i = 0; i < tempStr.length(); i++) {
                char ch = tempStr.charAt(i);
                switch (ch) {
                    case '[':
                    case '(':
                        brackets.push(ch);
                        break;
                    case ']':
                    case ')':
                        brackets.pop();
                }
            }
            if (brackets.empty()) {
                break;
            }
        }
        return str.trim();
    }

    public String getWord(){
        String str = new String();
        Stack<Character> brackets = new Stack<>();

        while( in.hasNext() ){
            String tempStr = in.next();
            str += tempStr + ' ';
            for (int i = 0; i < tempStr.length(); i++) {
                char ch = tempStr.charAt(i);
                switch (ch) {
                    case '[':
                    case '(':
                        brackets.push(ch);
                        break;
                    case ']':
                    case ')':
                        brackets.pop();
                }
            }
            if (brackets.empty()) {
                break;
            }
        }
        return str.trim();
    }

    public boolean hasNext(){
        return in.hasNext();
    }

    // public static void main(String[] args){
    //     Read in = new Read();
    //     // VariableSpace vs = new VariableSpace();
    //     // Operation op = new Operation(vs, in);
    //     while( in.hasNext() ){
    //         String s = in.getLine();
    //         System.out.println(s);
    //     }
    // }
}
