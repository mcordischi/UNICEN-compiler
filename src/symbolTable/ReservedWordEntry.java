package symbolTable;

public class ReservedWordEntry extends TableEntry {

	public ReservedWordEntry(String lexeme){
		super(lexeme);
	}
	@Override
	public String getType() {
		return lexeme;
	}
	
	public boolean isReservedWord(){
	    return true;
		}


}
