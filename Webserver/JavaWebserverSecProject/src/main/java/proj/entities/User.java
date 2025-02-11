package proj.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * User class
 * This class is used to represent a User object.
 * It contains the attributes username, passwordHashed and loginToken.
 */
@Getter
@Setter
@AllArgsConstructor
public class User {
    //Attributes
    private String username;
    private String passwordHashed;
    private String sessionToken;
    private FingerprintData fdt;
    //For debug prints
    @Override
    public String toString(){
        return String.format("""
                username: %s
                password: %s
                token: %s
                IP: %s
                """,username,passwordHashed,sessionToken,fdt.hashCode());
    }
}
