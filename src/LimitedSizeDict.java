import java.util.*;

/**
 * Board ID to data
 * */
public class LimitedSizeDict<V> {
    private static class Node<V>{
        private final V val;
        private int effDepth;
        private final int id;
        private Node<V> prev=null;
        private Node<V> next=null;
        private Node(V val,int effDepth,int id) {
            this.val = val;
            this.effDepth=effDepth;
            this.id=id;
        }
    }
    private static <V> void connect(Node<V> left,Node<V> right){
        left.next=right;
        right.prev=left;
    }

    private final int maxSize;
    private int size;
    private final HashMap<Integer,Node<V>> dict;

    //Doubly linked list: LIFO
    private final Node<V> head;
    private final Node<V> tail;

    public LimitedSizeDict(int maxSize) {
        this.maxSize = maxSize;
        size=0;
        dict= new HashMap<>(maxSize);
        //Dummy nodes
        head=new Node<>(null,Integer.MIN_VALUE,Integer.MIN_VALUE);
        tail=new Node<>(null,Integer.MIN_VALUE,Integer.MIN_VALUE);
        connect(head,tail);
    }
    /**
     * @return Integer.MIN_VALUE if not found or not deep enough
     * */
    public V queryWithMinDepth(int boardID,int minDepth){
        final Node<V> n=dict.get(boardID);
        if(n==null)
            return null;
        //remove n
        connect(n.prev,n.next);
        //put n at head
        connect(n,head.next);
        connect(head,n);
        return n.effDepth<minDepth?null:n.val;
    }
    public void put(int boardID,V val,int minDepth){
        if(size>=maxSize) {
            //remove tail
            final Node<V> t = tail.prev;
            connect(t.prev, tail);
            dict.remove(t.id);
        }
        else
            size++;
        final Node<V> n=new Node<>(val,minDepth,boardID);
        //put n at head
        connect(n,head.next);
        connect(head,n);
        dict.put(boardID,n);
    }
}
