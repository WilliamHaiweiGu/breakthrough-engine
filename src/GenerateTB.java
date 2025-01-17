import java.util.Arrays;
import java.util.stream.IntStream;

public class GenerateTB {

    public static byte[] parse6Digit(int n, final byte amt) {
        final int len = 6;
        byte[] ans = new byte[len];
        for (int i = len - 1; i >= 0; i--) {
            ans[i] = (byte) (amt * (n & 1));
            n >>= 1;
        }
        return ans;
    }

    public static int compute(int n) {
        Board b = new Board(new byte[][]{
                parse6Digit(n >> 12, (byte) -1),
                parse6Digit(n >> 6, (byte) -1),
                parse6Digit(n, (byte) 1),
        }, (byte) 1,-1);
        //return AlphaBetaPrune.search(b);

        return AlphaBetaPrune.search(b,5);
    }

    public static void main(String[] args) {
        long t = System.currentTimeMillis();

        final int len = 1 << 18;
        final int[] arr = new int[len];
        IntStream.range(0, len).parallel().forEach(i -> arr[i] = compute(i));


        StringBuilder out=new StringBuilder();
        int count=0;
        int cur=Integer.MIN_VALUE;
        for(int num:arr) {
            if (cur == num)
                count++;
            else{
                if(cur>Integer.MIN_VALUE){
                    out.append((char)(64+cur));
                    if(count>1)
                        out.append(count);
                }
                cur=num;
                count=1;
            }
        }
        out.append((char)(64+cur));
        if(count>1)
            out.append(count);

        String s=Arrays.toString(arr).replace(" ","");
        System.out.println(s);
        new TextFile("C:\\Users\\willi\\Downloads\\mp\\tb.txt").save(s);
        System.out.println((System.currentTimeMillis() - t) / 1000.0 + "s");
    }
}