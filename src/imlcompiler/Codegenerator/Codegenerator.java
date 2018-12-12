package imlcompiler.Codegenerator;

import ch.fhnw.lederer.virtualmachineFS2015.CodeArray;
import imlcompiler.Parser.ImlComponent;

public class Codegenerator {

    ImlComponent ast;

    public Codegenerator (ImlComponent ast){
        this.ast = ast;
    }

    public CodeArray getCode(){
        // todo generate CodeArray
        return null;

    }


    public int getCodeSize(){
        // todo
        return 0;
    }

}
