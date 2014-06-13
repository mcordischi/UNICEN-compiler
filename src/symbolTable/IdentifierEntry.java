package symbolTable;

public class IdentifierEntry extends TableEntry {
	
	String valueType ;
	boolean declared;
	ReservedWordEntry returnType; //Only for functions!
	
	
	public IdentifierEntry(String lexeme){
		super(lexeme);
		declared = false ;
	}

	@Override
	public String getType() {
		return TableEntry.IDENTIFIER;
	}
	
	@Override
	public String getValueType(){
		return valueType;
	}
	
	public void setValueType(String str){
		this.valueType = str;
	}
	
	
	public void setReturnType(TableEntry entry){
		returnType = (ReservedWordEntry)entry;
	}
	
	public void declare(){
		declared = true ;
	}
	
	@Override
	public boolean check(){
		return declared ;
	}
	
	public String toString(){
		return super.toString() + "\t" + valueType + "\t" + declared;
	}
	

}
