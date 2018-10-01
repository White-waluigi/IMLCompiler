package imlcompiler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Scanner {

    String imlCode;


    public Scanner(String path){

        this.imlCode = readFile(path);
    }

    public String toString(){

        return this.imlCode;
    }


    private String readFile(String fileName) {

        String line = null;
        StringBuilder stringBuilder = new StringBuilder();

        try {

            FileReader fileReader = new FileReader(fileName);

            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
        }

        return stringBuilder.toString();
    }

}
