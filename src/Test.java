import java.util.Iterator;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Test {
    public static void main(String[] args){
        for(int i=0;i<1024;i++){
            System.out.println("Loop "+i);
            Iterator<Integer> iter=IntStream.range(Integer.MIN_VALUE,Integer.MAX_VALUE).iterator();
            while(iter.hasNext())
                iter.next();
        }

    }
}
