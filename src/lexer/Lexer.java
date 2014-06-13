package lexer;

import event.Event;
import semanticAction.*;
import symbolTable.*;

public class Lexer {
	
	static int transitions[][]={{1,2,4,9,8,6,9,3,0,7,0},
								{1,1,9,9,9,9,9,9,9,9,9},
								{9,2,9,9,9,9,9,9,9,9,9},
								{3,3,3,3,3,3,3,9,3,3,3},
								{9,9,5,9,9,9,9,9,9,9,9},
								{5,5,5,5,5,5,5,5,5,5,0},
								{9,9,9,9,9,9,9,9,9,9,9},
								{9,9,9,9,9,9,9,9,9,9,9},
								{9,9,9,9,9,9,9,9,9,9,9}
								};
	
	SemanticAction actions[][];
	
	int filePosition ;
	int fileLine;
	boolean eof;
	String file;
	Event event;
	
	SymbolTable st;
	
	
	static final char LINE_BREAK = '\n';
	
	//INPUTS [][HERE]
	static final int INPUT_TYPES = 12;
	static final int NON_DIGIT_CHAR = 0 ;
	static final int DIGIT = 1;
	static final int ASTERISK = 2;
	static final int SIMPLE_PUNCTUATOR = 3;
	static final int LESS_THAN = 4 ;
	static final int GREATER_THAN = 5;
	static final int EQUALS = 6 ;
	static final int APOSTROPHE = 7;
	static final int BLANK = 8;
	static final int COLON = 9;
	static final int NEW_LINE = 10;
	static final int INVALID_CHAR = 11;
	
	//STATE [HERE][]
	static final int STATE_TYPES = 10;
	static final int INITIAL_STATE = 0;
	static final int IDENTIFIER_STATE = 1;
	static final int DIGIT_STATE = 2;
	static final int STRING_STATE = 3;
	static final int PRE_COMMENT_STATE = 4 ;
	static final int COMMENT_STATE = 5;
	static final int GREATER_COMPARATOR_STATE = 6;
	static final int ASSIGNMENT_STATE = 7;
	static final int LESS_COMPARATOR_STATE = 8 ;
	static final int FINAL_STATE = 9 ;

	public Lexer(SymbolTable st, Event eventHandler){
		
		this.st = st;
		this.event = eventHandler;
		
		filePosition=0;
		fileLine=1;
		eof = false;
		
		SemanticAction consumeInput = new ConsumeInput();
		SemanticAction inputBuffer = new InputBuffer();
		SemanticAction invalidAction = new InvalidAction();
		SemanticAction validateConstant = new ValidateConstant();
		SemanticAction validateIdentifier = new ValidateIdentifier();
		SemanticAction validateString = new ValidateString();
		SemanticAction validateSymbol = new ValidateSymbol();
		SemanticAction validateSymbolUnget = new ValidateSymbolUnget();
		
		actions = new SemanticAction[STATE_TYPES][INPUT_TYPES];
		
		for (int i=0;i<INPUT_TYPES;i++){
			actions[0][i]=inputBuffer;
			actions[1][i]=validateIdentifier;
			actions[2][i]=validateConstant;
			actions[3][i]=inputBuffer;
			actions[4][i]=validateSymbolUnget;
			actions[5][i]=consumeInput;
			actions[6][i]=validateSymbolUnget;
			actions[7][i]=invalidAction;
			actions[8][i]=validateSymbolUnget;
		}
		actions[0][3]=validateSymbol;
		actions[0][6]=validateSymbol;
		actions[0][8]=consumeInput;
		actions[1][0]=inputBuffer;
		actions[1][1]=inputBuffer;
		actions[2][1]=inputBuffer;
		actions[3][7]=validateString;
		actions[4][2]=consumeInput;
		actions[6][6]=validateSymbol;
		actions[7][6]=validateSymbol;
		actions[8][5]=validateSymbol;
		actions[8][6]=validateSymbol;
	}

	public void loadFile(String s){
		file = s;
	}
	
	//TODO repair lineCounter
	public TableEntry getToken(){
		if (file.length() <= filePosition) return null ;
		int state = INITIAL_STATE;
		int inputType =0;
		boolean finalState = false ;
		char nextChar = ' ';
		while(!finalState && !eof){
			//control conditions
			if (nextChar == LINE_BREAK)	fileLine++;
			if (state == INITIAL_STATE)	SemanticAction.resetBuffer();
			
			//System.out.print("State: " + state + " with " + SemanticAction.getBuffer());
			
			nextChar = file.charAt(filePosition);
			inputType = clasifyInput(nextChar);
			actions[state][inputType].run(nextChar);
			if (transitions[state][inputType] != FINAL_STATE)
				state = transitions[state][inputType];
			else finalState=true;
			filePosition++;
			
			if(file.length() <= filePosition) eof = true;
			
			//System.out.println(" ->" + SemanticAction.getBuffer());
		}
		
		
		if(finalState){
			return createEntry((ValidationAction)actions[state][inputType]);
		}

		if (eof) {
			event.eventWarning("Fin de Archivo");
			//System.out.println("Fin de archivo");
			//System.out.println(SemanticAction.getBuffer());
			//patch of unget
				filePosition++;
			switch(state){
				case INITIAL_STATE:
					return null ;
				case IDENTIFIER_STATE:
					return createEntry(new ValidateIdentifier());
				case DIGIT_STATE:
					return createEntry(new ValidateConstant());
				case GREATER_COMPARATOR_STATE:
					return createEntry(new ValidateSymbol());
				case ASSIGNMENT_STATE:
					return createEntry(new ValidateSymbol());
				case LESS_COMPARATOR_STATE:
					return createEntry(new ValidateSymbol());	
			}
			event.eventError("Fin inesperado de archivo", fileLine, "Lexer");
			return null ;
		}
		
		event.eventError("Estado Inesperado",fileLine,"Lexer");
		return null;

	}
	
	private TableEntry createEntry(ValidationAction action){
		String error = action.validate();
		if (error != null)
			event.eventError(error, fileLine, "Lexer");
		if (action.critical())
			return getToken();
		else{
			//System.out.println("Validating in state " +state+ " " + inputType );
			TableEntry newEntry = action.generateEntry();
			if (action.unget()) filePosition--;
			newEntry = st.addToken(newEntry);
			event.eventNewEntry(st, newEntry);
			return newEntry;
		}
		
	}
	
	public SymbolTable getSymbolTable(){
		return st;
	}

	private int clasifyInput(char c) {
		  if(c >= 'a' && c <= 'z')
	            return NON_DIGIT_CHAR;
	      if(c >= 'A' && c <= 'Z')
	            return NON_DIGIT_CHAR;
	      if(c >= '0' && c <= '9')
	            return DIGIT;
	        switch(c)
	        {
	        case 47: // '/'
	            return SIMPLE_PUNCTUATOR;

	        case 42: // '*'
	            return ASTERISK;
	            
	        case 39: // '\''
	        	return APOSTROPHE;
	        	
	        case 40: // '('
	            return SIMPLE_PUNCTUATOR;

	        case 41: // ')'
	            return SIMPLE_PUNCTUATOR;

	        case 123: // '{'
	            return SIMPLE_PUNCTUATOR;

	        case 125: // '}'
	            return SIMPLE_PUNCTUATOR;

	        case 43: // '+'
	            return SIMPLE_PUNCTUATOR;

	        case 45: // '-'
	            return SIMPLE_PUNCTUATOR;

	        case 59: // ';'
	            return SIMPLE_PUNCTUATOR;

	        case 44: // ','
	            return SIMPLE_PUNCTUATOR;
	            
	        case 94: // '^'
	        	return SIMPLE_PUNCTUATOR;

	        case 32: // ' '
	            return BLANK;

	        case 9: // '\t'
	            return BLANK;

	        case 10: // '\n'
	            return NEW_LINE;

	        case 13: // '\r'
	            return BLANK;

	        case 34: // '"'
	            return SIMPLE_PUNCTUATOR;

	        case 61: // '='
	            return EQUALS;

	        case 60: // '<'
	            return LESS_THAN;

	        case 62: // '>'
	            return GREATER_THAN;
	        
	        case 58: // ':'
	        	return COLON;
	        }
	        return INVALID_CHAR;
	    }
	
	public int getLineNumber(){
		return fileLine;
	}
	
	public Event getEvent(){
		return event;
	}
	
	public boolean endOfFile(){
		return eof;
	}
}
