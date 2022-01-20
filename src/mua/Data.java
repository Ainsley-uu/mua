package src.mua;

public class Data {
    private String value;
    private boolean isword = false;

    public Data( String value ){
        this.value = value;
        if( value.charAt(0) == '"' ){
            isword = true;
            this.value = this.value.substring(1); 
        }else isword = false;
    }

    public Data( Data data ){
        value = data.value;
    }

    public Data( double value ){
        this.value = Double.toString(value);
    }

    public Data( boolean value ){
        if( value ){
            this.value = "true";
        }else{
            this.value = "false";
        }
    }

    public boolean isWord(){
        return isword;
    }

    public boolean isNumber(){
        try{
            Double.valueOf(value);
            return true;
        }catch( Exception e ){
            return false;
        }
    }

    public boolean isBool(){
        if( value.equals("true") || value.equals("false") )
            return true;
        else
            return false;
    }

    public boolean isList(){
        value = value.trim();
        return (value.charAt(0) == '[' && value.charAt(value.length() - 1) == ']');
    }
    
    public String getWord(){
        return value;
    }

    public boolean getBool(){
        return value.equals("true");
    }

    public String getOperation(){
        return value;
    }

    public String getList(){
        return this.value.substring(1, value.length()-1);
    }

    public double getNumber(){
        return Double.valueOf(value);
    }

}
