package mua;
import src.mua.Operation;
import src.mua.Read;
import src.mua.VariableSpace;

public class Main {
    public static void main(String[] args){
        Read in = new Read();
        
        VariableSpace vs = new VariableSpace();
        Operation op = new Operation(vs, in);

        while( in.hasNext() ){
            System.out.println(in.getLine());
        }
    }
}