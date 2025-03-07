package proj.core.fingerprinting;
import io.javalin.http.Context;
import proj.core.Main;
import proj.entities.FingerprintData;
import proj.entities.Location;

import java.util.Objects;


/**
    * - Processes metadata from incoming requests
    * - Involved in handling of the fingerprint data
 */
public class FpRequestProcessor {

    //Instance of GeolocationProcessing passed from main
    private static final GeolocationProcessing geolocation_inst = Main.getGeolocation_instance();
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FpRequestProcessor.class);
    //Ua-Parser is static Method

    /**
     * - Creates a FingerprintData object from the incoming request
     * - Does all the initial processing from the request to a Object containing all the data that could be obtained
     * @param ctx http header context is being inserted into the function to be parsed
     * @return FingerprintData on successful processing, null otherwise
     */
    public static FingerprintData processRequestFp(Context ctx) {
        //Reacting to geolocation instance not being initialized
        if (geolocation_inst == null) {
            logger.error("GeolocationProcessing instance is null, cannot process request, may not be initialized in Main");
            return null;
        }
        //Reacting to cookies not beeing accepted
        //(If they are not accepted this is the only information that is collected and the rest is set to null)
        if (ctx.header("Cookie") != null && !Objects.requireNonNull(ctx.header("Cookie")).contains("cookiesAccepted=true")) {
            logger.warn("Cookies not accepted!, only returning object with cookiesAccepted=false");
            return FingerprintData.builder().cookiesAccepted(false).build();
        }
        logger.trace("Begin Processing Request to: " + ctx.path());
        FingerprintData.FingerprintDataBuilder builder = FingerprintData.builder();

        builder.IP(ctx.header("X-Forwarded-For"));

        //Simple Hash code is created for both accept headers to save db space
        String accept = ctx.header("Full-Accept");
        logger.trace("Accept Received: " + accept);
        //.hash code not crypt sec. but for this enough
        accept = accept == null ? "unknown" : String.valueOf(accept.hashCode());
        builder.accept(accept);
        String encoding = ctx.header("Accept-Encoding");
        encoding = encoding == null ? "unknown" : String.valueOf(encoding.hashCode());
        builder.encoding(encoding);

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
        builder.screen(ctx.header("Screen-Resolution"));
        builder.language(ctx.header("Accept-Language"));
        builder.timezone(ctx.header("Timezone"));
        //Parsing UserAgent
            builder.userAgent(UaParserUtil.parse(ctx.header("User-Agent")));
        builder.canvas(ctx.header("Canvas"));
        builder.webglVendor(ctx.header("WebGL-Vendor"));
        builder.webglRenderer(ctx.header("WebGL-Renderer"));
        builder.deviceMemory(ctx.header("Device-Memory"));
        builder.cookiesAccepted(
            ctx.header("Cookie") != null &&
            Objects.requireNonNull(ctx.header("Cookie")).contains("cookiesAccepted=true")
        );
        logger.trace("Request processed successfully");
        return builder.build();
    }
}
