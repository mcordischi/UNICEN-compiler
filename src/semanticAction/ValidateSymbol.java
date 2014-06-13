package semanticAction;

import symbolTable.*;


public class ValidateSymbol extends ValidationAction {

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
		return false;
	}

	@Override
	public void run(char c) {	
		buffer+= c ;
	}

}
