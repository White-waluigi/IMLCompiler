package imlcompiler;

public class Main {


    // Usage: add path to iml file to run configuration

    public static void main(String[] args){

        if (args.length < 1) System.out.println("No iml program provided");

        Scanner scanner = new Scanner(args[0]);

        TokenList tokenList = new TokenList();

        try {
            tokenList = scanner.run();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println(tokenList.toString());

    }
}
