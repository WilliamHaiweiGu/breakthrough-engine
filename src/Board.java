import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Board implements GameState<Board> {

    private final static int WINLOSE=Integer.MAX_VALUE-1;
    private final static int nRow = 6;
    private final static int nCol = 6;
    /**
     * Row 0: player 1's target
     * Row 5: player -1's target
     * */
    final byte[][] board;
    private final byte player;
    /**
     * Move format: 3*(index in 1d array)+(0?left / 1?mid / 2?right)
     * */
    final int move;
    private static final Predicate<Integer> isValidMove=m->m>=0;
    private final Function<Integer,Board> createBoard=m->new Board(this,m);

    private static final int[] tb=new int[1<<18];
    static{
        final Scanner in;
        try {
            in=new Scanner(new File("C:\\Users\\willi\\Downloads\\mp\\tbJava.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        for(int i=0;i<tb.length;i++)
            tb[i]=in.nextInt();
    }

    /**Signals*/

    /**
     * 1 if 1 won. -1 if -1 won. 0 if not end.
     * */
    private final byte endStage;
    /**
     * True if player wins next move
     * */
    private /*final*/ boolean winIn1=false;
    /**
     * True if must defend or opponent wins next move
     * */
    private /*final*/ boolean defense=false;
    /**
     * True if don't need defend and table base has forced win
     * */
    private /*final*/ boolean tbHit=false;


    /**
     * make new board position from old position
     * @param player Current player that plays this turn. must be either 1 or -1.
     * @param move The move that lead to this board.
     * */
    public Board(final byte[][] board, final byte player,int move) {
        if(!(player==1||player==-1))
            throw new IllegalArgumentException("Player's value is "+player);
        this.board = new byte[nRow][];
        for (int i = 0; i < nRow; i++)
            this.board[i] = Arrays.copyOf(board[i], nCol);
        this.player = player;
        this.move=move;
        //make last player's move
        if(move>=0) {
            final byte dest = (byte) (move % 3 - 1);
            move /= 3;
            final int srcR = move / nCol;
            final int srcC = move % nCol;
            board[srcR][srcC] = 0;
            board[srcR + player][srcC + dest] = (byte) -player;
        }
        //Generate signals
        endStage = computeEndStage();
        if(player<0)
            invBoard();
        //If player is at critical line, forced win in 1.
        if(contains(this.board[1],player)){
            winIn1 = true;
        }
        else if(contains(this.board[4], (byte) -player)) //If player is not at its critical line but opponent is, defend.
            defense=true;
        else { //defense==false. Lookup table base
            int n=0;
            for(int i=0;i<2;i++)
                for(int j=0;j<nCol;j++) {
                    n <<= 1;
                    if (this.board[i][j] !=player)
                        n |= 1;
                }
            for(int j=0;j<nCol;j++) {
                n <<= 1;
                if (this.board[2][j] ==player)
                    n |= 1;
            }
            tbHit=tb[n]>0;
        }
        if(player<0)
            invBoard();
    }

    /**
     * make new board position from old position
     * @param move The move that lead to this board.
     */
    public Board(Board src, int move) {
        this(src.board, (byte) -src.player,move);
    }

    private byte computeEndStage() {
        if(contains(board[0], (byte) 1))
            return 1;
        if(contains(board[nRow-1], (byte) -1))
            return -1;
        return 0;
    }

    private void invBoard(){
        for (int i = 0, j = nRow - 1; i < j; i++, j--) {
            byte[] t = board[i];
            board[i] = board[j];
            board[j] = t;
        }
    }
    /**Pos->player 1 winning. Neg-> player -1 winning*/
    @Override
    public int eval() {
        if(endStage!=0)
            return endStage *WINLOSE;
        if(winIn1||tbHit)
            return player*WINLOSE;
        //Normal evaluation. Step 1: positional value
        int ans=totalPosValue((byte) 1);
        invBoard();
        ans-=totalPosValue((byte) -1);
        invBoard();
        //Step 2: material value
        int cnt=0;
        for(int i=0;i<nRow;i++)
            for(int j=0;j<nCol;j++)
                cnt+=board[i][j];
        ans+=Integer.compare(cnt,0)*10;
        //Step 3: breakthough likelyhood
        ans+= totalBreakthroughLikelihood((byte) 1);
        invBoard();
        ans-= totalBreakthroughLikelihood((byte) -1);
        invBoard();
        return ans;
    }

    private static final byte[][] weights= {
            null,
            {20,28,28,28,28,20},
            {13,18,18,18,18,13},
            {7,10,10,10,10,7},
            {3,5,5,5,5,3},
            {5,12,9,9,12,5}
    };
    /**
     * Board must be inverted before calling if pov=-1.
     * @return non-negative number
     * */
    private int totalPosValue(byte pov){
        int ans=0;
        for(int i=1;i<nRow;i++)
            for(int j=0;j<nCol;j++)
                if(board[i][j]==pov){
                    ans+=weights[i][j];
                    final boolean hasDown=i<nRow-1;
                    int safe=0;
                    if(j<nCol-1){ // Has right
                        if(board[i-1][j+1]==-pov)
                            safe-=1;
                        if(hasDown&&board[i+1][j+1]==pov)
                            safe+=1;
                    }
                    if(j>0){ // Has left
                        if(board[i-1][j-1]==-pov)
                            safe-=1;
                        if(hasDown&&board[i+1][j-1]==pov)
                            safe+=1;
                    }
                    if(safe>=0)
                        ans+=weights[i][j]>>1;
                }
        return ans;
    }

    /**
     * Board must be inverted before calling if pov=-1.
     * @return non-negative number
     * */
    private int totalBreakthroughLikelihood(byte pov){
        int ans=0;
        for(int i=1;i<nRow;i++)
            for(int j=0;j<nCol;j++){
                final int j1=j+1;
                for(int i0=i-2;i0<i;i0++)
                    for(int j0=j-1;j0<=j1;j0++)
                        if(i0>=0&&j0>=0&&j0<nCol&&board[i0][j0]!=-pov)
                            ans++;
            }
        return ans;
    }

    public static boolean contains(byte[] arr,byte b){
        for(byte i:arr)
            if(i==b)
                return true;
        return false;
    }

    @Override
    public boolean stopTreeSearch() {
        return endStage!=0||winIn1||tbHit;
    }

    @Override
    public Stream<Board> nextStates() {
        //If a player must defend, only consider defending pieces
        final IntStream candidates=defense?
                player>0?IntStream.range((nRow-1) * nCol,nRow * nCol):
                        IntStream.range(0,nCol):
                IntStream.range(0, nRow * nCol);
        return candidates.mapToObj(i -> {
            final int srcC = i % nCol;
            final int srcR = i / nCol;
            final int nextRow = srcR - player;
            if (!(board[srcR][srcC] == player) || nextRow < 0 || nextRow >= nRow)
                return null;
            i*=3;
            //Potential moves
            final int leftFront = srcC > 0 && board[nextRow][srcC - 1] != player ? i : -1;
            final int midFront = board[nextRow][srcC] == 0 ? i+1 : -1;
            final int rightFront = srcC < nCol - 1 && board[nextRow][srcC + 1] != player ? i+2 : -1;
            return Stream.of(leftFront, midFront, rightFront).filter(isValidMove).map(createBoard);
        }).flatMap(s->s);
    }

    public int findBestMove(int maxDepth){
        return AlphaBetaPrune.search(this,maxDepth).move;
    }

    @Override
    public String toString() {
        final StringBuilder ans = new StringBuilder("endStage="+endStage+", winIn1="+winIn1+", defense="+defense+", tbHit="+tbHit+'\n');
        for (byte[] row : board) {
            ans.append('[');
            for (byte b : row)
                ans.append('\t').append(b);
            ans.append("\t]\n");
        }
        return ans.append(player).append("'turn\n").toString();
    }

    //TESTING METHODS

    public static void testConsistance(int n){
        byte[][] b=new byte[nRow][nCol];
        for(int i=1;i<nRow-1;i++)
            for(int j=0;j<nCol;j++)
                b[i][j]= (byte) Main.randInt(-1,2);
        byte[][] bInv=new byte[nRow][nCol];
        for(int i=0;i<nRow;i++)
            for(int j=0;j<nCol;j++)
                bInv[i][j]= (byte) -b[nRow-1-i][j];
        Board b0=new Board(b, (byte) 1,-1);
        int eval0=b0.eval();
        Board b1=new Board(bInv, (byte) -1,-1);
        int eval1=b1.eval();
        if(eval1+eval0!=0)
            System.out.println("Test " + n + " failed. b0=\n"+b0+"b1=\n"+b1+"eval0="+eval0+", eval1="+eval1);
    }

    public static void main(String[] args){
        /*
        Board b=new Board(new byte[][]{
                {0, 0, 0, 0, 0, 0},
                {-1, -1, 0, 0, -1, 0},
                {1, 0, 0, -1, 1, 0},
                {1, 0, 1, 0, 0, 1},
                {1, 0, 1, 1, 1, 1},
                {0, 0, 0, 0, 0, 0},
        }, (byte) -1,-1);
        System.out.println(b);
        System.out.println(b.eval());*/

        final long t=System.currentTimeMillis();
        IntStream.range(Integer.MIN_VALUE,Integer.MAX_VALUE).parallel().forEach(Board::testConsistance);
        System.out.println((System.currentTimeMillis()-t)/1000.0+"s");
    }
}
