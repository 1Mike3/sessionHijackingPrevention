package proj.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSON_Deserialize {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Deserializes a JSON string into a Java object of the specified type.
     *
     * @param json the JSON string to deserialize
     * @param typeRef the type reference of the Java object to deserialize into
     * @param <T> the type of the Java object
     * @return a Java object of the specified type
     * @throws Exception if deserialization fails
     */
    public static <T> T deserialize(String json, TypeReference<T> typeRef) throws Exception {
        return objectMapper.readValue(json, typeRef);
    }
}