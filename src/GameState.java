import java.util.stream.Stream;

public interface GameState<E> {
    double eval();

    Stream<E> nextStates();
}
