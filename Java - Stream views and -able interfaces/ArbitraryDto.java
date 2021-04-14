import java.io.*;

public class ArbitraryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int x;

    private final int y;

    //don't serialize this one
    private transient String a;

    //for infinite reference loop
    private final ArbitraryDto other;

    public ArbitraryDto() {
        this.x = 1;
        this.y = 1;
        this.a = "a";
        this.other = new ArbitraryDto(2, 2, this);
    }

    private ArbitraryDto(final int x, final int y, final ArbitraryDto other) {
        this.x = x;
        this.y = y;
        this.a = "a".repeat(this.x * this.y);
        this.other = other;
    }

    public ArbitraryDto getOther() {
        return this.other;
    }

    @Override
    public String toString() {
        return String.format("Dto[x=%d, y=%d, text=%s]", this.x, this.y, this.a);
    }

    /**
     * Implementation of {@link Serializable}.
     * Will write the size of the "a" string instead of the string itself.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.a.length());
    }

    /**
     * Implementation of {@link Serializable}.
     * Will restore the "a" string manually (constructors are not run during deserialization).
     */
    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        final int length = (int) in.readObject();
        this.a = "a".repeat(length);
    }

    public void serialize(final String path) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(this);
            out.flush();
        } catch (IOException e) {
            System.out.println("not good: " + e.getMessage());
        }
    }

    public static ArbitraryDto deserialize(final String path) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            return (ArbitraryDto) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("not good: " + e.getMessage());
            return null;
        }
    }
}
