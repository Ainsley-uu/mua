package src.mua;

import java.util.Scanner;

public class Read {
    Scanner in = new Scanner(System.in);

    public String getLine(){
        String str = new String();
        // while( in.hasNext() ){
        String tempStr = in.nextLine();
        str += tempStr + ' ';
        // }
        return str.trim();
    }

    public String getWord(){
        String str = new String();
        // while( in.hasNext() ){
            String tempStr = in.next();
            str += tempStr + ' ';
        // }
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
