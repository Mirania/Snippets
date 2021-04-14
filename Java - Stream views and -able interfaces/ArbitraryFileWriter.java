import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ArbitraryFileWriter implements AutoCloseable {

    // this design is done on purpose just to give AutoCloseable a reason to exist
    private final FileWriter writer;

    public ArbitraryFileWriter(final String path) throws IOException {
        var file = new File(path);

        if (!file.exists()) {
            file.createNewFile();
        }

        this.writer = new FileWriter(file);
    }

    public void write(final String format, final Object... args) throws IOException {
        this.writer.write(String.format(format, args));
    }

    /**
     * Implementation of {@link AutoCloseable}.
     */
    @Override
    public void close() throws IOException {
        System.out.println("closing");
        this.writer.close();
    }
}
