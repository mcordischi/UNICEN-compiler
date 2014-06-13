package semanticAction;

import symbolTable.SymbolEntry;
import symbolTable.TableEntry;

public class ValidateSymbolUnget extends ValidationAction {

	@Override
	public String validate() {
		critical = false;
		return null;
	}

	@Override
	public TableEntry generateEntry() {
		SymbolEntry result = new SymbolEntry(buffer);
		return result;
	}

	@Override
	public boolean unget() {
		return true;
	}

	@Override
	public void run(char c) {	
	}

	
}
