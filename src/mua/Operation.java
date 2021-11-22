package mua;

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
        instruction = instruction.replace("["," [");
        instruction = instruction.trim();

        Vector<String> instArray = uu.splitInstruction(instruction);

        for( String inst : instArray ){
           // System.out.println(inst);
            if(uu.isOperation(inst)){
                Integer x = argNum.get(inst);
                argCount.push(x);
                argN.push(x);
                dataStack.push(new Data(inst));
            }else if( uu.isFunc(inst) ) {
                Integer x = funcArgNumber(inst);
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

    public int funcArgNumber(String name){
        String s = space.get(name).getWord();
        Vector<String> tmp;
        tmp = uu.splitInstruction(s.substring(1, s.length()-1));
        s = tmp.get(0);
        // System.out.print(s);
        tmp = uu.splitInstruction(s.substring(1, s.length()-1));
        return tmp.size();
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
            case "erase":
                erase(tmp.elementAt(0));
                return 1;
            case "eq":
            case "gt":
            case "lt":
                dataStack.push(compareOpt(tmp.elementAt(2).getOperation(), tmp.elementAt(1), tmp.elementAt(0)));
                return 1;
            case "isname":
                dataStack.push(isName(tmp.elementAt(0)));
                return 1;
            case "isword":
            case "islist":
            case "isbool":
            case "isempty":
            case "isnumber":
                dataStack.push(typeOpt(tmp.elementAt(1).getOperation(), tmp.elementAt(0)));
                return 1;
            case "and":
            case "or":
                dataStack.push(logicOpt(tmp.elementAt(2).getOperation(), tmp.elementAt(1), tmp.elementAt(0)));
                return 1;
            case "if":
                ifOpt(tmp.elementAt(2).getBool(), tmp.elementAt(1), tmp.elementAt(0));
                output = tmp.elementAt(0);
                return 0;
            case "not":
                dataStack.push(not(tmp.elementAt(0)));
                return 1;
            case "return":
                output = tmp.elementAt(0);
                return -1;
            case "export":
                space.export(tmp.elementAt(0));
                return 0;
            default:
                return func(dataStack, tmp);
        }
    }

    public int func(Stack<Data> dataStack, Vector<Data> arg){
        Data funName, funBody;
        String f = space.get(arg.lastElement().getWord()).getList();
        // System.out.println(f);
        arg.remove(arg.size()-1);
        Vector<String> funTmp = uu.splitInstruction(f);

        funName = new Data(funTmp.get(0));
        // System.out.println(funName.getWord());

        funBody = new Data(funTmp.get(1));
        // System.out.println(funBody.getWord());

        VariableSpace funSpace = new VariableSpace(space.fatherName);
        funTmp = uu.splitInstruction(funName.getList());

        for( String i : funTmp ){
            // System.out.println(i + " " + arg.lastElement().getWord());

            funSpace.input(i, arg.lastElement());
            arg.remove(arg.size()-1);
        }
        Operation funOperation = new Operation(funSpace, input);
        funOperation.output = null;
        funOperation.runInstruction(funBody.getList());
        if(funOperation.output != null ){
            dataStack.push(funOperation.output);
            return 1;
        }else 
            return 0;
    }

    public void ifOpt(boolean op, Data list1, Data list2 ){
        String s;
        if( op )
            s = list1.getList();
        else
            s = list2.getList();
        runInstruction(s);
    }

    public Data isName(Data name){
        if(space.hasName(name.getWord()))
            return new Data("true");
        return new Data("false");
    }

    public Data not(Data value){
        if ( value.getBool() )
            return new Data("false");
        return new Data("true");
    }

    public Data logicOpt(String op, Data value1, Data value2 ){
        boolean num1 = value1.getBool();
        boolean num2 = value2.getBool();
        switch(op){
            case "and":
                return new Data( num1 && num2 );
            case "or":
                return new Data( num1 || num2 );
        }
        return new Data(true);
    }

    public Data compareOpt(String op, Data value1, Data value2){
        
        if( value1.isNumber() && value2.isNumber() ){
            switch(op){
                case "eq":
                    return new Data(value1.getWord().equals(value2.toString()));
                case "gt":
                    return new Data(value1.getWord().compareTo(value2.getWord()) > 0);
                case "lt":
                    return new Data(value1.getWord().compareTo(value2.getWord()) < 0);
            }
        } 
        return new Data(false);
    }

    public Data typeOpt( String op, Data value){
        switch(op){
            case "isword":
                return new Data(value.isWord());
            case "isnumber":
                return new Data(value.isNumber());
            case "islist":
                return new Data(value.isList());
            case "isbool":
                return new Data(value.isBool());
            case "isempty":
                if(value.isWord())
                    return new Data(value.getWord().equals(""));
                else if(value.isList())
                    return new Data(value.getList().equals(""));
        }
        return new Data(value.isWord());
    }

    public void erase(Data name){
        space.erase(name.getWord());
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
