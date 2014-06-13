package symbolTable;

public class SymbolEntry extends TableEntry {

	public SymbolEntry(String lexeme) {
		super(lexeme);
	}

	@Override
	public String getType() {
		return lexeme;
	}


}
