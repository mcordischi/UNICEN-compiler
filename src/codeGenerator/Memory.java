package codeGenerator;

import reversePolish.ReversePolishItem;

public class Memory implements AssemblerData {

	String name;
	ReversePolishItem item ;
	
	public Memory(ReversePolishItem item){
		this.name = "_" + item;
		name.replace(" ", "_").toUpperCase();
		this.item = item ;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getType() {
		return AssemblerData.MEM;
	}
	
	public ReversePolishItem getItem(){
		return item;
	}

}
