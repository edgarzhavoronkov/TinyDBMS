package ru.spbau.mit.meta;

/**
 * Created by John on 9/12/2015.
 */
public class Column {
    private String name;

    private final DataType dataType;

    private Integer size;

    public Column(String name, DataType dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    public void setName(String name) { this.name = name; }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public DataType getDataType() {
        return dataType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Column column = (Column) o;

        return name.equals(column.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
