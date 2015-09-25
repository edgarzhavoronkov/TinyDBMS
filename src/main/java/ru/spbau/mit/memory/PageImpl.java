package ru.spbau.mit.memory;

import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.Table;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;



/**
 * PageImpl of byteBuffer
 *
 * Created by John on 9/12/2015.
 */
public class PageImpl implements Page {
    private ByteBuffer byteBuffer;

    private int id;

    private boolean dirty;
    private int pinCount;

    public PageImpl(byte[] data, Integer id) {
        assert data.length == SIZE;

        byteBuffer = ByteBuffer.wrap(data);
        this.id = id;
    }

    public ByteBuffer getByteBuffer() {
        return this.byteBuffer;
    }

    private int getRecordCount(){
        return byteBuffer.getInt(Page.SIZE - Integer.BYTES);
    }

    private void setRecordCount(int recordCount) {
        byteBuffer.putInt(recordCount, Page.SIZE - Integer.BYTES);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void pin() {
        pinCount++;
    }

    @Override
    public void unpin() {
        assert pinCount > 0;
        pinCount--;
    }

    @Override
    public boolean isPin() {
        return pinCount > 0;
    }

    @Override
    public List<Record> getAllRecords(Table table) {
        return null;
    }

    @Override
    public Record getRecord(Integer num, Table table) {
        assert (num < getRecordCount());
        return null;
    }

    @Override
    public byte[] getData() {
        return byteBuffer.array();
    }

    @Override
    public void putRecord(Record record, Table table) {
        int recordCount = getRecordCount();

        int lastFreePos = 0;
        if (recordCount > 0) {
            lastFreePos = getLastFreePos(table);
        }

        byteBuffer.position(lastFreePos);

        Map<String, Object> values = record.getValues();
        for (Column column : table.getColumns()) {
            column.getDataType().putInPage(values.get(column.getName()), this);
        }

        setRecordCount(getRecordCount() + 1);
    }

    private int getLastFreePos(Table table) {
        int recordCount = getRecordCount();
        return byteBuffer.getInt(Page.SIZE - (recordCount * 4));
    }

    @Override
    public void makeDirty() {
        dirty = true;
    }

    @Override
    public void makeClean() {
        dirty = false;
    }

    @Override
    public boolean isDirty() {
        return false;
    }

}
