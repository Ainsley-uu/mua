package src.mua;

import java.util.TreeMap;
import javax.xml.stream.events.Namespace;

public class VariableSpace {
    public TreeMap<String, Data> varMap = new TreeMap<>();
    VariableSpace fatherName;

    VariableSpace( VariableSpace father ){
        fatherName = father;
        if( father == null ){
            fatherName = this;
        }
    }

    VariableSpace(){
        fatherName = this;
        varMap.put("pi", new Data(3.14159));
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

    public void remove(String name){
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
        }
    }
}
