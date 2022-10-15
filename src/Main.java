public class Main {
    public static int randInt(int lo, int hi) {
        return lo + (int) (Math.random() * (hi - lo));
    }

    public static int[] randArr(int len, int lo, int hi) {
        int[] ans = new int[len];
        for (int i = 0; i < len; i++)
            ans[i] = randInt(lo, hi);
        return ans;
    }

    public static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    public static void shuffle(int[] a) {
        for (int i = 0; i < a.length; i++)
            swap(a, i, randInt(i, a.length));
    }

    public static void depthLimitedSearch(int d){
        Board b=new Board(new byte[][]{
                {0, 0, -1, -1, -1, -1},
                {-1,-1, 0,-1, 0, 0},
                {0, 0, 0, 0,-1, 0},
                {1, 1,-1, 1, 0, 0},
                {0, 0, 0, 0, 1, 1},
                {0, 0, 1, 1, 1, 1},
        }, (byte) 1,-1);
        final long t = System.currentTimeMillis();
        int ans=b.findBestMove(d);
        System.out.println(d+" - "+ans+" - "+(System.currentTimeMillis() - t) / 1000.0 + "s");
    }

    public static void main(String[] args) {
        depthLimitedSearch(10);
    }
}
/*
{-1,0,-1,-1,-1,0},
{-1,0,-1,0,0,-1},
{1,1,1,0,0,0},
{0,0,0,0,1,1},
{0,0,0,0,0,0},
{0,0,0,0,0,0},

                {-1, 0, -1, -1, -1, 0},
                {-1, 0, -1, 0, 0, -1},
                {1, -1, 1, 0, 0, 0},
                {0, 0, 1, 0, 1, 1},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},

                {-1, -1, -1, -1, -1, -1},
                {-1, 0, 0, 0, -1, -1},
                {0, 0, -1, 1, 0, 0},
                {0, 1, 1, 0, 1, 1},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},

*               {0, 0, 0, 0, 0, 0},
                {-1, -1, -1, -1, -1, -1},
                {-1, -1, -1, -1, -1, -1},
                {1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1},
                {0, 0, 0, 0, 0, 0},
                *
                * {-1, -1, -1, -1, -1, -1},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
*
* */
