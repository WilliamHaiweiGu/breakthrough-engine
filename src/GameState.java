import java.util.stream.Stream;

public interface GameState<E> {
    /**
     * NEVER Return +-infinity. Reserved for ABT to work correctly.
     * */
    int eval();

    boolean stopTreeSearch();

    /**
     * Lazily generate all game states after valid moves
     * */
    Stream<E> nextStates();

    /**
     * @return 1 for max player, -1 for min player.
     * */
    byte player();

    int id();
}
