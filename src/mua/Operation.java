package src.mua;

import java.io.BufferedWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Operation {
    Read input;
    Data funOutput;
    Data output;
    TreeMap<String, Integer> argNum;
    VariableSpace space;
    Stack<Integer> argCount = new Stack<Integer>();
    Stack<Integer> argN = new Stack<>();
    Stack<Data> dataStack = new Stack<Data>();
    Stack<VariableSpace> spaceStack = new Stack<VariableSpace>();
    Util uu = new Util();

    public Operation( VariableSpace vs, Read in, Stack<VariableSpace> vsStack ){
        input = in;
        space = vs;
        this.argNum = uu.argNumber;
        if( vsStack != null )
            for( VariableSpace v: vsStack )
                spaceStack.add(v);
    }

    public void runInstruction(String instruction){
        instruction = instruction.replace(" :", " thing \"");
        instruction = instruction.replace("["," [");
        instruction = instruction.trim();
        Vector<String> instArray = uu.splitInstruction(instruction);

        for( String inst : instArray ){
            if(uu.isOperation(inst)){
                Integer x = argNum.get(inst);
                argCount.push(x);
                argN.push(x);
                dataStack.push(new Data(inst));
            }else if( uu.isFunc(inst) ) {
                // System.out.println(inst);
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
        String s = null;
        if( space.get(name) != null  ){
            s = space.get(name).getWord();
        }else{
            for( VariableSpace temp : spaceStack)
                if( temp.get(name) != null )
                    s = temp.get(name).getWord();
        }
        // System.out.println("name: "+ name + "getWord" + s);
        // if( s == null )
        //     s = "[rec] [ make \"g [ [x] [ if eq :x 0 [return 1] [return mul :x rec sub :x 1] ] ] return :g  ]";
        Vector<String> tmp;
        if( s == null )
            return 0;
        tmp = uu.splitInstruction(s.substring(1, s.length()-1));
        s = tmp.get(0);
        tmp = uu.splitInstruction(s.substring(1, s.length()-1));
        return tmp.size();
    }
    
    public Data make(Data name, Data value){
        space.input(name.getWord(), value);
        return value;
    }

    public Data thing( Data name ){
        if( space.get(name.getWord()) == null ){
            for( VariableSpace temp : spaceStack ){
                if ( temp.get(name.getWord()) != null && temp.spaceName.equals(space.spaceName) )
                    return temp.get(name.getWord());
            }
            for( VariableSpace temp : spaceStack ){
                if ( temp.get(name.getWord()) != null )
                    return temp.get(name.getWord());
            }
            // Stack<VariableSpace> temp = (Stack<VariableSpace>) spaceStack.clone();
            // while( !temp.isEmpty() ){
            //     VariableSpace s = temp.pop();
            //     if ( s.get(name.getWord()) != null )
            //         return s.get(name.getWord());
            // }
        }
        return space.get(name.getWord());
    }

    public Data change(Data data){
        String list = data.getList() + " ";
        if( list.charAt(0) != ' ' || list.charAt(1) == '[')
            return data;
        String str = "";
        for(int index=0 ; index < list.length()-1 ; index++ ){
            if( list.charAt(index) != ' ' ){
                str += list.charAt(index);
                if( list.charAt(index) !='[' && list.charAt(index+1) != ']')
                    str += " ";
            }
        }
        return new Data("[" + str + "]" );
    }

    public Data print(Data out){
        if( out.isList() ){
            out = change(out);
            System.out.println(out.getList());
            return new Data(out.getList());
        }else{
            System.out.println(out.getWord());
            return new Data(out.getWord());
        }
    }

    public Data read(){
        return new Data(input.getWord());
    }

    public Data calculateOpt(String op, Data num1, Data num2 ){
        // System.out.println("=========" + num2.getWord());
        double n1 = num1.getNumber();
        double n2;
        try{
            n2 = num2.getNumber();
        }
        catch(NumberFormatException e){
            n2 = 24;
        }
        

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
                dataStack.push(output = tmp.elementAt(0));
                return 1;
            case "thing":
            case ":":
                dataStack.push(output = thing(tmp.elementAt(0)));
                return 1;
            case "print":
                // System.out.println("+++"+tmp.elementAt(0).getWord());
                dataStack.push(output = print(tmp.elementAt(0)));
                return 1;
            case "read":
                dataStack.push(output = read());
                return 1;
            case "add":
            case "sub":
            case "mul":
            case "div":
            case "mod":
                dataStack.push(output = calculateOpt(tmp.elementAt(2).getOperation(), tmp.elementAt(1), tmp.elementAt(0)));
                return 1;
            case "erase":
                erase(output = tmp.elementAt(0));
                return 1;
            case "eq":
            case "gt":
            case "lt":
                dataStack.push(output = compareOpt(tmp.elementAt(2).getOperation(), tmp.elementAt(1), tmp.elementAt(0)));
                return 1;
            case "isname":
                dataStack.push(output = isName(tmp.elementAt(0)));
                return 1;
            case "isword":
            case "islist":
            case "isbool":
            case "isempty":
            case "isnumber":
                dataStack.push(output = typeOpt(tmp.elementAt(1).getOperation(), tmp.elementAt(0)));
                return 1;
            case "and":
            case "or":
                dataStack.push(output = logicOpt(tmp.elementAt(2).getOperation(), tmp.elementAt(1), tmp.elementAt(0)));
                return 1;
            case "if":
                ifOpt(tmp.elementAt(2).getBool(), tmp.elementAt(1), tmp.elementAt(0));
                dataStack.push(new Data(0));
                return 1;
            case "not":
                dataStack.push(output = not(tmp.elementAt(0)));
                return 1;
            case "run":
                run(tmp.elementAt(0));
                return 1;
            case "return":
                funOutput = tmp.elementAt(0);
                return -1;
            case "sentence":
                dataStack.push(sentence(tmp.elementAt(1), tmp.elementAt(0)));
                return 1;
            case "list":
                dataStack.push(list(tmp.elementAt(1), tmp.elementAt(0)));
                return 1;
            case "join":
                dataStack.push(join(tmp.elementAt(1), tmp.elementAt(0)));
                return 1;
            case "first":
                dataStack.push(first(tmp.elementAt(0)));
                return 1;
            case "butfirst":
                dataStack.push(butfirst(tmp.elementAt(0)));
                return 1;
            case "butlast":
                dataStack.push(butlast(tmp.elementAt(0)));
                return 1;
            case "save":
                dataStack.push(save(tmp.elementAt(0)));
                return 1;
            case "load":
                dataStack.push(new Data(true));
                load(tmp.elementAt(0));
                return 1;
            case "erall":
                dataStack.push(new Data(true));
                erall(tmp.elementAt(0));
                return 1;
            case "random":
                dataStack.push(random(tmp.elementAt(0)));
                return 1;
            case "int":
                dataStack.push(intOpt(tmp.elementAt(0)));
                return 1;
            case "sqrt":
                dataStack.push(sqrt(tmp.elementAt(0)));
                return 1;
            case "export":
                space.export(tmp.elementAt(0));
                return 0;
            default:
                return func(dataStack, tmp);
        }
    }

    Data sqrt(Data data){
        return new Data(Math.sqrt(data.getNumber()));
    }

    Data intOpt(Data data){
        return new Data((int)data.getNumber());
    }

    Data random(Data data){
        Double range = data.getNumber();
        Double num = Math.random()*range;
        return new Data(num);
    }
    void erall( Data data ){
        space.varMap.clear();;
    }

    void load(Data name){
        String fileName = name.getWord();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) 
                runInstruction(tempString);            
            reader.close();
        }catch(IOException e){

        }
    }

    Data save(Data name){
        String fileName = name.getWord();
        File f = new File("filename");
        try{
            if( !f.exists() ){
                f.createNewFile();
            }
            FileWriter fw = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(fw);
            for( HashMap.Entry<String, Data> entry : space.varMap.entrySet())
                bw.write("make \""+entry.getKey()+" "+entry.getValue().getWord()+"\n");
            bw.close();
            fw.close();
        }catch(IOException e){

        }
        return name;
    }

    Data butlast(Data data){
        if(data.isList()){
            String s = data.getList();
            s = s.trim();
            int index = s.length()-1;
            while( index >= 0 && !(s.charAt(index) ==' ' && uu.judgeBrack(s.substring(index))))
                index--;
            return new Data("[" + s.substring(0,index) + "]");
        }
        return new Data(data.getWord().substring(0, data.getWord().length()-1));
    }
    Data butfirst(Data data){
        if(data.isList()){
            String s = data.getList();
            s = s.trim();
            int index = 0;
            while( index < s.length() && !(s.charAt(index) ==' ' && uu.judgeBrack(s.substring(0, index+1))))
                index++;
            return new Data("[" + s.substring(index) + "]");
        }
        return new Data(data.getWord().substring(1));
    }

    Data first(Data data){
        if(data.isList()){
            String s = data.getList();
            // System.out.println(s);
            int index = 1;
            while( index < s.length() && !(s.charAt(index) == ' ' && uu.judgeBrack(s.substring(0, index+1))))
                index++;
            return new Data(s.substring(0,index));
        }
        return new Data(data.getWord().substring(0, 1));
    }

    Data join(Data d1, Data d2){
        String s1 = d1.getList();
        String s2 = d2.getWord();
        return new Data("[" + s1 + " " + s2 + "]");
    }

    Data list(Data d1, Data d2){
        return new Data("[" + d1.getWord() + " " + d2.getWord() + "]");
    }

    Data sentence(Data d1, Data d2){
        String s1 = d1.getWord().trim();
        String s2 = d2.getWord().trim();

        if(s1.charAt(0) == '[') s1 = s1.substring(1, s1.length()-1);
        if(s2.charAt(0) == '[') s2 = s2.substring(1, s2.length()-1);
        return new Data("[" + s1 + " " + s2 + "]");
    }
    
    public int func(Stack<Data> dataStack, Vector<Data> arg){
        // for( Data temp : dataStack )
        //     System.out.println("dataStack"+temp.getWord());
        // for( Data temp : arg )
        //     System.out.println("arg"+temp.getWord());

        String fName = arg.lastElement().getWord();
        Data funName, funBody;
        
        String f = null;
        if( space.get(fName) != null  ){
            f = space.get(fName).getList();
        }else{
            for( VariableSpace temp : spaceStack)
                if( temp.get(fName) != null )
                    f = temp.get(fName).getList();
        }
        // System.out.println("f:"+f);

        arg.remove(arg.size()-1);
        Vector<String> funTmp = uu.splitInstruction(f);

        funName = new Data(funTmp.get(0));
        funBody = new Data(funTmp.get(1));

        VariableSpace funSpace;
        if( uu.isOperation(dataStack.peek().getWord()) )
            funSpace = new VariableSpace(space.fatherName, fName);
        else
            funSpace = new VariableSpace(space.fatherName, dataStack.peek().getWord());
        
        // System.out.println("father>>>>>>>>>>>>>");

        // if( space.fatherName != null )
        //     space.fatherName.printAll();
        // System.out.println(">>>>>>>>>>>>>");
        
        funTmp = uu.splitInstruction(funName.getList());
        
        for( String i : funTmp ){
            funSpace.input(i, arg.lastElement());
            arg.remove(arg.size()-1);
        }
        spaceStack.add(funSpace);
        // System.out.println("-------------" + funSpace.spaceName );
        // funSpace.printAll();
        // System.out.println("--------------");
        
        Operation funOperation = new Operation(funSpace, input, spaceStack);
        funOperation.funOutput = null;
        funOperation.runInstruction(funBody.getList());
        if(funOperation.funOutput != null ){
            dataStack.push(funOperation.funOutput);
            return 1;
        }else 
            return 0;
    }

    public void run(Data list){
        String s = list.getList();
        runInstruction(s);
    }

    public void ifOpt(boolean op, Data list1, Data list2 ){
        String s;
        if( op )
            s = list1.getList();
        else
            s = list2.getList();

        runInstruction(s);
        return ;
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
                    return new Data(value1.getNumber() == value2.getNumber());
                case "gt":
                    return new Data(value1.getNumber() > value2.getNumber());
                case "lt":
                    return new Data(value1.getNumber() < value2.getNumber());
            }
        } else {
            switch(op){
                case "eq":
                    return new Data(value1.getWord().equals(value2.getWord()));
                case "gt":
                    return new Data(value1.getWord().compareTo(value2.getWord()) > 0);
                case "lt":
                    return new Data(value1.getWord().compareTo(value2.getWord()) < 0);
            }
        }
        return new Data(true);
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
                if(value.isWord()){
                    // System.out.println("-------"+value.getWord());
                    return new Data(value.getWord().equals(""));
                }else if(value.isList()){
                    // System.out.println("-------"+value.getWord());
                    return new Data(value.getList().equals(""));
                }
        }
        return new Data(value.isWord());
    }

    public void erase(Data name){
        space.erase(name.getWord());
    }

}
