/*
ABOUT
- managing user login sessions
- contains static and non-static methods for interacting with cookies
- because cookies are only used by this class general functions are also grouped in with this class
 */

//constants
// ... poor mans's enum
const SESSION_CONSTRUCTOR_MODI = Object.freeze({
    CREATE_FROM_LOGIN_REQUEST: 1,
    CREATE_FROM_STORED_TOKEN: 2,
});

class Session {

    /*
    constructor for the session class.
    depending on the modi (see SESSION_CONSTRUCTOR_MODI) the constructor either constructs
    a session from a new login request or from an existing token on startup.
     */
    constructor(modi, username, token) {
        switch (modi) {
            case SESSION_CONSTRUCTOR_MODI.CREATE_FROM_LOGIN_REQUEST:
                this.username = username;
                this.token = token;
                break;
            case SESSION_CONSTRUCTOR_MODI.CREATE_FROM_STORED_TOKEN:
                this.loadSessionFromCookie();
                break;
            default:
                console.log("Session.Constructor: Error invalid Modi!")
                break;
        }
    }

    //getters
    getUsername() {
        return this.username
    }

    getToken() {
        return this.token
    }

    //Store the isntance of the session as cookie
    storeSessionAsCookie(days = 30) {
        // object containing Session data
        const sessionData = {
            username: this.username,
            token: this.token,
        };

        // Convert session data to a JSON string
        const jsonData = JSON.stringify(sessionData);

        // Set an expiration date for the cookie (in days)
        const date = new Date();
        date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
        const expires = `expires=${date.toUTCString()}`;

        // Store the JSON data as a cookie
        document.cookie = `session=${encodeURIComponent(jsonData)}; ${expires}; path=/`;
    }


    // Method to load session from cookie, used in constructor
    loadSessionFromCookie() {
        const cookies = document.cookie.split("; ");
        for (let cookie of cookies) {
            const [name, value] = cookie.split("=");
            if (name === "session") {
                try {
                    const sessionData = JSON.parse(decodeURIComponent(value));
                    this.username = sessionData.username || null;
                    this.token = sessionData.token || null;
                    console.log("Session loaded from cookie:", this.username, this.token);
                } catch (error) {
                    console.error("Failed to parse session cookie:", error);
                }
                break;
            }
        }
    }


    //checks if a cookie is stored for the website
    static cookieExists(){

    }
    // Method which retrieves a stored cookie (relevant info), if there is any for outside prcessing
    static cookieGetInfo(){

    }
    //Destroys the stored cookie
    static cookieKill(){

    }


    static onStartup() {
        //TODO check if cookie token present if so load and make request to backend
        //TODO if so create new session object with response from backend
    }


}


