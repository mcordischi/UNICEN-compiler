package codeGenerator;

import java.util.Stack;
import java.util.Vector;

import event.Event;

import reversePolish.*;
import symbolTable.*;

public class CodeGenerator {


	static String[] simpleOperators = {"RET"};
	static String[] unaryOperators = {"print","CALL","JMP","JL","JLE","JE","JNE","JAE","JA"};
	static String[] binaryOperators = {":=","+","-","*","/",">",">=","<","<=","<>"};
	
	
	Vector<ReversePolishItem> rpv ;
	Stack<AssemblerData> stack;
	Vector<String> program;
 
	int position; //position in the vector
	Event event;
	MemoryManager mm ;
	
	public CodeGenerator(ReversePolish rp,MemoryManager mm,Event event,Vector<String> program){
		rpv = rp.getList();
		position = 0 ; 
		this.event=event;
		this.mm = mm ;
		stack = new Stack<AssemblerData>() ;
		this.program = program;
	}
	
	/**
	 * Main thread of execution
	 * @return a vector of assembler instructions;
	 */
	public void run(){
		ReversePolishItem item = getNextItem();
		do{
			if (item.getType() == ReversePolish.LABELDEF){
				program.add(item.getValue() + ":");
			}else{
			//if (item.marked())
			//	program.add("LABEL " + (position-1));
			String type = item.getType();
			if (type.equals(TableEntry.IDENTIFIER) || type.equals(TableEntry.CONST_UINT) ||
					type.equals(TableEntry.CONST_STRING) || type.equals(ReversePolish.LABEL)){
				//Stack item
				stack.push(new Variable(item));
			}
			else
				execute(item);
			}
			item = getNextItem();
		}while (item != null);
	}
	
	/**
	 * Translates an operation into assembler
	 * @param item
	 */
	private void execute(ReversePolishItem item){
		int operation;
		String operator = item.getValue();
		if ((operation = getOperatorPosition(simpleOperators,operator)) >=0 ){
			//Simple operation
			switch (operation){
			case 0 : //ret
				program.add("RET");
			break;
			}
		} else 
		if ((operation = getOperatorPosition(unaryOperators,operator)) >=0 ){
			//unary operation
			AssemblerData operand;
			switch (operation){
			case 0: //print
				operand = stack.pop();
				Memory memory = mm.getMemory((Variable)operand);
				program.add("invoke MessageBox, NULL, addr \"ventana\", addr " + memory.getName() +", MB_OK");
				break;
			case 1: //CALL
				operand = stack.pop(); //Patch
				if (((Variable)operand).getItem().getValueType() == TableEntry.FUNCTION_POINTER)
					program.add(operator+" [_"+operand.getName() + "]");
				else
					program.add(operator+" "+operand.getName());
				break;
			default: //all the operations have the same behavior
				operand = stack.pop();
				program.add(operator+" "+operand.getName());
				break;
			}
		} else
		if ((operation = getOperatorPosition(binaryOperators,operator)) >=0 ){
			//binary operation
			AssemblerData operand2 = stack.pop();
			AssemblerData operand1 = stack.pop();
			if (operand1 == null)
				event.eventError("Pila de operadores vacía", 0, "Generador de código");
			AssemblerData newReg,r1,r2;
			switch (operation){			
			case 0 : // :=
			/*	if (operand1.getType() == TableEntry.FUNCTION_POINTER){
					program.add("MOV EAX, " + operand2.getName());
					program.add("MOV " + operand1.getName() + ", EAX");
					stack.removeAllElements(); //Patch
				}else{*/
					r1 = mm.getMemory((Variable)operand1);
					r2 = mm.getRegisterOrMemOrIm(operand2);
					program.add("MOV " + r1.getName() + ", " + r2.getName());
					mm.freeRegister(r2);
				/*}*/
				break;
			case 1 : // +
				r1 = mm.getRegister(operand1);
				r2 = mm.getRegisterOrMemOrIm(operand2);
				program.add("ADD " + r1.getName() + ", " + r2.getName());
				mm.freeRegister(r2);
				stack.push(r1);
				break;
			case 2 : // -
				r1 = mm.getRegister(operand1);
				r2 = mm.getRegisterOrMemOrIm(operand2);
				program.add("SUB " + r1.getName() + ", " + r2.getName());
				mm.freeRegister(r2);
				stack.push(r1);
				break;
			case 3 : // *
				r1 = mm.setRegister(operand1,"AX");
				r2 = mm.getRegisterOrMem(operand2);
				program.add("MUL " + r1.getName() + ", " + r2.getName());
				mm.freeRegister(r2);
				newReg = mm.getFreeReg();
				((Register)newReg).setOccupy();  //Patch
				program.add("MOV " + newReg.getName() + ", " + "AX" );
				mm.freeRegister(r1);
				stack.push(newReg);
				break;
			case 4 : // /
				r1 = mm.setRegister(operand1,"AX");
				r2 = mm.getRegisterOrMem(operand2);
				program.add("CMP "+ r2.getName() +", 0");
				program.add("JZ DIVZERO");
				program.add("DIV "+ r2.getName());
				mm.freeRegister(r2);
				newReg = mm.getFreeReg();
				((Register)newReg).setOccupy(); //Patch
				program.add("MOV " + newReg.getName() + ", " + r1.getName());
				mm.freeRegister(r1);
				stack.push(newReg);
				break;
			default: // Logic
				r1 = mm.getRegisterOrMem(operand1);
				r2 = mm.getRegisterOrMemOrIm(operand2);
				program.add("CMP " + r1.getName() + ", " + r2.getName());
				mm.freeRegister(r2);
				mm.freeRegister(r1);
			    break;
			}
		}
	}
		
	/**
	 * Gets the next item in the Reverse Polish List
	 * TODO Improve performance
	 * @return a RPItem
	 */
	private ReversePolishItem getNextItem(){
		if (position >= rpv.size()) return null;
		position++;
		return rpv.get(position-1);
			
	}
	
	private int getOperatorPosition(String[] array, String match){
		for (int i=0;i<array.length;i++)
			if (match.equals(array[i]))
				return i;
			return -1;
	}
	
	/**
	 * 
	 * @return the program made by the {@link #run()} execution
	 */
	public Vector<String> getProgram(){
		return program;
	}

}
