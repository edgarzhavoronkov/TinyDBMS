package ru.spbau.mit.memory;

import ru.spbau.mit.meta.Table;

import java.util.List;

/**
 * Interface for page
 *
 * Created by John on 9/12/2015.
 */
public interface Page {

    public static final Integer SIZE = 4 * 1024;

    List<Record> getAllRecords(Table table);

    Record getRecord(Integer num, Table table);

    byte[] getData();

    int getId();

    void putRecord(Record record, Table table);

    void makeDirty();

    void makeClean();

    boolean isDirty();

    void pin();

    void unpin();

    boolean isPin();
}
