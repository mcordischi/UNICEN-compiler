package semanticAction;

import symbolTable.*;

public abstract class ValidationAction extends SemanticAction {

	boolean critical;
	
	public abstract String validate();
	
	public abstract TableEntry generateEntry();

	public boolean critical(){return critical;}
}
