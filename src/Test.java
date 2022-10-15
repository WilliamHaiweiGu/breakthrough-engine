public class Test {
    public static void main(String[] args){
        LimitedSizeDict<Integer> dict=new LimitedSizeDict<>(4);
        dict.put(1,1,5);
        System.out.println(dict);
        dict.put(2,2,5);
        System.out.println(dict);
        dict.put(3,3,5);
        System.out.println(dict);
        System.out.println(dict.queryWithMinDepth(3,4));
        System.out.println(dict);
        System.out.println(dict.queryWithMinDepth(3,6));
        System.out.println(dict);
        dict.put(4,4,6);
        System.out.println(dict);
        dict.put(5,5,6);
        System.out.println(dict);
        dict.put(6,6,6);
        System.out.println(dict);
    }
}
