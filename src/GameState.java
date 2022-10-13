import java.util.stream.Stream;

public interface GameState<E> {
    /**
     * Return +-infinity if and only if game is won/lost state
     * */
    double eval();

    /**
     * Lazily generate all game states after valid moves
     * */
    Stream<E> nextStates();
}
