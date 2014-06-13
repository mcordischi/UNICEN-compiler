package semanticAction;

import symbolTable.*;

public class ValidateString extends ValidationAction {

	@Override
	public String validate() {
		String regex = "\\\\ *\n *\\\\";
		buffer = buffer.replaceAll(regex, "");
		if (buffer.contains("\n")){
			critical = true;
			return "String Multilínea con mal formato";
		}
		critical = false;
		return null;
	}

	@Override
	public TableEntry generateEntry() {
		StringEntry result = new StringEntry(buffer);
		return result;
	}

	@Override
	public boolean unget() {
		return false;
	}

	@Override
	public void run(char c) {
		buffer+= c;
	}
}
