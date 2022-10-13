import java.io.FileReader;

public class Solution {
    public static String decompress(String s){
        StringBuilder out=new StringBuilder();
        char prev='0';
        int mult=0;
        for(char c:s.toCharArray()){
            if('0'<=c&&c<='9')
                mult=mult*10+c-'0';
            else {
                for(;mult>1;mult--)
                    out.append(prev);
                mult=0;
                out.append(c);
                prev = c;
            }
        }
        for(;mult>1;mult--)
            out.append(prev);
        return out.toString();
    }
    public static void main(String[] args) {
        String s=new TextFile("C:\\Users\\willi\\Downloads\\mp\\tbASCII64.txt").getContent();
        String s2=new TextFile("C:\\Users\\willi\\Downloads\\mp\\tbASCII64comp.txt").getContent();
        String s22=decompress(s2);
        System.out.println(s.equals(s22));
    }
}
