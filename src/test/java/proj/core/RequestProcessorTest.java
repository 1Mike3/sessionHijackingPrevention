package proj.core;

import io.javalin.http.Context;
import org.junit.jupiter.api.Test;
import proj.core.fingerprinting.FpRequestProcessor;
import proj.entities.FingerprintData;

import static org.junit.jupiter.api.Assertions.*;

public class RequestProcessorTest {
    @Test
    // Test to handle null context gracefully
    public void handleNullContext() {
        Context ctx = null;
        FingerprintData result = FpRequestProcessor.processRequestFp(ctx);
        assertDoesNotThrow(() -> FpRequestProcessor.processRequestFp(ctx));
        assertNull(result);
    }

    //Basic function test manually during runtime for now
    /* left now due to impractical test setup of http context
    @Test void handleValidContext() {
        Context ctx;
        FingerprintData result = RequestProcessor.processRequest(ctx);
        assertEquals(null, result);
    }
    */


}
