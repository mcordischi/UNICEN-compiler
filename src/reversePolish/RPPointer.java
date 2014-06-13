package reversePolish;

import symbolTable.TableEntry;

public class RPPointer extends ReversePolishItem {

	TableEntry value;
	
	public RPPointer(TableEntry value){
		this.value = value ;
	}
	@Override
	public String getType() {
		return value.getType();
	}

	@Override
	public String getValue() {
		return value.getLexeme();
	}

	@Override
	public String toString(){
		return value.getLexeme();
	}
	@Override
	public void setValue(Object obj) {
		this.value = (TableEntry)obj;
	}
	
	@Override
	public String getValueType(){
		return value.getValueType();
	}
	
}
