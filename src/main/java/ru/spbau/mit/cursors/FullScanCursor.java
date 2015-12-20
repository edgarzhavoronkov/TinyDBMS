package ru.spbau.mit.cursors;

import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.memory.Record;
import ru.spbau.mit.memory.page.RecordPage;
import ru.spbau.mit.memory.page.RecordPageImpl;
import ru.spbau.mit.meta.Table;

import java.io.IOException;

/**
 * Created by gellm_000 on 09.10.2015.
 */
public class FullScanCursor implements Cursor {
    private final Table table;
    private Integer pageId, offset;
    private Integer initialPageId, initialOffset;
    private RecordPage currentRecordPage = null;
    private final BufferManager bufferManager;
    private Record currentRecord;

    @Override
    public Record getCurrentRecord() {
        return currentRecord;
    }

    public BufferManager getBufferManager() {
        return bufferManager;
    }

    public Table getTable() {
        return table;
    }

    public Integer getPageId() {
        return currentRecordPage.getId();
    }

    public Integer getOffset() {
        return offset;
    }

    public Cursor clone() {
        try {
            return new FullScanCursor(getBufferManager(), getTable(), getPageId(), getOffset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void reset() {
        this.pageId = initialPageId;
        this.offset = initialOffset;
    }

    public FullScanCursor(BufferManager bufferManager, Table table, Integer pageId, Integer offset) throws IOException {
        this(bufferManager, table);
        this.pageId = pageId;
        this.offset = offset;
        this.initialPageId = pageId;
        this.initialOffset = offset;
        initiateCursor(pageId, offset);
    }

    public FullScanCursor(BufferManager bufferManager, Table table){
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
        this.currentRecordPage = new RecordPageImpl(bufferManager.getPage(pageId), table);
        currentRecordPage.pin();
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
