package imlcompiler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Scanner {

    String imlCode;

    enum State{ZERO,ONE,TWO,THREE}


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


    private HashMap<String, String> readTokens(String fileName) {

        String keyword = null;
        HashMap<String, String> hashMap = new HashMap<>();

        try {

            FileReader fileReader = new FileReader(fileName);

            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while((keyword = bufferedReader.readLine()) != null) {
                hashMap.put(keyword, bufferedReader.readLine());
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

        return hashMap;
    }






    public TokenList run() throws Exception{

        HashMap<String, String> hashMap = readTokens("tokens.txt");

        TokenList tokenList = new TokenList();

        State state = State.ZERO;

        //todo: finish scanning algorithm

        StringBuilder identifier = new StringBuilder();
        StringBuilder literal = new StringBuilder();
        StringBuilder operator = new StringBuilder();


        for( int i = 0; i < this.imlCode.length(); i++){

            char c = this.imlCode.charAt(i);



            if (c == '\n' || c == '\b'){
                state = State.ZERO;
            }
            else if(Character.isLetter(c) && state == State.ZERO){
                state = State.ONE;
                identifier.append(c);

            }
            else if (Character.isLetterOrDigit(c) && state == State.ONE){
                identifier.append(c);
            }
            else if (!Character.isLetterOrDigit(c) && state == State.ONE){
                state = State.ZERO;
                if (hashMap.containsKey(identifier.toString())){
                    tokenList.add(new Token(hashMap.get(identifier.toString())));
                }
                else {
                    tokenList.add(new Token("(IDENT," + identifier.toString() + ")"));

                }
                identifier.delete(0, identifier.length());
                i--;
            }
            else if (Character.isDigit(c) && state == State.ZERO){
                state = State.TWO;
                literal.append(c);
            }
            else if (Character.isDigit(c) && state == State.TWO) {
                literal.append(c);
            }
            else if (!Character.isDigit(c) && state == State.TWO) {
                tokenList.add(new Token("(LITERAL," + literal.toString()+")"));
                literal.delete(0, literal.length());
                state = State.ZERO;
                // i--;
            }
            else if (c == '<'  && state == State.ZERO){
                operator.append(c);
                state = State.THREE;
            }
            else if (c == '=' && state == State.THREE){
                operator.append('=');
                state = State.ZERO;
            }
            else if (c != '=' && state == State.THREE) {
                if (hashMap.containsKey(operator.toString())){
                    tokenList.add(new Token(hashMap.get(operator.toString())));
                }
                operator.delete(0, operator.length()) ;
                state = State.ZERO;
               // i--;
            }
            else if (c == ':' && state == State.ZERO){

            }
            else if (c == '\t'){
                throw new Exception("tab in iml code not allowed");

            }
            else if (c == '(' && state == State.ZERO){
                tokenList.add(new Token(hashMap.get("(")));
            }
            else if (c == ')' && state == State.ZERO){
                tokenList.add(new Token(hashMap.get(")")));
            }

            else{

            }
        }



        return tokenList;
    }

}
