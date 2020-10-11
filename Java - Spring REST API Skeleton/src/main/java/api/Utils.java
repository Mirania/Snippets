package api;

import static api.Exceptions.*;

public class Utils {

    private Utils() {}

    public static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IntParseException(value);
        }
    }

}
