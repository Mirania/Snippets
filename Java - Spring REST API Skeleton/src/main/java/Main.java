import api.Api;
import api.ErrorHandler;
import org.springframework.boot.builder.SpringApplicationBuilder;

// to turn this into a one-click deployable REST API, compile the project into an executable JAR file
// this API will be available at localhost:port
public class Main {

    public static void main(String[] args) {
        // configurations are defined in src/main/resources/application.properties
        new SpringApplicationBuilder().sources(Api.class, ErrorHandler.class).run(args);
    }

}
