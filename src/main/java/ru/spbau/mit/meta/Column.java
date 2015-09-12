package ru.spbau.mit.meta;

/**
 * Created by John on 9/12/2015.
 */
public class Column {
    private String name;

    private final DataType dataType;

    public Column(String name, DataType dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public DataType getDataType() {
        return dataType;
    }
}
