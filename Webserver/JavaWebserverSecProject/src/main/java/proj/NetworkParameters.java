package proj;

public enum NetworkParameters {

    //TODO !! Do NOT forget to change in JS aswell !!

    //PARAM server address and port
    ADDRESS("localhost"),
    PORT(3000),
    //PARAM file dir
    FILE_DIR("/static");

    private final Object value;
    NetworkParameters(Object value) {
        this.value = value;
    }
    public Object getValue() {
        return value;
    }
}
