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
        System.out.println((n.utility.eval > 0 ? "Win" : "Lose"));
        for (Node<T> node = n; node != null; node = node.bestMove)
            System.out.println(node);*/

        return n.bestRes.state;
    }

    private Node<E> abSearch() {
        return maxSearch(startState, Integer.MIN_VALUE, Integer.MAX_VALUE,0);
    }

    private Node<E> maxSearch(E state, int a, int b,int depth) {
        if (depth>=maxDepth||state.stopTreeSearch())
            return new Node<>(state, null, state.eval());
        int utility = Integer.MIN_VALUE;
        Node<E> bestRes = null;
        depth++;
        final Iterator<E> nextStates = state.nextStates().iterator();
        while (nextStates.hasNext()) {
            final Node<E> next = minSearch(nextStates.next(), a, b,depth);
            if (utility<next.utility) {
                utility = next.utility;
                bestRes = next;
                if (utility>a)
                    a = utility;
            }
            if (utility>=b)
                break;
        }
        return new Node<>(state, bestRes, utility);
    }

    private Node<E> minSearch(E state, int a,int b,int depth) {
        if (depth>=maxDepth||state.stopTreeSearch())
            return new Node<>(state, null, state.eval());
        int utility = Integer.MAX_VALUE;
        Node<E> bestRes = null;
        depth++;
        final Iterator<E> nextStates = state.nextStates().iterator();
        while (nextStates.hasNext()) {
            final Node<E> next = maxSearch(nextStates.next(), a, b,depth);
            if (utility>next.utility) {
                utility = next.utility;
                bestRes = next;
                if (utility<b)
                    b = utility;
            }
            if (utility <= a)
                break;
        }
        return new Node<>(state, bestRes, utility);
    }


    private static class Node<T> {
        private final int utility;
        private final T state;
        private final Node<T> bestRes;

        private Node(T state, Node<T> bestMove, int utility) {
            this.state = state;
            this.bestRes = bestMove;
            this.utility = utility;
        }

        @Override
        public String toString() {
            return "eval=" + utility + '\n' + state + '\n';
        }
    }
}
