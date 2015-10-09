package ru.spbau.mit.meta;

import java.util.List;

/**
 * Created by John on 9/12/2015.
 */
public class Table {

    private String name;

    private int firstPageId = -1;

    private int firstFreePageId = -1;

    private List<Column> columns;

    private int recordSize = 0;

    public Table(String name, int firstPageId, int firstFreePageId, List<Column> columns) {
        this.name = name;
        this.firstPageId = firstPageId;
        this.firstFreePageId = firstFreePageId;
        this.columns = columns;

        for (Column column : columns) {
            recordSize += column.getDataType().getSize();
        }
    }

    public Integer getRecordSize() {
        return recordSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
}
