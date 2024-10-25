package proj.entities;

public class User {
    //Attributes
    private String username;
    private String passwordHashed;
    private String loginToken;

    //Constructor
    public User(String username, String passwordHashed, String loginToken) {
        this.username = username;
        this.passwordHashed = passwordHashed;
        this.loginToken = loginToken;
    }

}
