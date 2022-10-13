import java.io.*;
import java.util.Arrays;

public class TextFile extends File {
    private static final FilenameFilter filter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".txt");
        }
    };

    public TextFile(File dir, String pathname) {
        super(dir, pathname);
    }

    public TextFile(String string) {
        super(string);
    }

    public static File[] filesInDir(File dir) {
        File[] ans = dir.listFiles(filter);
        Arrays.sort(ans);
        return ans;
    }

    public String getContent() {
        try {
            FileInputStream rd = new FileInputStream(this);
            byte[] txt = rd.readAllBytes();
            rd.close();
            return new String(txt);
        } catch (Exception e) {
        }
        return null;
    }

    public void save(String content) {
        try {
            FileWriter writer = new FileWriter(getPath());
            writer.append(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}