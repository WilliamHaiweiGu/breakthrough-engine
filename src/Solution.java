import java.io.FileReader;

public class Solution {
    public static String decompress(String s){
        StringBuilder out=new StringBuilder();
        char prev='0';
        int mult=0;
        for(char c:s.toCharArray()){
            if('0'<=c&&c<='9')
                mult=mult*10+c-'0';
            else {
                for(;mult>1;mult--)
                    out.append(prev);
                mult=0;
                out.append(c);
                prev = c;
            }
        }
        for(;mult>1;mult--)
            out.append(prev);
        return out.toString();
    }

    public static void genereateOpenings(int depth) {
        Board b=new Board(new byte[][]{
                {-1, -1, -1, -1, -1, -1},
                {-1, -1, -1, -1, -1, -1},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1},
        }, (byte) 1,-1);
        final long t = System.currentTimeMillis();
        AlphaBetaPrune.search(b,depth,5);
        System.out.println("depth="+depth+" - "+(System.currentTimeMillis() - t) / 1000.0 + "s");
    }
    public static void main(String[] args) {
        try {
            for (int i = 7; ; i++)
                genereateOpenings(i);
        }catch (OutOfMemoryError e){
            e.printStackTrace();
        }
    }
}
