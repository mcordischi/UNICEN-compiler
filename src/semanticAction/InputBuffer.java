package semanticAction;

public class InputBuffer extends SemanticAction {

	@Override
	public void run(char newChar) {
		buffer += newChar;
	}
	
	@Override
	public boolean unget() {
		return false;
	}

}
