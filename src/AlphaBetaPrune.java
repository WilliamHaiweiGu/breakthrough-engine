import java.util.Iterator;

/**
 * AlphaBeta prune algorithm
 * */
public class AlphaBetaPrune<E extends GameState<E>> {
    private final E startState;
    private final int maxDepth;
    private final static LimitedSizeDict<Node<?>> dict=new LimitedSizeDict<>(1<<28);
    private AlphaBetaPrune(E startState,int maxDepth) {
        this.startState = startState;
        this.maxDepth=maxDepth;
    }

    /**
     * @return state after best move if forced win. null otherwise.
     * */
    public static <T extends GameState<T>> int search(T state,int maxDepth) {
        final Node<T> n = new AlphaBetaPrune<>(state,maxDepth).abSearch();

        /*
        for (Node<T> node = n; node != null; node = node.bestRes)
            System.out.println(node);*/

        return n==null?null:n.bestMove;
    }

    private Node<E> abSearch() {
        return startState.player()>0? maxSearch(startState, Utility.MIN, Utility.MAX,0):
                minSearch(startState, Utility.MIN, Utility.MAX,0);
    }

    private Node<E> maxSearch(E state, Utility a, Utility b, int curDepth) {
        if (curDepth>=maxDepth|| state.stopTreeSearch())
            return new Node<>(state, -1, new Utility(state.eval(), curDepth));
        final int effDepth=maxDepth-curDepth;
        final Node<E> dictRes= (Node<E>) dict.queryWithMinDepth(state.id(),effDepth);
        if(dictRes!=null)
            return dictRes;
        Utility utility = Utility.MIN;
        Node<E> bestRes = null;
        curDepth++;
        final Iterator<E> nextStates = state.nextStates().iterator();
        while (nextStates.hasNext()) {
            final Node<E> next = minSearch(nextStates.next(), a, b, curDepth);
            if (utility.compareTo(next.utility) < 0) {
                utility = next.utility;
                bestRes = next;
                if (utility.compareTo(a) > 0)
                    a = utility;
            }
            if (utility.compareTo(b) >= 0)
                break;
        }
        Node<E> ans=new Node<>(state, bestRes.state.move(), utility);
        dict.put(state.id(), ans,effDepth);
        return ans;
    }

    private Node<E> minSearch(E state, Utility a, Utility b, int curDepth) {
        if (curDepth>=maxDepth|| state.stopTreeSearch())
            return new Node<>(state, -1, new Utility(state.eval(), curDepth));
        final int effDepth=maxDepth-curDepth;
        final Node<E> dictRes= (Node<E>) dict.queryWithMinDepth(state.id(),effDepth);
        if(dictRes!=null)
            return dictRes;
        Utility utility = Utility.MAX;
        Node<E> bestRes = null;
        curDepth++;
        final Iterator<E> nextStates = state.nextStates().iterator();
        while (nextStates.hasNext()) {
            final Node<E> next = maxSearch(nextStates.next(), a, b, curDepth);
            if (utility.compareTo(next.utility) > 0) {
                utility = next.utility;
                bestRes = next;
                if (utility.compareTo(b) < 0)
                    b = utility;
            }
            if (utility.compareTo(a) <= 0)
                break;
        }
        Node<E> ans=new Node<>(state, bestRes.state.move(), utility);
        dict.put(state.id(), ans,effDepth);
        return ans;
    }


    private static class Node<T> {
        private final Utility utility;
        private final T state;
        private final int bestMove;

        private Node(T state, int bestMove, Utility utility) {
            this.state = state;
            this.bestMove = bestMove;
            this.utility = utility;
        }

        @Override
        public String toString() {
            return "eval=" + utility.eval + '\n' + state + '\n';
        }
    }

    private static class Utility implements Comparable<Utility> {
        public final static Utility MIN = new Utility(Integer.MIN_VALUE, Integer.MIN_VALUE);
        public final static Utility MAX = new Utility(Integer.MAX_VALUE, Integer.MIN_VALUE);

        private final int eval;
        private final int depth;

        private Utility(int eval, int depth) {
            this.eval = eval;
            this.depth = depth;
        }

        @Override
        public int compareTo(Utility o) {
            final int evalCmp = Integer.compare(eval, o.eval);
            if (evalCmp == 0 && eval!=0) {
                final int depthCmp = Integer.compare(depth, o.depth);
                return eval > 0 ? -depthCmp : depthCmp;
            }
            return evalCmp;
        }
    }
}
