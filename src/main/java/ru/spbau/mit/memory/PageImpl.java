package ru.spbau.mit.memory;

import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.Table;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.List;
import java.util.Map;


/**
 * PageImpl of byteBuffer
 * <p>
 * Created by John on 9/12/2015.
 */
public class PageImpl implements Page {
    private final int NEXT_PAGE_OFFSET = 4;
    private final int RECORD_COUNT_OFFSET = 6;
    private final int BIT_MASK_OFFSET = 134;

    private Table table;

    public void setTable(Table table) {
        this.table = table;
    }

    private BitSet bitSet;

    private ByteBuffer byteBuffer;

    private long operationId;
    private Short recordCount;
    private int id;

    private boolean dirty;
    private int pinCount;

    public PageImpl(byte[] data, Integer id) {
        assert data.length == SIZE;

        byteBuffer = ByteBuffer.wrap(data);

        this.id = id;
    }

    private BitSet getBitSet() {
        if (bitSet == null) {
            byte[] bytes = new byte[BIT_MASK_OFFSET - RECORD_COUNT_OFFSET];
            byteBuffer.get(bytes, Page.SIZE - BIT_MASK_OFFSET, BIT_MASK_OFFSET - RECORD_COUNT_OFFSET);
            bitSet = BitSet.valueOf(bytes);
        }
        return bitSet;
    }

    public ByteBuffer getByteBuffer() {
        return this.byteBuffer;
    }

    public short getRecordCount() {
        if (recordCount == null) {
            recordCount = byteBuffer.getShort(Page.SIZE - RECORD_COUNT_OFFSET);
        }
        return recordCount;
    }

    private void setRecordCount(short recordCount) {
        this.recordCount = recordCount;
        byteBuffer.putShort(Page.SIZE - RECORD_COUNT_OFFSET, recordCount);
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
    public long getLastOperationId() {
        return operationId;
    }

    @Override
    public void updateOperationId(Long operationId) {
        this.operationId = operationId;
    }

    @Override
    public List<Record> getAllRecords() {
        return null;
    }

    @Override
    public Record getRecord(Integer num) {
        assert (num < getRecordCount());
        //todo index before
        return null;
    }

    @Override
    public byte[] getData() {
        return byteBuffer.array();
    }

    private int getFirstFreePos() {
        BitSet bitSet = getBitSet();
        for (int i = 0; i < (Page.SIZE - BIT_MASK_OFFSET) / table.getRecordSize(); i++) {
            if (!bitSet.get(i)){
                return i;
            }
        }
        return -1;
    }

    @Override
    public void putRecord(Record record) {
        assert (getRecordCount() * (table.getRecordSize() + 1) > (Page.SIZE - BIT_MASK_OFFSET));

        byteBuffer.position(getFirstFreePos());

        Map<String, Object> values = record.getValues();
        for (Column column : table.getColumns()) {
            column.getDataType().putInPage(values.get(column.getName()), this);
        }

        makeDirty();
        setRecordCount((short) (getRecordCount() + 1));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PageImpl page = (PageImpl) o;

        return id == page.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
