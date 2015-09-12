package ru.spbau.mit.memory;

import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.DataType;
import ru.spbau.mit.meta.Table;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static ru.spbau.mit.meta.DataType.INTEGER;

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

    private int getRecordCount(){
        return byteBuffer.getInt(Page.SIZE - 4);
    }

    private void setRecordCount(int recordCount) {
        byteBuffer.putInt(recordCount, Page.SIZE - 4);
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
            lastFreePos = getLastFreePos();
        }

        byteBuffer.position(lastFreePos);

        //todo: refactor maybe
        Map<String, Object> values = record.getValues();
        for (Column column : table.getColumns()) {
            switch (column.getDataType()) {
                case INTEGER:
                    put((Integer) values.get(column.getName()));
                    break;
                case DOUBLE:
                    put((Double) values.get(column.getName()));
                    break;
                case VARCHAR:
                    put((String) values.get(column.getName()));
                    break;
                default:
                    put(values.get(column.getName()));
            }
        }

    }

    private void put(Object value) {
        throw new ClassCastException("Invalid class " + value.getClass());
    }

    private void put(Integer value) {
        byteBuffer.putInt(value);
    }

    private void put(String value) {
        byteBuffer.put(value.getBytes(Charset.defaultCharset()));
    }

    private void put(Double value) {
        byteBuffer.putDouble(value);
    }

    private int getLastFreePos() {
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
