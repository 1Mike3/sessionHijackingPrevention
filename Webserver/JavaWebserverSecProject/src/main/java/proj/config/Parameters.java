package proj.config;

public enum Parameters {



    //server address and port
    ADDRESS("localhost"),
    PORT(3000),
    //file dir
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
