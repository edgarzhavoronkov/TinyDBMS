package ru.spbau.mit.memory.page;

import ru.spbau.mit.memory.Record;
import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.Table;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * RecordPageImpl of byteBuffer
 * <p>
 * Created by John on 9/12/2015.
 */
public class RecordPageImpl extends BasePageImpl implements RecordPage {
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

    public RecordPageImpl(byte[] data, Integer id) {
        super(data, id);
    }

    public RecordPageImpl(BasePage basePage, Table table) {
        super(basePage.getData(), basePage.getId());
        operationId = ((BasePageImpl) basePage).operationId;
        dirty = ((BasePageImpl) basePage).dirty;
        pinCount = ((BasePageImpl) basePage).pinCount;
        setTable(table);
    }

    private BitSet getBitSet() {
        if (bitSet == null) {
            byte[] bytes = new byte[BIT_MASK_OFFSET - RECORD_COUNT_OFFSET];
            byteBuffer.position(SIZE - BIT_MASK_OFFSET);
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = byteBuffer.get();
            }
            bitSet = BitSet.valueOf(bytes);
        }
        return bitSet;
    }

    @Override
    public short getRecordCount() {
        if (recordCount == null) {
            recordCount = byteBuffer.getShort(SIZE - RECORD_COUNT_OFFSET);
        }
        return recordCount;
    }

    private void setRecordCount(short recordCount) {
        this.recordCount = recordCount;
        byteBuffer.putShort(SIZE - RECORD_COUNT_OFFSET, recordCount);
    }

    @Override
    public List<Record> getAllRecords() {
        return null;
    }

    @Override //TODO - very ineffective realization - cast data to array at initialize, better to use 134byte in
    public Record getRecord(Integer num) {
        assert (num < getRecordCount());
        int recordNum = getAbsRecordNum(num);

        Map<Column, Object> values = new HashMap<>(table.getColumns().size());
        byteBuffer.position(table.getRecordSize() * recordNum);
        for (Column column : table.getColumns()) {
            values.put(column, column.getDataType().getFromPage(this));
        }
        return new Record(values);
    }

    /**
     * return absolute number of record
     * in bitset can be free record
     * @param num of busy record
     * @return absolute number of record
     */
    private int getAbsRecordNum(int num) {
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
        return i;
    }

    @Override
    public void removeRecord(Integer num) {
        assert (num < getRecordCount());

        int recordNum = getAbsRecordNum(num);

        makeDirty();
        getBitSet().set(recordNum, false);
    }

    private int getFirstFreePos() {
        BitSet bitSet = getBitSet();
        return bitSet.nextClearBit(0);
    }

    @Override
    public void putRecord(Record record) {
        assert (isFreeSpace());

        int firstFreePos = getFirstFreePos();
        byteBuffer.position(firstFreePos * table.getRecordSize());

        Map<Column, Object> values = record.getValues();
        for (Column column : table.getColumns()) {
            column.getDataType().putInPage(values.get(column), this);
        }

        makeDirty();
        getBitSet().set(firstFreePos, true);
        setRecordCount((short) (getRecordCount() + 1));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecordPageImpl page = (RecordPageImpl) o;

        return id == page.id;
    }

    @Override
    public boolean hasNext(){
        return getNextPageId() != -1;
    }

    //todo - fix (next page)
    @Override
    public int getNextPageId(){
        if (nextPageId == null) {
            nextPageId = byteBuffer.getInt(SIZE - NEXT_PAGE_OFFSET);
        }
        return nextPageId;
    }

    @Override
    public void setNextPageId(Integer nextPageId) {
        this.nextPageId = nextPageId;
        byteBuffer.putInt(SIZE - NEXT_PAGE_OFFSET, nextPageId);
    }

    @Override
    public boolean isFreeSpace() {
        return getRecordCount() * (table.getRecordSize() + 1) < (SIZE - BIT_MASK_OFFSET);
    }

    @Override
    public void close() {
        super.close();
        //save bitSet
        byte[] bytes = getBitSet().toByteArray();
        byteBuffer.position(SIZE - BIT_MASK_OFFSET);
        byteBuffer.put(bytes, 0, bytes.length);
    }

}
