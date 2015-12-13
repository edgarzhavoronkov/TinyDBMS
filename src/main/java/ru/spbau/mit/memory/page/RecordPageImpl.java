package ru.spbau.mit.memory.page;

import ru.spbau.mit.cursors.Index.BTree.LeafEntry;
import ru.spbau.mit.memory.Record;
import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.Table;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;


/**
 * RecordPageImpl of byteBuffer
 * <p>
 * Created by John on 9/12/2015.
 */
public class RecordPageImpl implements RecordPage {
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
    private BasePage page;

    public RecordPageImpl(BasePage basePage, Table table) {
        page = basePage;
        setTable(table);
        ((BasePageImpl) basePage).setAfterClose(this::flush);
    }

    @Override
    public ByteBuffer getByteBuffer() {
        return page.getByteBuffer();
    }

    @Override
    public byte[] getData() {
        return page.getData();
    }

    @Override
    public int getId() {
        return page.getId();
    }

    @Override
    public void makeDirty() {
        page.makeDirty();
    }

    @Override
    public void makeClean() {
        page.makeClean();
    }

    @Override
    public boolean isDirty() {
        return page.isDirty();
    }

    @Override
    public void pin() {
        page.pin();
    }

    @Override
    public void unpin() {
        page.unpin();
    }

    @Override
    public boolean isPin() {
        return page.isPin();
    }

    @Override
    public long getLastOperationId() {
        return page.getLastOperationId();
    }

    @Override
    public void updateOperationId(Long id) {
        page.updateOperationId(id);
    }

    private BitSet getBitSet() {
        if (bitSet == null) {
            byte[] bytes = new byte[BIT_MASK_OFFSET - RECORD_COUNT_OFFSET];
            page.getByteBuffer().position(SIZE - BIT_MASK_OFFSET);
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = page.getByteBuffer().get();
            }
            bitSet = BitSet.valueOf(bytes);
        }
        return bitSet;
    }

    @Override
    public short getRecordCount() {
        if (recordCount == null) {
            recordCount = page.getByteBuffer().getShort(SIZE - RECORD_COUNT_OFFSET);
        }
        return recordCount;
    }

    private void setRecordCount(short recordCount) {
        this.recordCount = recordCount;
    }

    @Override //TODO - very ineffective realization - cast data to array at initialize, better to use 134byte in
    public Record getRecord(Integer num) {
        assert (num < getRecordCount());
        int recordNum = getAbsRecordNum(num);

        Map<Column, Object> values = new HashMap<>(table.getColumns().size());
        page.getByteBuffer().position(table.getRecordSize() * recordNum);
        for (Column column : table.getColumns()) {
            values.put(column, column.getDataType().getFromPage(this));
        }
        return new Record(values);
    }

    @Override
    public Record getRecordByAbsolutePosition(Integer num){
        if(getBitSet().get(num)){
            int position = num*table.getRecordSize();
            page.getByteBuffer().position(position);
            Map<Column, Object> values = new HashMap<>(table.getColumns().size());
            for (Column column : table.getColumns()) {
                values.put(column, column.getDataType().getFromPage(this));
            }
            return new Record(values);
        }
        return null;
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
    public LeafEntry putRecord(Record record) {
        assert (isFreeSpace());

        int firstFreePos = getFirstFreePos();
        page.getByteBuffer().position(firstFreePos * table.getRecordSize());

        Map<Column, Object> values = record.getValues();
        for (Column column : table.getColumns()) {
            column.getDataType().putInPage(values.get(column), this);
        }

        makeDirty();
        getBitSet().set(firstFreePos, true);
        setRecordCount((short) (getRecordCount() + 1));
        return new LeafEntry(getId(), firstFreePos);
    }



    @Override
    public boolean hasNext(){
        return getNextPageId() != -1;
    }

    //todo - fix (next page)
    @Override
    public int getNextPageId(){
        if (nextPageId == null) {
            nextPageId = page.getByteBuffer().getInt(SIZE - NEXT_PAGE_OFFSET);
        }
        return nextPageId;
    }

    @Override
    public void setNextPageId(Integer nextPageId) {
        this.nextPageId = nextPageId;
    }

    @Override
    public boolean isFreeSpace() {
        return getRecordCount() * (table.getRecordSize() + 1) < (SIZE - BIT_MASK_OFFSET);
    }

    @Override
    public void flush() {
        //save bitSet
        byte[] bytes = getBitSet().toByteArray();
        page.getByteBuffer().position(SIZE - BIT_MASK_OFFSET);
        page.getByteBuffer().put(bytes, 0, bytes.length);
        page.getByteBuffer().putInt(SIZE - NEXT_PAGE_OFFSET, getNextPageId());
        page.getByteBuffer().putShort(SIZE - RECORD_COUNT_OFFSET, getRecordCount());
    }

}
