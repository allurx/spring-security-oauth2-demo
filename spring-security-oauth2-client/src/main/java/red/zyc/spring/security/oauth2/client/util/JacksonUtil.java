package red.zyc.spring.security.oauth2.client.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author zyc
 */
public final class JacksonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JacksonUtil() {
    }

    public static String toJsonString(Object src) {
        try {
            return OBJECT_MAPPER.writeValueAsString(src);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
