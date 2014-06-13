package symbolTable;

public class UintEntry extends TableEntry{

	
	public UintEntry(String lexeme){
		super(lexeme);
	}
	
	@Override
	public String getType() {
		return TableEntry.CONST_UINT;
	}
}
