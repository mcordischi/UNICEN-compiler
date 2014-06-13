package codeGenerator;

import reversePolish.*;

public class Variable implements AssemblerData {

	ReversePolishItem item;
	
	public Variable(ReversePolishItem item){
		this.item = item;
	}
	
	@Override
	public String getName() {
		return item.getValue();
	}

	@Override
	public String getType() {
		return item.getType();
	}
	
	
	public ReversePolishItem getItem(){
		return item;
	}

}
