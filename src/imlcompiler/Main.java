package imlcompiler;

public class Main {

    public static void main(String[] args){

        if (args.length < 1) System.out.println("No iml program provided");

        Scanner scanner = new Scanner(args[0]);

        System.out.println(scanner.toString());

    }
}
