package proj.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSON_Deserialize {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Deserializes a JSON string into a Java object of the specified type.
     *
     * @param json the JSON string to deserialize
     * @param clazz the class of the Java object to deserialize into
     * @param <T> the type of the Java object
     * @return a Java object of the specified type
     * @throws Exception if deserialization fails
     */
    public static <T> T deserialize(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }
}