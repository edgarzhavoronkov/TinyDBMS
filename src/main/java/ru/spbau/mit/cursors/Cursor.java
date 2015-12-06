package ru.spbau.mit.cursors;

import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.memory.Record;
import ru.spbau.mit.meta.Table;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by wimag - future cursor functionaloty on 09.10.2015.
 */
public interface Cursor extends Iterator{
    Record getCurrentRecord();

    BufferManager getBufferManager();

    Table getTable();

    Integer getPageId();

    Integer getOffset();

    void initiateCursor(Integer pageId, Integer offset) throws IOException;

    //TODO - later on, when we have not select * - pass list of columns to select to cursor
}
