package ru.spbau.mit.memory;

import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.Table;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * PageImpl of byteBuffer
 * <p>
 * Created by John on 9/12/2015.
 */
public class PageImpl implements Page {
    //todo must use next page offset
    private final int NEXT_PAGE_OFFSET = 4;
    private final int RECORD_COUNT_OFFSET = 6;
    private final int BIT_MASK_OFFSET = 134;

    private Table table;

    public void setTable(Table table) {
        this.table = table;
    }

    //Save 1 - for used positions; 0 - for free
    private BitSet bitSet;
    private Short recordCount;
    private Integer nextPageId;

    private ByteBuffer byteBuffer;
    private long operationId;
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
            byteBuffer.position(Page.SIZE - BIT_MASK_OFFSET);
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = byteBuffer.get();
            }
            bitSet = BitSet.valueOf(bytes);
        }
        return bitSet;
    }

    public ByteBuffer getByteBuffer() {
        return this.byteBuffer;
    }

    @Override
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

    @Override //TODO - very ineffective realization - cast data to array at initialize, better to use 134byte in
    public Record getRecord(Integer num) {
        assert (num < getRecordCount());
        int cur = 0;
        int i;

        for (i = 0; i < getBitSet().size(); i++) {
            if (getBitSet().get(i)) {
                cur++;
            }
            if (cur == (num + 1)) {
                break;
            }
        }

        Map<Column, Object> values = new HashMap<>(table.getColumns().size());
        byteBuffer.position(table.getRecordSize() * i);
        for (Column column : table.getColumns()) {
            values.put(column, column.getDataType().getFromPage(this));
        }
        return new Record(values);
    }

    @Override
    public byte[] getData() {
        return byteBuffer.array();
    }

    private int getFirstFreePos() {
        BitSet bitSet = getBitSet();
        for (int i = 0; i < (Page.SIZE - BIT_MASK_OFFSET) / table.getRecordSize(); i++) {
            if (!bitSet.get(i)) {
                return i;
            }
        }
        return -1;

    }

    @Override
    public void putRecord(Record record) {
        assert (getRecordCount() * (table.getRecordSize() + 1) > (Page.SIZE - BIT_MASK_OFFSET));

        int firstFreePos = getFirstFreePos();
        byteBuffer.position(firstFreePos);

        Map<Column, Object> values = record.getValues();
        for (Column column : table.getColumns()) {
            column.getDataType().putInPage(values.get(column), this);
        }

        makeDirty();
        getBitSet().set(firstFreePos, true);
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
        return dirty;
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

    @Override
    public boolean hasNext(){
        return nextPageId != -1;
    }

    //todo - fix
    @Override
    public int getNextPageId(){
        if (nextPageId == null) {
            nextPageId = byteBuffer.getInt(Page.SIZE - NEXT_PAGE_OFFSET);
        }
        return nextPageId;
    }

    @Override
    public void setNextPageId(Integer nextPageId) {
        this.nextPageId = nextPageId;
        byteBuffer.putInt(Page.SIZE - NEXT_PAGE_OFFSET, nextPageId);
    }

    public void close() {
        //todo add save bitSet
    }

}
