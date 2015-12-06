package ru.spbau.mit.memory;

import ru.spbau.mit.meta.Table;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Interface for page
 *
 * Created by John on 9/12/2015.
 */
public interface RecordPage extends BasePage {

    short getRecordCount();

    List<Record> getAllRecords();

    Record getRecord(Integer num);

    //todo (low) better table inference implementation
    void setTable(Table table);

    void removeRecord(Integer num);

    void putRecord(Record record);

    boolean hasNext();

    int getNextPageId();

    void setNextPageId(Integer nextPageId);

    boolean isFreeSpace();
}
