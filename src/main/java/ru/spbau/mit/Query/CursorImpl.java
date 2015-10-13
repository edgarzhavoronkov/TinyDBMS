package ru.spbau.mit.Query;

import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.memory.Page;
import ru.spbau.mit.memory.Record;
import ru.spbau.mit.meta.Table;

import java.io.IOException;

/**
 * Created by gellm_000 on 09.10.2015.
 */
public class CursorImpl implements Cursor {
    private final Table table;
    private Integer pageId, offset;
    private Page currentPage = null;
    private final BufferManager bufferManager;
    private Record currentRecord;

    public CursorImpl(BufferManager bufferManager, Table table, Integer pageId, Integer offset) throws IOException {
        this(bufferManager, table);
        this.pageId = pageId;
        this.offset = offset;
        initiateCursor(pageId, offset);
    }

    public CursorImpl(BufferManager bufferManager, Table table){
        this.table = table;
        this.bufferManager = bufferManager;
    }

    @Override
    public void initiateCursor(Integer pageId, Integer offset) throws IOException {
        this.pageId = pageId;
        this.offset = offset;
        start();
    }

    private void start() throws IOException {
        this.currentPage = bufferManager.getPage(pageId, table);
        currentPage.pin();
        currentRecord = currentPage.getRecord(offset);
    }

    public Record value(){
        return currentRecord;
    }

    @Override
    public boolean hasNext() {
        return ((offset + 1) < currentPage.getRecordCount() || currentPage.hasNext());
    }

    @Override
    public Object next() {
        if((offset + 1) < currentPage.getRecordCount()){
            offset ++;
        }else{
            currentPage.unpin();
            try {
                currentPage = bufferManager.getPage(currentPage.getNextPageId(), table);
            } catch (IOException e) {
                e.printStackTrace();
            }
            currentPage.pin();
            offset = 0;
        }
        return  currentPage.getRecord(offset);
    }

    @Override
    public void remove() {
        //todo
    }
}
