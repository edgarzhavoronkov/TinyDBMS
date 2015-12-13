package ru.spbau.mit.memory.page;

import ru.spbau.mit.cursors.Index.BTree.LeafEntry;
import ru.spbau.mit.memory.Record;
import ru.spbau.mit.meta.Table;

/**
 * Interface for page
 *
 * Created by John on 9/12/2015.
 */
public interface RecordPage extends BasePage {

    short getRecordCount();

    Record getRecord(Integer num);

    //todo (low) better table inference implementation
    void setTable(Table table);

    Record getRecordByAbsolutePosition(Integer num);

    void removeRecord(Integer num);

    LeafEntry putRecord(Record record);

    boolean hasNext();

    int getNextPageId();

    void setNextPageId(Integer nextPageId);

    boolean isFreeSpace();
}
