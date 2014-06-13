package symbolTable;

public abstract class TableEntry {
	
	protected String lexeme;
	
	public static String CONST_STRING = "Str";
    public static String CONST_UINT = "Uint";
    public static String IDENTIFIER = "ID";
    public static String VARIABLE = "Variable";
    public static String FUNCTION_NAME = "Funcion";
    public static String FUNCTION_POINTER = "Ptr Funcion";
    public static String RESERVED_WORD = "Reservada";
    //public static String SYMBOL = "symbol";
    
    public TableEntry(){}
    
    public TableEntry(String lexeme){
    	this.lexeme=lexeme;
    }
    	
	public abstract String getType();

	public int hashCode(){
        return lexeme.hashCode();
	}

	public boolean equals(Object obj){
	  TableEntry aux = (TableEntry)obj;
	  return aux.lexeme.equals(lexeme);
    }

	public boolean isReservedWord(){
    return false;
	}

	public String getLexeme(){
        return lexeme;
    }

    public void setLexeme(String lexeme)
    {
        this.lexeme = lexeme;
	}
    
	public String toString(){
		return this.getType() + "\t" + this.lexeme ; 
	}
	
	public boolean check(){
		return true;
	}
	
	public String getValueType(){
		return null;
	}


	
}
