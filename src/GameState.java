import java.util.stream.Stream;

public interface GameState<E> {
    public double eval();
    public Stream<E> nextStates();
}
