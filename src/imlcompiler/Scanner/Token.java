package imlcompiler.Scanner;

import imlcompiler.CompileErrorException;

public class Token {
	public int position=-1;
	public int line=-1;
	public String debugString;

	public Token(String s) {
		debugString = s;
	}
	public String toString() {
    	return String.format("%-25s%-25s%s%n", this.getClass().getSimpleName(),"\""+debugString+"\"",line+":"+position+",");

	}
	public static class IdentfierToken extends Token {

		public IdentfierToken(String s) {
			super(s);
			this.name = s;
		}

		String name;

	}

	public static class LiteralToken extends Token {

		public LiteralToken(String s) {
			super(s);
			this.value = s;
		}

		String value;

	}

	public static class ChangeModeToken extends Token {
		public static enum Lexeme {
			VAR, CONST
		};

		Lexeme mode;

		public ChangeModeToken(String s) {
			super(s);
			switch (s) {
			case "var":
				mode = Lexeme.VAR;
				break;
			case "const":
				mode = Lexeme.CONST;
				break;
			default:
				throw new CompileErrorException("Unrecognized Changemode Token:"+"\""+s+"\"\n");
			}
		}

	}

	public static class FlowModeToken extends Token {
		public FlowModeToken(String s) {
			super(s);
			switch (s) {
			case "in":
				mode = Lexeme.IN;
				break;
			case "out":
				mode = Lexeme.OUT;
				break;
			case "inout":
				mode = Lexeme.INOUT;
				break;
			default:
				throw new CompileErrorException("Unrecognized Flowmode Token:"+"\""+s+"\"\n");
			}
		}

		public static enum Lexeme {
			IN, OUT, INOUT
		};

		Lexeme mode;
	}

	public static class MechModeToken extends Token {
		public MechModeToken(String s) {
			super(s);
			switch (s) {
			case "copy":
				mode = Lexeme.COPY;
				break;
			case "ref":
				mode = Lexeme.REF;
				break;
			default:
				throw new CompileErrorException("Unrecognized Mechmode Token:"+"\""+s+"\"\n");
			}
		}

		public static enum Lexeme {
			COPY, REF
		};

		Lexeme mode;
	}

	public static class IntsOperatorToken extends Token {
		public IntsOperatorToken(String s) {
			super(s);
			switch (s) {
			case "*":
				op = Lexeme.MUL;
				break;
			case "+":
				op = Lexeme.PLUS;
				break;
			case "-":
				op = Lexeme.MINUS;
				break;
			case "divE":
				op = Lexeme.DIVE;
				break;
			default:
				throw new CompileErrorException("Unrecognized IntsOperator Token:"+"\""+s+"\"\n");
			}
		}

		public static enum Lexeme {
			MUL, PLUS, MINUS, DIV, DIVE
		};

		Lexeme op;
	}

	public static class IntsToBoolOperatorToken extends Token {
		public IntsToBoolOperatorToken(String s) {
			super(s);
			switch (s) {
			case "=":
				op = Lexeme.EQ;
				break;
			case "/=":
				op = Lexeme.NE;
				break;
			case "<":
				op = Lexeme.LT;
				break;
			case ">":
				op = Lexeme.GT;
				break;
			case "<=":
				op = Lexeme.LE;
				break;
			case ">=":
				op = Lexeme.GE;
				break;
			default:
				throw new CompileErrorException("Unrecognized IntsToBoolOperator Token:"+"\""+s+"\"\n");
			}
		}

		public static enum Lexeme {
			EQ, NE, LT, GT, LE, GE,
		};

		Lexeme op;
	}

	public static class BooleansOperatorToken extends Token {
		public BooleansOperatorToken(String s) {
			super(s);
			switch (s) {
			case "&&":
				op = Lexeme.AND;
				break;
			case "||":
				op = Lexeme.OR;
				break;
			case "|?":
				op = Lexeme.CAND;
				break;
			case "&?":
				op = Lexeme.COR;
				break;
			default:
				throw new CompileErrorException("Unrecognized BooleansOperator Token:"+"\""+s+"\"\n");
			}
		}

		public static enum Lexeme {
			AND, OR, CAND, // ?
			COR // ?
		};

		Lexeme op;
	}

	public static class TypeToken extends Token {
		public TypeToken(String s) {
			super(s);
			switch (s) {
			case "int":
				type = Lexeme.INT;
				break;
			case "bool":
				type = Lexeme.BOOL;
				break;
			default:
				throw new CompileErrorException("Unrecognized TypeToken Token:"+"\""+s+"\"\n");
			}
		}

		public static enum Lexeme {
			INT, BOOL
		};

		Lexeme type;
	}

	public static class DelimiterToken extends Token {
		public DelimiterToken(String s) {
			super(s);
			value = s;
		}

		String value;
	}

	public static class BecomeToken extends Token {

		public BecomeToken(String s) {
			super(s);
			if (!":=".equals(s)) {
				throw new CompileErrorException("Unrecognized Become Token:"+"\""+s+"\"\n");
			}
		}

	}

	public static class DoToken extends Token {

		public DoToken(String s) {
			super(s);
			if (!"do".equals(s)) {
				throw new CompileErrorException("Unrecognized DoToken Token:"+"\""+s+"\"\n");
			}
		}

	}

	public static class FlowControlToken extends Token {

		public FlowControlToken(String s) {
			super(s);
			switch (s) {
			case "while":
				mode = Lexeme.WHILE;
				break;
			case "endwhile":
				mode = Lexeme.ENDWHILE;
				break;
			case "if":
				mode = Lexeme.IF;
				break;
			case "endif":
				mode = Lexeme.ENDIF;
				break;
			case "then":
				mode = Lexeme.THEN;
				break;
			case "else":
				mode = Lexeme.ELSE;
				break;
			case "fun":
				mode = Lexeme.FUN;
				break;
			case "endfun":
				mode = Lexeme.ENDFUN;
				break;
			case "proc":
				mode = Lexeme.PROC;
				break;
			case "endproc":
				mode = Lexeme.ENDPROC;
				break;
			case "skip":
				mode = Lexeme.SKIP;
				break;
			case "return":
				mode = Lexeme.RETURN;
				break;
			case "program":
				mode = Lexeme.PROGRAM;
				break;
			case "endprogram":
				mode = Lexeme.ENDPROGRAM;
				break;
			case "local":
				mode = Lexeme.LOCAL;
				break;
			case "global":
				mode = Lexeme.GLOBAL;
				break;
			default:
				throw new CompileErrorException("Unrecognized FlowControl Token:"+"\""+s+"\"\n");
			}
		}

		public static enum Lexeme {
			WHILE, ENDWHILE, IF, ENDIF, THEN, ELSE, FUN, ENDFUN, PROC, ENDPROC, SKIP, RETURN, PROGRAM, ENDPROGRAM, LOCAL, GLOBAL,

		};

		Lexeme mode;
	}

	public static class IOToken extends Token {
		public IOToken(String s) {
			super(s);
			switch (s) {
			case "debugin":
				cmd = Lexeme.DEBUGIN;
				break;
			case "debugout":
				cmd = Lexeme.DEBUGOUT;
				break;
			default:
				throw new CompileErrorException("Unrecognized IO Token:"+"\""+s+"\"\n");
			}
		}
		

		public static enum Lexeme {
			DEBUGIN, DEBUGOUT
		};

		Lexeme cmd;
	}

	public static class InitToken extends Token {

		public InitToken(String s) {
			super(s);
			if(!"init".equals(s)) {
				throw new CompileErrorException("Unrecognized Init Token:"+"\""+s+"\"\n");
			}
		}
		
	}

	public static class SingleBooleanOperatorToken extends Token {
		public SingleBooleanOperatorToken(String s) {
			super(s);
			switch (s) {
			case "not":
				op = Lexeme.NOT;
				break;
			default:
				throw new CompileErrorException("Unrecognized SingleBooleansOperator Token:"+"\""+s+"\"\n");
			}
		}

		public static enum Lexeme {
			NOT
		};

		Lexeme op;
	}

	public static class SentinelToken extends Token {
		public SentinelToken(String s){
			super(s);
		}
	}
}
