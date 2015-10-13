package ru.spbau.mit.memory;

import ru.spbau.mit.meta.Table;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Interface for page
 *
 * Created by John on 9/12/2015.
 */
public interface Page {

    public short getRecordCount();

    public static final Integer SIZE = 4 * 1024;

    List<Record> getAllRecords();

    Record getRecord(Integer num);

    ByteBuffer getByteBuffer();

    //todo better table inference implementation
    void setTable(Table table);

    byte[] getData();

    int getId();

    void putRecord(Record record);

    void makeDirty();

    void makeClean();

    boolean isDirty();

    void pin();

    void unpin();

    boolean isPin();

    long getLastOperationId();

    void updateOperationId(Long id);

    boolean hasNext();

    int getNextPageId();

    void setNextPageId(Integer nextPageId);
}
