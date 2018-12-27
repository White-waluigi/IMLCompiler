package testing;

public class fibonacci{

    int x = 1;

    int recurse( int y) {
        System.out.println(y);
        if( y > 1000) return 0;
        else {
            int z = x + y;
            x = y;
            return recurse(z);
        }
    }

    public static void main(String[] args){

        fibonacci f = new fibonacci();
        f.recurse(1);
    }

}