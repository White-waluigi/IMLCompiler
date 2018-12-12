package imlcompiler.Scanner;

import com.sun.org.apache.bcel.internal.generic.Instruction;

public class Token {
	public static class Attribute{}
	public static class IdentAttribute extends Attribute{
		String value;	
		public IdentAttribute(String value) {
			super();
			this.value = value;
		}
		public String toString(){
			return "\""+value+"\"";
		}
	};
	public static class IntAttribute extends Attribute{
		int value;
		public IntAttribute(int value) {
			super();
			this.value = value;
		}
		public String toString(){
			return "INT: "+value;
		}
	};
	public static class OtherAttribute extends Attribute{
		EnumAttribute value;
		public OtherAttribute(EnumAttribute value) {
		
			super();
			this.value = value;
		}
		public String toString(){
			return value.toString();
		}
	};
	
	Terminal terminal;
	public Terminal getTerminal() {
		return terminal;
	}


	public Attribute getAttribute() {
		return attribute;
	}


	public String getDebugString() {
		return debugString;
	}
	Attribute attribute;
	public String debugString;
	
	public Token(Terminal terminal, Attribute attribute, String debugString) {
		super();
		this.terminal = terminal;
		this.attribute = attribute;
		this.debugString = debugString;
	}
	
	
	public Token(Terminal sentinel) {
		this.terminal=sentinel;
		
	}


	public String toString() {

    	return String.format("%-20s%-20s%-30s", debugString+"", terminal.toString(), attribute==null?"":attribute+"");

	}

	public static Attribute getAttributeEnum(String attr) {
		if(attr==null) {
			return null;
		}
		return new OtherAttribute(EnumAttribute.valueOf(attr));
	};
	public enum Terminal{
		LPAREN,
		RPAREN,
		COMMA,
		SEMICOLON,
		COLON,
		BECOMES,
		MULTOPR,
		ADDOPR,
		RELOPR,
		BOOLOPR,
		TYPE,
		CALL,
		CHANGEMODE,
		MECHMODE,
		DEBUGIN,
		DEBUGOUT,
		DO,
		ELSE,
		ENDFUN,
		ENDIF,
		ENDPROC,
		ENDPROGRAM,
		ENDWHILE,
		LITERAL,
		FUN,
		GLOBAL,
		IF,
		FLOWMODE,
		INIT,
		LOCAL,
		NOTOPR,
		PROC,
		PROGRAM,
		RETURNS,
		SKIP,
		THEN,
		WHILE, 
		IDENT,
		SENTINEL,
		TUP, RBRACK, LBRACK
	};
	public enum EnumAttribute{
		TIMES,
		PLUS,
		MINUS,
	 
		EQ,
		NE,
		LT,
		GT,
		LE,
		GE,
		
		AND,
		OR,
		CAND,
		COR,
		
		BOOL,

		INT32,
		INT64,

		CONST,
		COPY,
		
		DIV_E,
		
		IN,
		INOUT,
		
		MOD_E,
		
		OUT,
		REF,
		VAR,
		
		TRUE,
		FALSE
	}
	public boolean is(Terminal sentinel) {
		return this.terminal==sentinel;
	}
	public boolean has(EnumAttribute attr) {
		if(!(attribute instanceof OtherAttribute)) {
			return false;
		}
		return ((OtherAttribute)this.attribute).value==attr;
	}
	public boolean isInfixOperator() {




		
		switch (terminal) {
		case MULTOPR:
		case ADDOPR:
		case RELOPR:
		case BOOLOPR:
			
		return true;

		default:return false;
		}
	}
	public boolean isOperator() {
		return isInfixOperator()||isPrefixOperator();
	}


	private boolean isPrefixOperator() {
		switch (terminal) {
		case TUP:
		case INIT:
		case BECOMES:
		case DEBUGIN:
		case DEBUGOUT:

			
		return true;

		default:return false;
		}
	}


	public String toShortString() {
		return terminal.toString();
	}

}

