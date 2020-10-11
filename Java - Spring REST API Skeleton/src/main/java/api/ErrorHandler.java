package api;

import org.json.JSONObject;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static api.Exceptions.*;

@RestController
@ControllerAdvice
public class ErrorHandler implements ErrorController {

    @Override
    public String getErrorPath() { return "/error"; }

    /**
     * Default error handling method. In this case, it's used to handle requests that 404.
     */
    @RequestMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> badUrl() {
        var json = new JSONObject().put("error", "Unexpected request path");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(json.toString());
    }

    /**
     * Method that will respond to a request whenever it results in this exception being thrown.
     */
    @ExceptionHandler(IntParseException.class)
    public ResponseEntity<String> intParseEx(IntParseException ex) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var json = new JSONObject().put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).body(json.toString());
    }

    /**
     * Method that will respond to a request whenever it results in this exception being thrown.
     */
    @ExceptionHandler(MissingDataException.class)
    public ResponseEntity<String> missingDataEx(MissingDataException ex) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var json = new JSONObject().put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).body(json.toString());
    }

    /**
     * Method that will respond to a request whenever it results in this exception being thrown.
     * This is an exception internally thrown by Spring.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> httpEx(HttpRequestMethodNotSupportedException ex) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var json = new JSONObject().put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).body(json.toString());
    }
}
