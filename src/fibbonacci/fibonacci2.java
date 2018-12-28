package fibbonacci;

public class fibonacci2 {

    public static void main (String [] args){
        int x = 1;
        int y = 1;
        while(x < 1000){
            System.out.println(x);
            int tmp = x;
            x = y + x;
            y = tmp;
        }

    }
}
