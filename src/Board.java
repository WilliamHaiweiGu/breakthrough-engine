import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Board implements GameState<Board> {
    private final static int nRow = 3;
    private final static int nCol = 6;
    final byte[][] board;
    private final byte player;
    final int move;

    public Board(byte[][] board, byte newPlayer,int move) {
        this.board = new byte[nRow][];
        for (int i = 0; i < nRow; i++)
            this.board[i] = Arrays.copyOf(board[i], nCol);
        player = newPlayer;
        this.move=move;
    }

    public Board(byte[][] board) {
        this(board, (byte) 1,-1);
    }

    /**
     * make new board position from old position
     * dest=-1?left,0?front,1?right
     */
    public Board(Board src, int move) {
        this(src.board, (byte) -src.player,move);
        final byte dest= (byte) (move%3-1);
        move/=3;
        final int srcR=move/nCol;
        final int srcC=move%nCol;
        if (board[srcR][srcC] == 0)
            throw new RuntimeException();
        board[srcR][srcC] = 0;
        board[srcR - src.player][srcC + dest] = src.player;
    }

    public byte terminalStage() {
        for (byte b : board[0])
            if (b > 0)
                return 1;
        for (int i = 1; i < nRow; i++)
            for (byte b : board[i])
                if (b > 0)
                    return 0;
        return -1;
    }

    @Override
    public double eval() {
        if (player > 0)
            for (byte b : board[1])
                if (b > 0)
                    return 1;
        final byte terminal = terminalStage();
        return terminal == 0 ? 0 : terminal * Double.POSITIVE_INFINITY;
    }


    @Override
    public Stream<Board> nextStates() {
        final Stream<Board> ans = IntStream.range(0, nRow * nCol).mapToObj(i -> {
            final int srcC = i % nCol;
            final int srcR = i / nCol;
            final int nextRow = srcR - player;
            if (!(board[srcR][srcC] == player) || nextRow < 0 || nextRow >= nRow)
                return null;
            i*=3;
            final Board leftFront = srcC > 0 && board[nextRow][srcC - 1] != player ? new Board(this, i) : null;
            final Board midFront = board[nextRow][srcC] == 0 ? new Board(this, i+1) : null;
            final Board rightFront = srcC < nCol - 1 && board[nextRow][srcC + 1] != player ? new Board(this, i+2) : null;
            return Stream.of(leftFront, midFront, rightFront).filter(Objects::nonNull);
        }).flatMap(s -> s);
        return player < 0 ? Stream.concat(ans, Stream.of(new Board(this.board, (byte) 1,-1))) : ans;
    }

    @Override
    public String toString() {
        final StringBuilder ans = new StringBuilder();
        for (byte[] row : board) {
            ans.append('[');
            for (byte b : row)
                ans.append('\t').append(b);
            ans.append("\t]\n");
        }
        return ans.append(player).append("'turn\n").toString();
    }
}
