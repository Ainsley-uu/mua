package src.mua;

import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;

public class Operation {
    Read input;
    Data output;
    TreeMap<String, Integer> argNum;
    VariableSpace space;
    Stack<Integer> argCount = new Stack<Integer>();
    Stack<Integer> argN = new Stack<>();
    Stack<Data> dataStack = new Stack<Data>();
    Util uu = new Util();

    public Operation( VariableSpace vs, Read in ){
        input = in;
        space = vs;
        this.argNum = uu.argNumber;
    }

    public void runInstruction(String instruction){
        instruction = instruction.replace(" :", " thing \"");
        instruction = instruction.trim();

        Vector<String> instArray = uu.splitInstruction(instruction);

        for( String inst : instArray){
            if(uu.isOperation(inst)){
                Integer x = argNum.get(inst);
                argCount.push(x);
                argN.push(x);
                dataStack.push(new Data(inst));
            }else{
                Data tmp = new Data(inst);
                if( !argCount.empty() ){
                    Integer top = argCount.pop() - 1;
                    argCount.push(top);
                    dataStack.push(tmp);
                }
            }
            while( !argCount.empty() && argCount.peek() == 0){
                argCount.pop();
                Integer top = argCount.empty() ? -1 : argCount.pop();
                int rets = runOperation(dataStack, argN.pop());
                if( rets < 0 )
                    return ;
                top = top - rets;
                if( top >= 0 )
                    argCount.push(top);
            }
        }
    }

    public Data make(Data name, Data value){
        space.input(name.getWord(), value);
        return value;
    }

    public Data thing( Data name ){
        return space.get(name.getWord());
    }

    public Data print(Data out){
        System.out.println(out.getWord());
        return new Data(out.getWord());
    }

    public Data read(){
        return new Data(input.getWord());
    }

    public Data calculateOpt(String op, Data num1, Data num2 ){
        double n1 = num1.getNumber();
        double n2 = num2.getNumber();

        switch(op){
            case "add":
                return new Data( n1 + n2 );
            case "sub":
                return new Data( n1 - n2 );
            case "mul":
                return new Data( n1 * n2 );
            case "div":
                return new Data( n1 / n2 );
            case "mod":
                return new Data( n1 % n2 );
        }
        return new Data(0);
    }
    
    public Integer runOperation(Stack<Data> dataStack, int argN){
        Vector<Data> tmp = new Vector<Data>();
        while ( argN >= 0 ){
            argN -= 1;
            tmp.add(dataStack.pop());
        }
        
       
        switch( tmp.lastElement().getOperation()){
            case "make":
                make(tmp.elementAt(1), tmp.elementAt(0));
                return 0;
            case "thing":
            case ":":
                dataStack.push(thing(tmp.elementAt(0)));
                return 1;
            case "print":
                dataStack.push(print(tmp.elementAt(0)));
                return 1;
            case "read":
                dataStack.push(read());
                return 1;
            case "add":
            case "sub":
            case "mul":
            case "div":
            case "mod":
                dataStack.push(calculateOpt(tmp.elementAt(2).getOperation(), tmp.elementAt(1), tmp.elementAt(0)));
                return 1;
            default:
                return -1;
        }

    }
    // public static void main(String[] args){
    //     Read in = new Read();
    //     VariableSpace vs = new VariableSpace();
    //     Operation op = new Operation(vs, in);
    //     // while( in.hasNext() ){
    //         String s = "make name value thing ycs :ddd print name read add 1 2 mode 10 2";
    //         op.runInstruction(s);
    //     // }
    // }
}
