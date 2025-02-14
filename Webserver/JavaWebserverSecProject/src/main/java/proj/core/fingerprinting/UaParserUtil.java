package proj.core.fingerprinting;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proj.entities.UserAgent;
import ua_parser.Client;
import ua_parser.Parser;

/**
    * Utility class to parse UserAgent Strings using the ua_parser Java Library
    * https://github.com/ua-parser/uap-java
 */
public class UaParserUtil {
    private static final ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
    private static final Logger logger = loggerFactory.getLogger(UaParserUtil.class.getName());

    /**
     * Parses a UserAgent String and returns a UserAgent Object
     * @param userAgent UserAgent String to be parsed
     * @return UserAgent Object, null if parsing failed
     */
    public static UserAgent parse (String userAgent){
        Parser uaParser = new Parser();
        Client c = uaParser.parse(userAgent);
        //System.out.println("Browser: " + c.userAgent.family);
        //System.out.println("Browser Version: " + c.userAgent.major);
        //System.out.println("Platform: " + c.os.family);
        if (c.userAgent == null){
            logger.error("Failed to parse UserAgent String");
            return null;
        }else{
            logger.trace("UserAgent String parsed successfully");
            return new UserAgent(c.userAgent.family, c.userAgent.major, c.os.family);
        }
    }

}
