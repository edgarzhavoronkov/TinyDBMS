package ru.spbau.mit.meta;

/**
 * Created by John on 9/12/2015.
 */
public enum DataType {
    INTEGER (Integer.class),
    DOUBLE (Double.class),
    VARCHAR (String.class);

    private final Class<?> type;

    DataType(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }
}
