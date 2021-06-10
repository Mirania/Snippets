import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        var a = new File("imgs/1.png");
        var b = new File("imgs/2.png");
        var c = new File("imgs/3.png");
        var d = new File("imgs/4.jpg");
        var e = new File("imgs/5.png");
        System.out.println(ImageSimilarity.compare(a,b));
        System.out.println(ImageSimilarity.compare(a,c));
        System.out.println(ImageSimilarity.compare(a,d));
        System.out.println(ImageSimilarity.compare(a,e));
    }

}
