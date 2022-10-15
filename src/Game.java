import java.util.Scanner;

public class Game {
    private static final Scanner in=new Scanner(System.in);
    private static Board playerTurn(Board b){
        int r0=in.nextInt(),c0=in.nextInt(),r1=in.nextInt(),c1=in.nextInt();
        b=new Board(b,(r0*6+c0)*3+(c1-c0)+1);
        System.out.println(b);
        return b;
    }
    private static Board AiTurn(Board b){
        final long t = System.currentTimeMillis();
        int ans = b.findBestMove(5);
        System.out.println("Move: " + ans + " - " + (System.currentTimeMillis() - t) / 1000.0 + "s");
        b=new Board(b,ans);
        System.out.println(b);
        return b;
    }
    public static void main(String[] args){

        Board b=new Board(new byte[][]{

                {0, 0, -1, -1, -1, -1},
                {-1,-1, 0,-1, 0, 0},
                {0, 0, 0, 0,-1, 0},
                {1, 1,-1, 1, 0, 0},
                {0, 0, 0, 0, 1, 1},
                {0, 0, 1, 1, 1, 1},
        }, (byte) 1,-1);
        System.out.println(b);
        while(!b.game_over()) {
            b=AiTurn(b);
        }
    }
}
