import java.util.stream.Stream;

public interface GameState<E> {
    /**
     * Return +-infinity if and only if game is won/lost state
     * */
    int eval();

    boolean stopTreeSearch();

    /**
     * Lazily generate all game states after valid moves
     * */
    Stream<E> nextStates();
}
