package ru.spbau.mit.cursors;

import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.memory.Record;
import ru.spbau.mit.memory.page.RecordPage;
import ru.spbau.mit.memory.page.RecordPageImpl;
import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gellm_000 on 09.10.2015.
 */
public class FullScanCursor implements Cursor {
    private final Table table;
    private Integer pageId, offset;
    private RecordPage currentRecordPage = null;
    private final BufferManager bufferManager;
    private final List<Column> fields;
    private Record currentRecord;

    @Override
    public Record getCurrentRecord() {
        return currentRecord;
    }

    @Override
    public BufferManager getBufferManager() {
        return bufferManager;
    }

    @Override
    public Table getTable() {
        return table;
    }

    public Integer getPageId() {
        return pageId;
    }

    public Integer getOffset() {
        return offset;
    }

    public FullScanCursor(BufferManager bufferManager, Table table, Integer pageId, Integer offset) throws IOException {
        this(bufferManager, table);
        this.pageId = pageId;
        this.offset = offset;
        initiateCursor(pageId, offset);
    }

    public FullScanCursor(BufferManager bufferManager, Table table){
        this.table = table;
        fields = new ArrayList<>(table.getColumns());
        this.bufferManager = bufferManager;
    }

    @Override
    public void initiateCursor(Integer pageId, Integer offset) throws IOException {
        this.pageId = pageId;
        this.offset = offset;
        start();
    }


    private void start() throws IOException {
        this.currentRecordPage = new RecordPageImpl(bufferManager.getPage(pageId), table);
        currentRecordPage.pin();
//        currentRecord = currentRecordPage.getRecord(offset);
    }

    public Record value(){
        return currentRecord;
    }

    @Override
    public boolean hasNext() {
        return (offset < currentRecordPage.getRecordCount() || currentRecordPage.hasNext());
    }

    @Override
    public Object next() {
        if (!hasNext()) return null;
        if(offset >= currentRecordPage.getRecordCount()){
            currentRecordPage.unpin();
            try {
                currentRecordPage = new RecordPageImpl(bufferManager.getPage(currentRecordPage.getNextPageId()), table);
            } catch (IOException e) {
                e.printStackTrace();
            }
            currentRecordPage.pin();
            offset = 0;
        }

        currentRecord = currentRecordPage.getRecord(offset);
        offset ++;
        return currentRecord;
    }


    @Override
    public void remove() {
        //todo
    }
}
