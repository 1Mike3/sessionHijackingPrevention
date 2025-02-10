package proj.core;
import io.javalin.http.Context;
import proj.core.web.GeolocationProcessing;
import proj.entities.FingerprintData;
import proj.entities.Location;
import proj.util.UaParserUtil;

import java.util.Objects;


/**
    * - Processes metadata from incoming requests
    * - Involved in handling of the fingerprint data
 */
public class RequestProcessor {

    //Instance of GeolocationProcessing passed from main
    private static final GeolocationProcessing geolocation_inst = Main.getGeolocation_instance();
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RequestProcessor.class);
    //Ua-Parser is static Method

    /**
     * - Creates a FingerprintData object from the incoming request
     * - Does all the initial processing from the request to a Object containing all the data that could be obtained
     * @param ctx http header context is being inserted into the function to be parsed
     * @return FingerprintData on successful processing, null otherwise
     */
    public static FingerprintData processRequest(Context ctx) {
        if (geolocation_inst == null) {
            logger.error("GeolocationProcessing instance is null, cannot process request, may not be initialized in Main");
            return null;
        }
        logger.trace("Begin Processing Request to: " + ctx.path());
        FingerprintData.FingerprintDataBuilder builder = FingerprintData.builder();
        builder.IP(ctx.header("X-Forwarded-For"));
        builder.accept(ctx.header("Accept"));
        builder.encoding(ctx.header("Accept-Encoding"));
        //Obtaining and assigning Location
            Location locTemp = null;
            try {
                locTemp = geolocation_inst.getCoordinates(ctx.header("X-Forwarded-For"));
                if(locTemp == null){
                    throw new Exception("Geolocation get Coordinates returned null");
                }
            }catch (Exception e){
                logger.error("Failed to get coordinates from IP" + e.getMessage());
            }
            builder.location(locTemp);
        builder.screen(ctx.header("Screen"));
        builder.language(ctx.header("Accept-Language"));
        builder.timezone(ctx.header("Timezone"));
        //Parsing UserAgent
            builder.userAgent(UaParserUtil.parse(ctx.header("User-Agent")));
        builder.canvas(ctx.header("Canvas"));
        builder.webglVendor(ctx.header("WebGL-Vendor"));
        builder.webglRenderer(ctx.header("WebGL-Renderer"));
        builder.deviceMemory(ctx.header("Device-Memory"));
        builder.cookiesAccepted(
            ctx.header("Cookies-Accepted") != null &&
            Objects.requireNonNull(ctx.header("Cookies-Accepted")).contains("cookiesAccepted=true")
        );
        return builder.build();
    }
}
