package proj.config;

public enum Parameters {

    //TODO !! Do NOT forget to change in JS aswell !!

    //server address and port
    ADDRESS("localhost"),
    PORT(3000),
    //file dir
    FILE_DIR("/static"),
    USER_DB_PATH("/persistence/userDB.json");


    private final Object value;
    Parameters(Object value) {
        this.value = value;
    }
    public Object getValue() {
        return value;
    }
}
