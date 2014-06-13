package semanticAction;

import symbolTable.*;

public class InvalidAction extends ValidationAction {

	@Override
	public String validate() {
		// TODO Auto-generated method stub
		return "Lexema no reconocido";
	}

	@Override
	public TableEntry generateEntry() {
		// TODO Auto-generated method stub
		return null;
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
