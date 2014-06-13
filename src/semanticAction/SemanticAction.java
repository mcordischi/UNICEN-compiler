package semanticAction;

public abstract class SemanticAction {
	
	public SemanticAction(){}
	
	public abstract void run(char c);
	
	public static void resetBuffer(){
		buffer = "";
	}
	
	public abstract boolean unget();
	
	public static String getBuffer(){
		return buffer;
	}
	
	static String buffer; 

}
