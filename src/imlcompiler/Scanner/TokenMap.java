package imlcompiler.Scanner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import imlcompiler.CompileErrorException;

public class TokenMap {
	public class Element {
		String group;
		String lexeme;
		public Element(String group, String lexeme) {
			this.group = group;
			this.lexeme = lexeme;
			if (lexeme==null|| group==null){
				throw new CompileErrorException("Token Template has no group or Lexeme:\n"+lexeme+"\t"+group);
			}
		}
		
	}
	ArrayList<Element> elements=new ArrayList<>();
	
	public String GetGroupOfElement(String lexeme) {
		for(Element e: elements) {
			if(e.lexeme.equals(lexeme)) {
				return e.group;
			}
		}
		return null;
	}
	
	public TokenMap(String a) {
		
		String currentGroup=null;
		try (BufferedReader br = new BufferedReader(new FileReader(a))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	if(line.startsWith("#")) {
		    		currentGroup=line.substring(1);		
		    	}else {
		    		elements.add(new Element(currentGroup, line));
		    	}
		    	
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
