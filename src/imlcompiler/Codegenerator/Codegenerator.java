package imlcompiler.Codegenerator;

import ch.fhnw.lederer.virtualmachineFS2015.CodeArray;
import ch.fhnw.lederer.virtualmachineFS2015.ICodeArray;
import ch.fhnw.lederer.virtualmachineFS2015.IInstructions;
import imlcompiler.Parser.ImlComponent;

public class Codegenerator {

    ImlComponent ast;
    CodeArray codeArray;
    int SIZE = 7;

    public Codegenerator (ImlComponent ast) throws ICodeArray.CodeTooSmallError {
        this.ast = ast;
        this.codeArray = new CodeArray(SIZE);

        codeArray.put(0, new IInstructions.LoadImInt(3));

        codeArray.put(1, new IInstructions.LoadImInt(7));

        codeArray.put(2, new IInstructions.SubInt());

        codeArray.put(3,  new IInstructions.AllocBlock(2));

        codeArray.put(4, new IInstructions.Store());

        codeArray.put(5, new IInstructions.OutputInt("test"));

        codeArray.put(6,  new IInstructions.Stop());


    }

    public CodeArray getCode(){
        return codeArray;
    }

    public int getStoreSize(){
        return 100;
    }

}
