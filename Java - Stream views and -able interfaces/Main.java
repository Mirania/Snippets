import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {

        var data = new ArbitraryDataStructure<String, Integer>();
        data.put("a", 1, 2, 3);
        data.put("b", 4, 5);
        data.put("c", 6);
        var data2 = new ArbitraryDataStructure<String, Integer>();

        var arr = Arrays.asList(data, data2);
        Collections.sort(arr);
        System.out.println("sorted -> " + arr);

        for (var number : data) {
            System.out.println("iterator -> " + number);
        }

        data.stream().forEach(number -> System.out.println("sequential stream -> " + number));
        data.parallelStream().forEach(number -> System.out.println("parallel stream -> " + number));

        try (var writer = new ArbitraryFileWriter("test.txt")) {
            writer.write("structures: %s, %s", data, data2);
        } catch (IOException e) {
            System.out.println("not good: " + e.getMessage());
        }

        var dto = new ArbitraryDto();
        System.out.println("original -> " + dto + " -- " + dto.getOther() + " -- " + dto.getOther().getOther());
        dto.serialize("dto.ser");
        var dto2 = ArbitraryDto.deserialize("dto.ser");
        System.out.println("restored -> " + dto2 + " -- " + dto2.getOther() + " -- " + dto2.getOther().getOther());
    }
}
