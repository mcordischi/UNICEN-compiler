package codeGenerator;


public class Register implements AssemblerData{
	String name;
	boolean inUse;
	
	public Register(String name){
		this.name = name;
		inUse = false;
	}

	public String getName() {
		return name;
	}
	
	public String getType(){
		return AssemblerData.REG;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isFree(){
		return !(inUse) ;
	}
	
	public void setFree(){
		inUse = false;
	}
	
	public void setOccupy(){
		inUse = true;
	}
	
}
	/* DEPRECATED
	boolean modified;
	boolean inUse;
	String name;
	ReversePolishItem value;
	MemoryManager mm;
	
	public Register(){
		modified = false;
		inUse = true;
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public boolean isInUse() {
		return inUse;
	}

	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}
	
	public String getValue(){
		return value.getValue();
	}
	
	public void saveData(){
		//if (modified)
		//	mm.saveRegister(this);
	}
	
}*/
