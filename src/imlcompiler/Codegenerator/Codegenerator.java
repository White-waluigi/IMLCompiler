package imlcompiler.Codegenerator;

import java.util.ArrayList;

import ch.fhnw.lederer.virtualmachineFS2015.CodeArray;
import ch.fhnw.lederer.virtualmachineFS2015.ICodeArray;
import ch.fhnw.lederer.virtualmachineFS2015.IInstructions;
import ch.fhnw.lederer.virtualmachineFS2015.IInstructions.IInstr;
import imlcompiler.Parser.ImlComponent;

public class Codegenerator {

    ImlComponent ast;
    CodeArray codeArray;
    int SIZE = 12;

    public Codegenerator (ImlComponent ast) throws ICodeArray.CodeTooSmallError {
        this.ast = ast;

        ArrayList<IInstructions.IInstr> ar=new ArrayList<>();
        
        int i=0;
        
        
        
        
        
        
        
        ar.add( new IInstructions.LoadImInt(3));
        ar.add( new IInstructions.LoadImInt(0));
        ar.add( new IInstructions.LoadImInt(0));
        ar.add( new IInstructions.Deref());
        ar.add( new IInstructions.LoadImInt(1));
        ar.add( new IInstructions.SubInt());
        ar.add( new IInstructions.Store());        
        ar.add( new IInstructions.LoadImInt(0));
        ar.add( new IInstructions.Deref());
        ar.add( new IInstructions.CondJump(11));
        ar.add( new IInstructions.Call(1));
        ar.add(  new IInstructions.Stop());

        
        
        this.codeArray = new CodeArray(ar.size());
        
        for(IInstr a:ar) {
        	codeArray.put(i++, a);        	
        }

        
    }

    public CodeArray getCode(){
        return codeArray;
    }

    public int getStoreSize(){
        return 100;
    }

}
