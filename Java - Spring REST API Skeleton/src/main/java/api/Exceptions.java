package api;

public class Exceptions {

    private Exceptions() {}

    /**
     * Custom API exception example.
     */
    public static class IntParseException extends RuntimeException {
        public IntParseException(String badValue) {
            super("Integer value expected but received '" + badValue + "' instead");
        }
    }

    /**
     * Custom API exception example.
     */
    public static class MissingDataException extends RuntimeException {
        public MissingDataException(int id) {
            super("An entry with the id '" + id + "' was not found");
        }
    }

}
