package src.mua;

import java.util.TreeMap;

public class VariableSpace {
    public TreeMap<String, Data> varMap = new TreeMap<>();
    public String spaceName = "global";
    VariableSpace fatherName;

    VariableSpace( VariableSpace father ){
        fatherName = father;
        if( father == null ){
            fatherName = this;
        }
    }

    VariableSpace( VariableSpace father, String name ){
        fatherName = father;
        if( father == null ){
            fatherName = this;
        }
        spaceName = name;
    }

    VariableSpace(){
        fatherName = this;
        varMap.put("pi", new Data(3.14159));
        // varMap.put("run",new Data("[ [x] [if true :x [] ] ]"));
    }

    public boolean hasName(String name){
        if( varMap.containsKey(name))
            return true;
        if( fatherName == this )
            return false;
        return fatherName.hasName(name);
    }

    public void input( String name, Data value){
        varMap.put(name, value);
    }

    public void erase(String name){
        varMap.remove(name);
    }

    public Data get(String name){
        if( varMap.containsKey(name)){
            return varMap.get(name);
        }
        if(fatherName == this)
            return null;
        return fatherName.get(name);
    }

    public void removeAll(){
        varMap.clear();
    }
    
    public void printAll(){
        for( String s: varMap.keySet() ){
            System.out.println(s);
            System.out.println(varMap.get(s).getWord());
        }
    }

    public void export(Data data){
        if( fatherName != this ){
            String name = data.getWord();
            fatherName.varMap.put(name, varMap.get(name));
        }
    }

    // public void findFather(Data data){
    //     if( fatherName != this ){
    //         return fatherName.
    //     }
    // }
}
