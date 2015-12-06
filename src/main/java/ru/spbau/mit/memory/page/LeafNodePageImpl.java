package ru.spbau.mit.memory.page;

import ru.spbau.mit.cursors.Index.BTree.LeafEntry;

/**
 * Created by John on 12/6/2015.
 */
public class LeafNodePageImpl extends NodePageImpl implements LeafNodePage {
    private static final int EMPTY_ENTRY_ID = -1;
    private static final int ENTRY_OFFSET = KEYS_OFFSET + KEYS_CAPACITY * Integer.BYTES;

    private LeafEntry[] entries;

    public LeafNodePageImpl(byte[] data, Integer id) {
        super(data, id);
    }

    public LeafNodePageImpl(BasePage basePage) {
        super(basePage);
    }

    @Override
    public void close() {
        super.close();
        byteBuffer.position(ENTRY_OFFSET);
        for (LeafEntry entry : entries) {
            if (entry == null) {
                byteBuffer.putInt(EMPTY_ENTRY_ID);
            } else {
                byteBuffer.putInt(entry.getPageId());
                byteBuffer.putInt(entry.getOffset());
            }
        }
    }

    private LeafEntry[] getEntries() {
        if (entries == null) {
            byteBuffer.position(ENTRY_OFFSET);
            entries = new LeafEntry[ENTRY_CAPACITY];
            for (int i = 0; i < entries.length; i++) {
                int pageId = byteBuffer.getInt();
                if (pageId == EMPTY_ENTRY_ID) continue; //means LeafEntry is null
                int offset = byteBuffer.getInt();
                entries[i] = new LeafEntry(pageId, offset);
            }
        }
        return entries;
    }

    @Override
    public LeafEntry getEntryAt(int index) {
        return getEntries()[index];
    }

    @Override
    public void setEntryAt(int index, LeafEntry leafEntry) {
        getEntries()[index] = leafEntry;
    }

    @Override
    public void setEntries(LeafEntry[] entries) {
        this.entries = entries;
    }
}
