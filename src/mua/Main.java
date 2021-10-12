package src.mua;

public class Main {
    public static void main(String[] args){
        Read in = new Read();
        VariableSpace vs = new VariableSpace();
        Operation op = new Operation(vs, in);
        while( in.hasNext() ){
            op.runInstruction(in.getLine());
        }
    }
}
