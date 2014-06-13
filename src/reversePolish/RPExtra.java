package reversePolish;


public class RPExtra extends ReversePolishItem {
	
	String value;
	String type;
	
	public RPExtra(String value,String type){
		this.value = value;
		this.type = type;
	}
	
	public RPExtra(String value){
		this.value = value;
		this.type = "COMPLETE";
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String toString(){
		return value ;
	}

	@Override
	public void setValue(Object obj) {
		this.value = (String)obj;
	}
}
