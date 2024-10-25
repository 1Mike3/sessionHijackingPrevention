package proj.util;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSON_Serialize {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Serializes a Java object into a JSON string.
     *
     * @param obj the Java object to serialize
     * @return a JSON string representing the object
     * @throws Exception if serialization fails
     */
    public static String serialize(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}
/*Class generated using Generative AI*/