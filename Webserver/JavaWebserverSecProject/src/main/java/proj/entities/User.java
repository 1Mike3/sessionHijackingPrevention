package proj.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * User class
 * This class is used to represent a User object.
 * It contains the attributes username, passwordHashed and loginToken.
 */
@Getter
public class User {
    //Attributes
    private String username;
    private String passwordHashed;
    private String sessionToken;

    // Fingerprinting Criteria

    private String IP;

    //Constructor
    @JsonCreator
    public User(@JsonProperty("username") String username,
                @JsonProperty("passwordHashed") String passwordHashed,
                @JsonProperty("loginToken") String loginToken,
                @JsonProperty("IP") String IP)
    {
        this.username = username;
        this.passwordHashed = passwordHashed;
        this.sessionToken = loginToken;
        this.IP = IP;
    }



    //Setters
    //Only added the setter for loginToken as the other attributes should remain constant after creation
    public void setLoginToken(String loginToken) {
        this.sessionToken = loginToken;
    }

    //For debug prints
    @Override
    public String toString(){
        return String.format("""
                username: %s
                password: %s
                token: %s
                IP: %s
                """,username,passwordHashed,sessionToken,IP);
    }

}
