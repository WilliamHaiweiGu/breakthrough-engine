import jdk.jshell.execution.Util;

import java.util.Iterator;

/**
 * AlphaBeta prune algorithm
 * */
public class AlphaBetaPrune<E extends GameState<E>> {
    private final E startState;
    private final int maxDepth;
    private AlphaBetaPrune(E startState,int maxDepth) {
        this.startState = startState;
        this.maxDepth=maxDepth;
    }

    /**
     * @return state after best move if forced win. null otherwise.
     * */
    public static <T extends GameState<T>> T search(T state,int maxDepth) {
        final Node<T> n = new AlphaBetaPrune<>(state,maxDepth).abSearch();

        /*
        for (Node<T> node = n; node != null; node = node.bestRes)
            System.out.println(node);*/

        return n==null||n.bestRes==null?null:n.bestRes.state;
    }

    private Node<E> abSearch() {
        return startState.player()>0? maxSearch(startState, Utility.MIN, Utility.MAX,0):
                minSearch(startState, Utility.MIN, Utility.MAX,0);
    }

    private Node<E> maxSearch(E state, Utility a, Utility b, int curDepth) {
        if (curDepth>=maxDepth|| state.stopTreeSearch())
            return new Node<>(state, null, new Utility(state.eval(), curDepth));
        Utility utility = Utility.MIN;
        Node<E> bestMove = null;
        curDepth++;
        final Iterator<E> nextStates = state.nextStates().iterator();
        while (nextStates.hasNext()) {
            final Node<E> next = minSearch(nextStates.next(), a, b, curDepth);
            if (utility.compareTo(next.utility) < 0) {
                utility = next.utility;
                bestMove = next;
                if (utility.compareTo(a) > 0)
                    a = utility;
            }
            if (utility.compareTo(b) >= 0)
                break;
        }
        return new Node<>(state, bestMove, utility);
    }

    private Node<E> minSearch(E state, Utility a, Utility b, int curDepth) {
        if (curDepth>=maxDepth|| state.stopTreeSearch())
            return new Node<>(state, null, new Utility(state.eval(), curDepth));
        Utility utility = Utility.MAX;
        Node<E> bestMove = null;
        curDepth++;
        final Iterator<E> nextStates = state.nextStates().iterator();
        if (!nextStates.hasNext()) { // Player -1 has no pieces in this half of board. Skip.
            final Node<E> next = maxSearch(state, a, b, curDepth);
            return new Node<>(state, next, next.utility);
        }
        while (nextStates.hasNext()) {
            final Node<E> next = maxSearch(nextStates.next(), a, b, curDepth);
            if (utility.compareTo(next.utility) > 0) {
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


    private static class Node<T> {
        private final Utility utility;
        private final T state;
        private final Node<T> bestRes;

        private Node(T state, Node<T> bestRes, Utility utility) {
            this.state = state;
            this.bestRes = bestRes;
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
