package proj.entities;

import lombok.Getter;

/**
 * UserAgent Class
 * "immutable" instead of changing values create new Object
 * This is a representaion of a parsed User Agent string from a HTTP Request
 */
@Getter
public class UserAgent {
    private final String Browser;
    private final String BrowserVersion;
    private final String Platform;

    /**
     * Constructor, creates new UserAgent Object from ua-parser
     * @param Browser Name of the Browser used
     * @param BrowserVersion Version of the Browser used
     * @param Platform Platform used
     */
    public UserAgent(String Browser, String BrowserVersion, String Platform){
        this.Browser = Browser;
        this.BrowserVersion = BrowserVersion;
        this.Platform = Platform;
    }
}
