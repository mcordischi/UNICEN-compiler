package reversePolish;

import java.util.Stack;
import java.util.Vector;

import symbolTable.*;
import event.Event;

public class ReversePolish {

	//TODO Line number data
	
	boolean recursion ; //DEPRECATED
//	boolean markNext ; DEPRECATED
	int functions;
	boolean pointerAssignment ; //PATCH
	Event event ;
	SymbolTable st ;
	Vector<ReversePolishItem> workingArray;
	Vector<ReversePolishItem> mainArray;
	Vector<ReversePolishItem> f1Array;
	Vector<ReversePolishItem> f2Array;
	Vector<IdentifierEntry> functionID;
	Stack<Integer> controlStack;
	Stack<ReversePolishItem> operationStack ;
	
	public static String COMMAND = "Command" ;
	//public static String POSITION = "Position" ; Deprecated, changed for LABEL
	public static String AUXVAR = "Auxiliar";
	public static String LABEL = "Label";
	public static String LABELDEF = "Label Definition";
	public static String VAR = TableEntry.IDENTIFIER;
	
	RPExtra jl = new RPExtra("JL",COMMAND);
	RPExtra jmp = new RPExtra("JMP",COMMAND);
	RPExtra jle = new RPExtra("JLE",COMMAND);
	RPExtra je = new RPExtra("JE",COMMAND);
	RPExtra jne = new RPExtra("JNE",COMMAND);
	RPExtra jae = new RPExtra("JAE",COMMAND);
	RPExtra ja = new RPExtra("JA",COMMAND);
	RPExtra ret = new RPExtra("RET",COMMAND);
	RPExtra call = new RPExtra("CALL",COMMAND);
	public static RPExtra rtn = new RPExtra("rtn",VAR);
	
	
	public ReversePolish(SymbolTable st, Event event){
		mainArray = new Vector<ReversePolishItem>();
		controlStack = new Stack<Integer>() ;
		operationStack = new Stack<ReversePolishItem>();
		functionID = new Vector<IdentifierEntry>() ;
		workingArray = mainArray;
		functions = 0 ;
		this.st = st ;
		this.event = event ;
		//markNext = false; DEPRECATED
		pointerAssignment = false;
	}
	
	
	/**
	 * Add new item to the Reverse Polish Working Array 
	 * @param lexeme
	 */
	public void addItem(String lex){
		TableEntry se = st.searchEntry(lex);
		if (!se.check())
			event.eventError("Variable No declarada: " + lex , 0, "Generador de código intermedio");
		RPPointer item = new RPPointer(se);
		workingArray.add(item);
		if ((se.getValueType()==TableEntry.FUNCTION_POINTER ||  se.getValueType()==TableEntry.FUNCTION_NAME)
				&& !pointerAssignment){
			/* Patch for Function pointer deprecated - Temporaly solved in CodeGenerator
			 * if (se.getValueType()==TableEntry.FUNCTION_POINTER){  //Fun Ptr Patch
				RPExtra item = new RPExtra ("[_" +se.getLexeme() + "]",LABEL);
				workingArray.add(item);
			}
			else{
				RPPointer item = new RPPointer(se);
				workingArray.add(item);
			}*/
			RPExtra auxVar = new RPExtra("AUX" + se.getLexeme(), VAR);
			TableEntry equals = st.searchEntry(":=");
			workingArray.add(call);
			workingArray.add(auxVar);
			workingArray.add(rtn);
			workingArray.add(new RPPointer(equals));
			workingArray.add(auxVar);
		}
	}
	
	/**
	 * Add new item to the Reverse Polish Working Array 
	 * @param Table entry
	 */
	public void addItem(TableEntry se){
		event.eventNewPolishItem(se.getLexeme());
		if (!se.check())
			event.eventError("Variable No declarada: " + se.getLexeme(), 0, "Generador de código intermedio");
		RPPointer item = new RPPointer(se);
		workingArray.add(item);
		if ((se.getValueType()==TableEntry.FUNCTION_POINTER ||  se.getValueType()==TableEntry.FUNCTION_NAME)
				&& !pointerAssignment){
			RPExtra auxVar = new RPExtra("AUX" + se.getLexeme(), VAR);
			TableEntry equals = st.searchEntry(":=");
			workingArray.add(call);
			workingArray.add(auxVar);
			workingArray.add(rtn);
			workingArray.add(new RPPointer(equals));
			workingArray.add(auxVar);
		}
	}
	
	/**
	 * Patch -- Only used when needed a return from function
	 * @param rpi
	 */
	public void addItem(ReversePolishItem rpi){
		workingArray.add(rpi);
	}
	
	/**
	 * DEPRECATED - Use addItem
	 * Adds a new item to the OperationStack (temporary stack)
	 * @param item
	 */
	public void addItemStack(String lex){
		TableEntry se = st.searchEntry(lex);
		if (!se.check())
			event.eventError("Variable No declarada: " + lex, 0, "Generador de código intermedio");
		RPPointer item = new RPPointer(se);
		operationStack.push(item);
	}
	
	/**
	 * DEPRECATED
	 * Does the proper work for the end of an operation:
	 * Flushes the OperationStack in the workingArray
	 */
	public void endOperationStack(){
		while (!operationStack.empty())
			workingArray.add(operationStack.pop());
	}
	
	
	/**
	 * Patch to solve pointer assignments
	 */
	public void setPtrAssigment(){
		pointerAssignment = true;
	}
	

	/**
	 * Patch to solve pointer assignments
	 */
	public void endPtrAssigment(){
		pointerAssignment = false;
	}
	
	/**
	 * Starts a function, change the workingArray and checks for inconsistencies
	 */
	public void startFunction(IdentifierEntry e){
		functionID.add(e);
		functions++;
		if (mainArray == f1Array)
			event.eventError("Función declarada dentro de otra" , 0, "Generador de código intermedio");
		if (functions > 2)
			event.eventError("Demasiadas funciones creadas", 0 , "Generador de código intermedio");
		if (functions == 1){
			f1Array = new Vector<ReversePolishItem>();
			workingArray = f1Array ;
		}
		if (functions == 2){
			f2Array = new Vector<ReversePolishItem>();
			workingArray = f2Array ;
		}
		RPExtra funHeader = new RPExtra(e.getLexeme(),LABELDEF);
		//funHeader.mark();
		workingArray.add(funHeader);		
	}
	
	/**
	 * Returns the workingArray to mainArray
	 */
	public void endFunction(){
		if (workingArray != mainArray){ //Patch
			TableEntry equals = st.searchEntry(":=");
			workingArray.add(new RPPointer(equals));
			workingArray.add(ret);
		}
		workingArray = mainArray;
	}
	
	/**
	 * Starts an If sentence.
	 * Stacks the branch for future position of label
	 * <cond> <branch False> ...  <exe> <Label> 
	 */
	public void startIf(){
		int position = workingArray.size();
		Integer i = new Integer(position);
		controlStack.push(i);
		RPExtra jumpPosition = new RPExtra("Not Defined",LABEL);
		//jumpPosition.mark();
		workingArray.add(jumpPosition);
		workingArray.add(getJumpType(workingArray.get(position-1)));
	}
	
	/**
	 * Ends last If stacked.
	 * Completes the <branch False> with the actual position.
	 * Marks the actual position
	 */
	public void endIf(){
		Integer position = controlStack.pop().intValue();
		Integer jumpDest = new Integer(getActualPosition());
		((RPExtra)workingArray.get(position)).setValue(jumpDest.toString());
		workingArray.add(new RPExtra(jumpDest.toString(),LABELDEF)); 
	}
	
	/**
	 * Starts the else sentence. 
	 * Adds to the array  Jump <execution>
	 * adds the position of jump of last IF
	 * No need of executing endElse
	 */
	public void startElse(){
		int position = workingArray.size();
		Integer i = new Integer(position);
		RPExtra jumpPosition = new RPExtra("Not Defined",LABEL);
		workingArray.add(jumpPosition);
		workingArray.add(getJumpType(workingArray.get(position-1)));
		endIf();
		controlStack.push(i);
	}
	
	/**
	 * Ends last else stacked
	 * Completes the <Unconditional Jump> with the actual position
	 * Marks the actual position
	 */
	public void endElse(){
		Integer position = controlStack.pop().intValue();
		Integer jumpDest = new Integer(getActualPosition());
		((RPExtra)workingArray.get(position)).setValue(jumpDest.toString());
		workingArray.add(new RPExtra(jumpDest.toString(),LABELDEF));
	}
	
	/**
	 * Adds to auxStack the actual position,
	 * Marks the position.
	 */
	public void startDo(){
		Integer jumpDest = new Integer(getActualPosition());
		workingArray.add(new RPExtra(jumpDest.toString(),LABELDEF));
		//int position = workingArray.size();
		controlStack.push(jumpDest);
	}
	
	/**
	 * Adds conditional branch to position stacked.
	 * <Label> <execution> <condition> <brach to label>
	 */
	public void endDo(){
		Integer position = controlStack.pop();
		RPExtra jumpPosition = new RPExtra(position.toString(),LABEL);
		workingArray.add(jumpPosition);
		workingArray.add(getJumpType(workingArray.get(workingArray.size()-2)));
		
	}
	
	public Vector<ReversePolishItem> getList(){
		Vector<ReversePolishItem> result = new Vector<ReversePolishItem>();
		if (f1Array != null)
			result.addAll(f1Array);
		if (f2Array != null)
			result.addAll(f2Array);
		result.addAll(mainArray);
		return result ;
	}
	

	
	@Override
	public String toString(){
		String result = new String("POLACA INVERSA:\n");
		result += "\nMain Array: \n";
		for(int i = 0; i<mainArray.size();i++){
			result += i + "\t" +mainArray.get(i).toString() + "\t" + mainArray.get(i).getType() ;
			if (mainArray.get(i).marked())
				result += "\tMarcado";
			result += "\n";
		}
		
		if (f1Array != null){
			result += "\nFunción 1 Array: \n";
			for(int i = 0; i<f1Array.size();i++){
				result += i + "\t" +f1Array.get(i).toString() ;
				if (f1Array.get(i).marked())
					result += "\tMarcado";
				result += "\n";
			}
		}
		
		if (f2Array != null){
			result += "\nFunción 2 Array: \n";
			for(int i = 0; i<f2Array.size();i++){
				result += i + "\t" +f2Array.get(i).toString() ;
				if (f2Array.get(i).marked())
					result += "\tMarcado";
				result += "\n";
			}
		}
		result += "\nFIN POLACA INVERSA\n";
		return result ;
	}
	
	static String[] logicOperators = {">",">=","<","<=","<>","=="};
	
	/**
	 * @param Operand item
	 * @return the jump type (carry,negative,equals)
	 */
	private ReversePolishItem getJumpType(ReversePolishItem item){
		int i = 0;
		while (!item.getValue().equals(logicOperators[i]))
		{i++;}
		switch (i){
		case 0: return jle; //>
		case 1: return jl; //>=
		case 2: return jae; // <
		case 3: return ja; // <=
		case 4: return je; // <>
		case 5: return jne; // == 
		}
		return null;
 
	}
	
	private int getActualPosition(){
		int result = 0 ;
		if (f1Array != null){
			result+=f1Array.size();
			if(workingArray == f1Array)
				return result;
		}
		
		if (f2Array != null){
			result+=f2Array.size();
			if(workingArray == f2Array)
				return result;
		}
		
		if (mainArray != null)
			result+=mainArray.size();
		return result;
		
	}
}
