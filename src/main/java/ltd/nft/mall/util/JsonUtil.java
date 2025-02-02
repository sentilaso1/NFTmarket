package ltd.nft.mall.util;


import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Encapsulation methods for handling JSON
 * use: jackson
 */
public class JsonUtil {
    /*
     * 001. Convert JSON to object
     * 
     * @param: object to be passed, JSON string
     * 
     * @return: Object
     */
    public static Object jsonToObj(Class objClass, String jsonStr)
            throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonStr, objClass);
    }

    /*
     * 002. Convert object to JSON
     * 
     * @param: object to be passed
     * 
     * @return: JSON string
     */
    public static String objToJson(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }
}
