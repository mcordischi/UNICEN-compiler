package symbolTable;

public class StringEntry extends TableEntry {
	
	public StringEntry(String lexeme){
		super(lexeme);
	}

	@Override
	public String getType() {
		return TableEntry.CONST_STRING;
	}

}
