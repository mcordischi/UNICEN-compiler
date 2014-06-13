package reversePolish;

public abstract class ReversePolishItem {
	
	private boolean marked = false; //DEPRECATED
	
	public abstract String getType();
	public abstract String getValue(); 
	public abstract void setValue(Object obj);
	public void mark(){ marked = true; } //DEPRECATED
	public boolean marked(){return marked;} //DEPRECATED
	public String getValueType(){return null;}

	public boolean equals(Object other){
		ReversePolishItem o = (ReversePolishItem) other;
		if (getType().equals(o.getType()) && getValue().equals(o.getValue()))
			return true;
		return false;
	}
}
