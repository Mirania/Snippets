package api;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static api.Exceptions.*;

@EnableAutoConfiguration
@RestController
public class Api {

    private Map<Integer, String> data;

    public Api() {
        data = new HashMap<>();
        data.put(1, "data1");
        data.put(2, "data2");
    }

    /**
     * A plaintext response. Available at localhost:port/hello.
     */
    @RequestMapping(value = "/hello", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> hello() {
        return ResponseEntity.status(HttpStatus.OK).body("Hello world!");
    }

    /**
     * A JSON response. Available at localhost:port/entry/{id}, e.g. localhost:port/entry/1.
     * It will return a different message if the id does not exist or is not a valid integer.
     */
    @RequestMapping(value = "/entry/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getData(@PathVariable("id") String id) {
        var dataId = Utils.parseInt(id);

        if (!data.containsKey(dataId))
            throw new MissingDataException(dataId);

        var json = new JSONObject().put(String.valueOf(dataId), data.get(dataId));
        return ResponseEntity.status(HttpStatus.OK).body(json.toString());
    }

    /**
     * A JSON response. Available at localhost:port/entry.
     * This is a POST method. It expects an input like {100: "data", 200: "data"}.
     * It will return a different message if any of the supplied ids is not a valid integer.
     */
    @RequestMapping(value = "/entry", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postData(@RequestBody Map<String, String> body) {
        var dataIds = body.keySet().stream().map(Utils::parseInt).collect(Collectors.toList());
        var array = new JSONArray();

        for (var id : dataIds) {
            data.put(id, body.get(String.valueOf(id)));
            array.put("/entry/" + id);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(array.toString());
    }

}
