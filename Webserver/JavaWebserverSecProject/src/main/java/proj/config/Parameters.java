package proj.config;

public enum Parameters {

    //set to true for HTTPS and false for HTTP
    HTTPS(false),
    //server address and port
    // The _SECURE versions are used when switching to https
    ADDRESS("localhost"),
    ADDRESS_SECURE("X"),
    PORT(3000),
    PORT_SECURE(5),
    //file & folder dirs
    PATH_WS_STATIC("/static"),
    PATH_RELATIVE_USER_DB("./src/main/resources/persistence/userDB.json"),
    PATH_RELATIVE_USERSPACE_HTML("./src/main/resources/static/restricted/userSpace.html");
    private final Object value;
    Parameters(Object value) {
        this.value = value;
    }
    public Object getValue() {
        return value;
    }
}
