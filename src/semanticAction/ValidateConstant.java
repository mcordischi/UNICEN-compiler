package semanticAction;

import symbolTable.*;

public class ValidateConstant extends ValidationAction {

	public static int MAXVALUE = 65535; 
	
	@Override
	public String validate() {
		if (buffer.length() < 6){
			Integer uint = new Integer(buffer);
			if (MAXVALUE > uint.intValue() ){
				critical = false;
				return null;
			}
		}
		critical = true ; 
		return "Valor fuera de rango";
	}

	@Override
	public TableEntry generateEntry() {
		UintEntry result = new UintEntry(buffer);
		return result ;
	}

	@Override
	public boolean unget() {
		return true;
	}

	@Override
	public void run(char c) {
	}

}
