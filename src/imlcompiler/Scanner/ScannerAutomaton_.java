package imlcompiler.Scanner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import imlcompiler.CompileErrorException;

public class ScannerAutomaton {
	
	String currentToken = "";
	char[] illegalChars = { '@' };
	private TokenList retVal;
	TokenMap tm=new TokenMap("tokenClass.cmp");
	TokenList tl =new TokenList();
	
	int cline;
	int cpos;
	
	public ScannerAutomaton() {

		retVal=new TokenList();
	}

	boolean contains(char c, char[] array) {
		for (char x : array) {
			if (x == c) {
				return true;
			}
		}
		return false;
	}
	public void addToken(Token t) {
		//clean whitespace
		currentToken = "";
		
		if(null==t)
			return;
		
		t.line=cline;
		t.position=cpos;
		tl.add(t);
		
		
	}
	public int State0(char a) {

		if(Character.isWhitespace(a)) {
			//loop
			return 0;
		}
		//Whitespace not considered a Token, is removed
		addToken(null);
		
		if (Character.isLetter(a)) {
			//exit
			return 1;
		} else if (Character.isDigit(a)) {
			//exit
			return 2;
		} else if (contains(a, illegalChars)) {
			//exit
			return -1;
		} else {
			//exit
			return 3;
		}
	}

	public int State1(char a) {
		if (Character.isLetterOrDigit(a)) {
			//loop
			return 1;
		}
		
		String group=tm.GetGroupOfElement(currentToken);
		switch (group!=null?group:"") {
		case "FLOWMODETOKEN":
			addToken(new Token.FlowModeToken(currentToken));
			break;
		case "MECHMODETOKEN":
			addToken(new Token.MechModeToken(currentToken));
			break;
		case "CHANGEMODETOKEN":
			addToken(new Token.ChangeModeToken(currentToken));
			break;
		case "LITERALTOKEN":
			addToken(new Token.LiteralToken(currentToken));
			break;
		case "TYPETOKEN":
			addToken(new Token.TypeToken(currentToken));
			break;
		case "IOTOKEN":
			addToken(new Token.IOToken(currentToken));
			break;
		case "FLOWCONTROLTOKEN":
			addToken(new Token.FlowControlToken(currentToken));
			break;
		case "DOTOKEN":
			addToken(new Token.DoToken(currentToken));
			break;
		case "INITTOKEN":
			addToken(new Token.InitToken(currentToken));
			break;
		case "INTSOPERATORTOKEN":
			addToken(new Token.IntsOperatorToken(currentToken));
			break;
		default:
			addToken(new Token.IdentfierToken(currentToken));
			break;
		}
		
		
		if (Character.isWhitespace(a)) {
			//exit
			return 0;
		} else if (contains(a, illegalChars)) {
			//exit
			return -1;
		} else {
			//exit
			return 3;
		}
	}
	
	public int State2(char a) {
		if (Character.isLetterOrDigit(a)) {
			//loop
			return 2;
		}
		
		addToken(new Token.LiteralToken(currentToken));
		
		
		
		if (Character.isWhitespace(a)) {
			//exit
			return 0;
		} else if (contains(a, illegalChars)) {
			//exit
			return -1;
		} else {
			//exit
			return 3;
		}

	}
	public int StateError(char a) {
		throw new CompileErrorException("Compiler has entered Error state near token:\t"+currentToken);

	}
	public int State3(char a) {
		if(!Character.isWhitespace(a)&&!Character.isLetterOrDigit(a)&&!"DELIMITERTOKEN".equals(tm.GetGroupOfElement(a+""))) {
			return 3;
		}
		
		
		String group=tm.GetGroupOfElement(currentToken);
		
		switch (group!=null?group:"") {
		case ("INTSTOBOOLOPERATORTOKEN"):
			addToken(new Token.IntsToBoolOperatorToken(currentToken));
			break;
		case ("BOOLEANSOPERATORTOKEN"):
			addToken(new Token.BooleansOperatorToken(currentToken));
			break;
		case ("DELIMITERTOKEN"):
			addToken(new Token.DelimiterToken(currentToken));
			break;
		case ("BECOMETOKEN"):
			addToken(new Token.BecomeToken(currentToken));
			break;			
		case ("SINGLEBOOLEANOPERATORTOKEN"):
			addToken(new Token.SingleBooleanOperatorToken(currentToken));
			break;	
		case ("INTSOPERATORTOKEN"):
			addToken(new Token.IntsOperatorToken(currentToken));
			break;	
		default:
			System.out.println( "Token cannot be parsed. Ignoring for now. Token: \""+currentToken+"\"\tgroup: "+(group==null?"<None>":"\""+group+"\"")+"\tat: "+cline+":"+cpos);
			addToken(null);
			break;
		}
			
		
		
		
		
		
		
		if (Character.isLetter(a)) {
			return 1;
		} else if (Character.isDigit(a)) {
			return 2;
		} else if (contains(a, illegalChars)) {
			return -1;
		}
		else if (Character.isWhitespace(a)) {
			return 0;
		}
		else if ("DELIMITERTOKEN".equals(tm.GetGroupOfElement(a+""))) {
			return 3;
		}
		else {
			return -1;
		}
		
	}

	public void start(String file) throws IOException {
//		Iterator<Character> c = code.chars().mapToObj(cc -> (char) cc).collect(Collectors.toList()).iterator();
		int next=0;
		cline=0;
		cpos=0;
		

		FileReader fileReader = new FileReader(file);

		BufferedReader bufferedReader = new BufferedReader(fileReader);

		int c;
		while ((c = bufferedReader.read()) != -1){
			
			char a = (char)c;

			
			next= enterAutomaton(next,a);

			currentToken += a;
			
			if('\n'==a) {
				cline++;
				cpos=0;
			}else {
				cpos++;
			}
		}
		next= enterAutomaton(next,' ');
	
	}
	int enterAutomaton(int next,char a) {
		switch (next) {
		case 0:
			return State0(a);
		case 1:
			return State1(a);
		case 2:
			return State2(a);
		case 3:
			return State3(a);
		default:
			return StateError(a);
		}
	}
}
