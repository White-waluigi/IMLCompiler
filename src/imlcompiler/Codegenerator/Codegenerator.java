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
        
        
        
        
        
        int r=0;
        ar.add( new IInstructions.LoadImInt(3));		r++;        
        ar.add( new IInstructions.Call(3));				r++;
        ar.add( new IInstructions.Stop());        		r++;
        ar.add( new IInstructions.LoadImInt(0));		r++;
        ar.add( new IInstructions.LoadImInt(0));		r++;
        ar.add( new IInstructions.Deref());				r++;
        ar.add( new IInstructions.LoadImInt(1));		r++;
        ar.add( new IInstructions.SubInt());			r++;
        ar.add( new IInstructions.Store());        		r++;
        ar.add( new IInstructions.LoadImInt(0));		r++;
        ar.add( new IInstructions.Deref());				r++;
        ar.add( new IInstructions.CondJump(r+2));		r++;
        ar.add( new IInstructions.Call(3));				r++;
        ar.add( new IInstructions.Return(0));			r++;

        
        
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
