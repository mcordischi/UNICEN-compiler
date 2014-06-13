package event;

import symbolTable.*;

public class UserInterface implements Event {

	public boolean error = false;
	@Override
	public void eventNewToken(TableEntry newEntry ) {
		//System.out.println("Nuevo Token: "+ newEntry.getType() + " " +newEntry.getLexeme().toString());
	}

	@Override
	public void eventWarning(String s) {
		System.out.println("Warning: " + s);

	}

	@Override
	public void eventError(String s, int line, String origin) {
		System.out.println("Error:" + s + ". Generado en línea " + line + ". Informa: " + origin);
		error = true ;
	}

	@Override
	public void eventNewEntry(SymbolTable st, TableEntry newEntry) {
		//System.out.println("Nueva entrada en tabla de símbolos: " + newEntry.getLexeme());

	}

	@Override
	public void eventNewRule(String rule, int line) {
		//System.out.println("Línea " + line + ", estructura sintáctica: " + rule);

	}

	@Override
	public void eventNewPolishItem(String item) {
		//System.out.println("Nuevo item en polaca: " + item);
	}

}
