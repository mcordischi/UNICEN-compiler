package codeGenerator;


import java.util.Vector;


import event.Event;

import reversePolish.*;
import symbolTable.TableEntry;





public class MemoryManager {
	static int REGQ = 4 ; //only used unsigned integers from 0 to 2^16
	Event event;
	Vector<Memory> memory;
	Vector<String> program;
	Vector<Register> regMem;
	
	public MemoryManager(Vector<String> program,Event event){
		memory = new Vector<Memory>();
		regMem = new Vector<Register>();
		regMem.add(new Register("BX"));regMem.add(new Register("CX"));
		regMem.add(new Register("DX"));regMem.add(new Register("AX"));
		this.event = event;
		this.program = program;
		
	}
	
	/**
	 * Obtains the item from memory
	 * @param item
	 * @return the name of the memory container
	 */
	public Memory getMemory(Variable item){
		for(Memory mem: memory){
			if (mem.getItem().equals(item.getItem()))
				return mem;
		}
		return setMemory(item);
	}
	
	/**
	 * Creates a new memory container
	 * @param item
	 */
	public Memory setMemory(Variable item){
		Memory newMem = new Memory(item.getItem());
		memory.add(newMem);
		return newMem;
	}
	
	/**
	 * Gets from register or memory
	 * @param item
	 * @return a register or memory
	 */
	public AssemblerData getRegisterOrMem(AssemblerData item){
		if (item.getType().equals(AssemblerData.REG))
			return item;
		return getMemory((Variable)item);
	}
	
	/**
	 * Gets from immediate, register or memory 
	 * @param item
	 * @return immediate,register or memory
	 */
	public AssemblerData getRegisterOrMemOrIm(AssemblerData item){
		if (item.getType().equals(AssemblerData.REG))
			return item;
		if (item.getType().equals(TableEntry.CONST_UINT))
				return item;
		return getRegisterOrMem(item);
	}
	

	
	/**
	 * Returns the register. If it does not exist, it takes it from memory
	 * @param item
	 * @return register
	 */
	public Register getRegister(AssemblerData item){
		if (item.getType().equals(AssemblerData.REG))
			return (Register)item;
		return setRegister(item);
	}
	
	
	/**
	 * Assigns a new register to the info, loads to the program the operation needed
	 * @param item
	 * @return
	 */
	public Register setRegister(AssemblerData item){
		return setRegister(item,getFreeReg());
	}
		
	/**
	 * Assigns a specific register to the info, loads to the program the operation needed
	 * @param item the the item
	 * @param reg the new register
	 * @return the reg name
	 */
	public Register setRegister(AssemblerData item, Register reg){
		if (!reg.isFree())
			moveRegister(reg);
		Memory mem = getMemory((Variable)item);
		program.add("MOV " + reg.getName() + ", " + mem.getName() );	
		reg.setOccupy();
		return reg;
	}
	
	
	/**
	 * Assigns a specific register to the info, loads to the program the operation needed
	 * @param item the item
	 * @param reg the new register
	 * @return the reg name
	 */
	public Register setRegister(AssemblerData item, String regName){
		for (Register reg : regMem){
			if (regName.equals(reg.getName())){
				return setRegister(item,reg);
			}
		}
		return null;
	}
	
	private void moveRegister(Register r){
		Register newPlace = getFreeReg();
		r.setFree();
		program.add("MOV " + newPlace.getName() + ", " + r.getName());
	}
	
	/**
	 * Gets a free register
	 * @return the free register
	 */
	public Register getFreeReg(){
		for (Register r : regMem)
			if (r.isFree()){
				return r;
			}
		return null;
	}
	

	
	public void freeRegister(AssemblerData item){
		if(item.getType().equals(AssemblerData.REG))
				((Register)item).setFree();
	}
	
	public void saveRegister(Register reg,Variable item){
		Memory mem = getMemory(item);
		program.add("MOV " + mem.getName() + ", " + reg.getName() );
	}
	
	public void saveRegister(String r,Variable item){
		for (Register reg : regMem)
			if (r.equals(reg.getName()))
				saveRegister(reg,item);
	}
	
	/**
	 * Return the declaration of variables
	 * @return
	 */
	public Vector<String> getDeclarations(){
		Vector<String> result = new Vector<String>();
		for (Memory mem: memory) {
			ReversePolishItem variable = mem.getItem();
			if ((variable.getValueType()==TableEntry.CONST_STRING) || (variable.getValueType()==TableEntry.FUNCTION_NAME ))
				result.add(mem.getName() + " DB " + variable.getValue());
			else if (variable.getValueType()==TableEntry.FUNCTION_POINTER)
				result.add(mem.getName() + " DB NULLPTR") ;
			else
				result.add(mem.getName() + " DB ?") ;
				
		}
		return result;
	}
	
	
}
