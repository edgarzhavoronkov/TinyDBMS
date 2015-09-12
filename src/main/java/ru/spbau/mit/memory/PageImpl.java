package ru.spbau.mit.memory;

import ru.spbau.mit.meta.Table;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * PageImpl of byteBuffer
 *
 * Created by John on 9/12/2015.
 */
public class PageImpl implements Page {
    public static final Integer PAGE_SIZE = 4 * 1024;

    private ByteBuffer byteBuffer;

    private int id;

    private boolean dirty;
    private boolean pinned;

    public PageImpl(byte[] data, int id) {
        assert data.length == PAGE_SIZE;

        byteBuffer = ByteBuffer.wrap(data);
        id = byteBuffer.getInt(0);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    @Override
    public List<Record> getAllRecords() {
        return null;
    }

    @Override
    public Record getRecord(Integer num, Table table) {
        return null;
    }

}
