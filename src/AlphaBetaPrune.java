import java.util.Arrays;
import java.util.Iterator;

public class AlphaBetaPrune<E extends GameState<E>> {
    private final E startState;

    private AlphaBetaPrune(E startState) {
        this.startState = startState;
    }

    public static <T extends GameState<T>> int search(T state) {
        final Node<T> n = new AlphaBetaPrune<>(state).abSearch();

        System.out.println((n.utility.eval > 0 ? "Win" : "Lose") + " in " + n.utility.depth);
        for (Node<T> node = n; node != null; node = node.bestMove)
            System.out.println(node);

        return n.utility.eval > 0 ? n.utility.depth:-n.utility.depth;
    }

    private Node<E> abSearch() {
        return maxSearch(startState, Utility.MIN, Utility.MAX,0);
    }

    /*
    private static final byte[][] targetMax =new byte[][]{
        {0,-1,-1,0,-1,0},
        {0,0,0,-1,-1,-1},
        {0,0,1,0,0,1}
    };

    private static final byte[][] targetMin =new byte[][]{
            {0,-1,-1,-1,-1,0},
            {0,0,0,-1,1,-1},
            {0,0,1,0,0,1}
    };*/

    private Node<E> maxSearch(E state, Utility a, Utility b, int curDepth) {
        final double eval = state.eval();
        if (Double.isInfinite(eval))
            return new Node<>(state, null, Utility.of(eval, curDepth));
        Utility utility = Utility.MIN;
        Node<E> bestMove = null;
        curDepth++;
        final Iterator<E> nextStates = state.nextStates().iterator();
        while (nextStates.hasNext()) {
            final Node<E> next = minSearch(nextStates.next(), a, b,curDepth);
            /*
                if(Arrays.deepEquals(targetMax,((Board) state).board)) {
                    System.out.println("Cur:\n"+bestMove);
                    System.out.println("Next:\n"+next);
                }*/
            if (utility.compareTo(next.utility) < 0) {
                utility = next.utility;
                bestMove = next;
                if (utility.compareTo(a) > 0)
                    a = utility;
            }

            if (utility.compareTo(b) >= 0)
                break;
        }
        /*
        if(Arrays.deepEquals(targetMax,((Board) state).board)) {
            System.out.println("Cur:\n" + state);
            System.out.println("Final best move: " + bestMove);
        }*/
        return new Node<>(state, bestMove, utility);
    }

    private Node<E> minSearch(E state, Utility a, Utility b,int curDepth) {
        final double eval = state.eval();
        if (Double.isInfinite(eval))
            return new Node<>(state, null, Utility.of(eval, curDepth));
        Utility utility = Utility.MAX;
        Node<E> bestMove = null;
        curDepth++;
        final Iterator<E> nextStates = state.nextStates().iterator();
        if(!nextStates.hasNext()){ // Player -1 has no pieces in this half of board. Skip.
            final Node<E> next = maxSearch(state, a, b,curDepth);
            return new Node<>(state, next, next.utility);
        }
        while (nextStates.hasNext()) {
            final Node<E> next = maxSearch(nextStates.next(), a, b,curDepth);
            if (utility.compareTo(next.utility) > 0) {
                /*
                if(Arrays.deepEquals(targetMin,((Board) state).board)) {
                    System.out.println("Cur:\n"+bestMove);
                    System.out.println("Next:\n"+next);
                }*/
                utility = next.utility;
                bestMove = next;
                if (utility.compareTo(b) < 0)
                    b = utility;
            }

            if (utility.compareTo(a) <= 0)
                break;
        }
        return new Node<>(state, bestMove, utility);
    }

    private static class Utility implements Comparable<Utility> {
        private final double eval;
        private final int depth;

        private Utility(double eval, int depth) {
            this.eval = eval;
            this.depth = depth;
        }
        private final static int cacheSize=10;
        private final static Utility[] NEGINF=new Utility[cacheSize];
        private final static Utility[] POSINF=new Utility[cacheSize];
        static {
            for(int i=0;i<cacheSize;i++)
                NEGINF[i]=new Utility(Double.NEGATIVE_INFINITY, i);
            for(int i=0;i<cacheSize;i++)
                POSINF[i]=new Utility(Double.POSITIVE_INFINITY, i);
        }
        public static Utility of(double eval, int depth){
            return Double.isInfinite(eval)&&depth<cacheSize?eval>0?POSINF[depth] :NEGINF[depth]:new Utility(eval,depth);
        }
        public final static Utility MIN =new Utility(Double.NEGATIVE_INFINITY, Integer.MIN_VALUE);
        public final static Utility MAX =new Utility(Double.POSITIVE_INFINITY, Integer.MIN_VALUE);

        @Override
        public int compareTo(Utility o) {
            final int evalCmp = Double.compare(eval, o.eval);
            if(evalCmp==0&&Double.isInfinite(eval)){
                final int depthCmp=Integer.compare(depth, o.depth);
                return eval>0?-depthCmp:depthCmp;
            }
            return evalCmp;
        }
    }

    private static class Node<T> {
        private final Utility utility;
        private final T state;
        private final Node<T> bestMove;

        private Node(T state, Node<T> bestMove, Utility utility) {
            this.state = state;
            this.bestMove = bestMove;
            this.utility = utility;
        }

        @Override
        public String toString(){
            return "eval=" + utility.eval + ", depth=" + utility.depth + '\n' + state+'\n';
        }
    }

    public static void main(String[] args){

    }
}
