package symbolTable;

import java.util.Vector;



public class SymbolTable {
	
	Vector<TableEntry> table;

	public SymbolTable(){
		table = new Vector<TableEntry>();
		TableEntry init[] = {
				new ReservedWordEntry("if"), new ReservedWordEntry("else"), new ReservedWordEntry("fun"),
				new ReservedWordEntry("uint"), new ReservedWordEntry("return"), new ReservedWordEntry("do"),
				new ReservedWordEntry("until"), new ReservedWordEntry("ui"), new ReservedWordEntry("print"),
				new ReservedWordEntry("then"),
				new SymbolEntry(">"),new SymbolEntry("<"),new SymbolEntry(">="),
				new SymbolEntry("<"),new SymbolEntry("<="),new SymbolEntry("<>"),new SymbolEntry(":="),
				new SymbolEntry("^")
		};
		for(TableEntry t : init){
			table.add(t);
		}
	}
	
	
	public TableEntry addToken(TableEntry t){
		int i;
		if((i=this.searchToken(t)) >= 0)
			return table.get(i);
		table.add(t);
		return t ;
	}
	
	
	protected int searchToken(TableEntry t){
		//System.out.println("Comparing " + t.getLexeme() + "...");
		for(int i=0; i<table.size(); i++){
			//System.out.println("comp " + i + " with " + table.elementAt(i).toString());
			if (table.elementAt(i).equals(t))
				return i;
		}
		return -1;
	}


	
	public TableEntry getEntry(int i){
		return table.elementAt(i);
	}
	
	public TableEntry searchEntry(String lex){
		for(TableEntry entry : table)
			if (entry.getLexeme().equals(lex))
				return entry ;
		return null;
	}
		
	public String toString(){
		String result = "\n-----------\nTABLA DE SÍMBOLOS\n-----------\nN\ttype\tvalue\tTypeValue\tdeclared\n\n";
		for (int i=0 ; i<table.size();i++){
			result += i + "\t" + table.elementAt(i).toString() + "\n";
		}
		return result;
	}
}
