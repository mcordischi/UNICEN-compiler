package semanticAction;

import symbolTable.*;


public class ValidateIdentifier extends ValidationAction {
	
	static int MAXIDLENGTH = 12;

	@Override
	public String validate() {
		if (buffer.length() < MAXIDLENGTH){
			critical = false;
			return null;
		}
		critical = false;
		buffer = buffer.substring(0, MAXIDLENGTH-1);
		return "Identificador demasiado largo, truncado";
	}

	@Override
	public TableEntry generateEntry() {
		IdentifierEntry result = new IdentifierEntry(buffer);
		return result;
	}

	@Override
	public boolean unget() {
		return true;
	}

	@Override
	public void run(char c) {
		// TODO Auto-generated method stub
		
	}
}
