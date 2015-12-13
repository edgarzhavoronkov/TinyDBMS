package ru.spbau.mit.memory.page;

import ru.spbau.mit.cursors.Index.BTree.LeafEntry;

/**
 * Created by John on 12/6/2015.
 */
public class LeafNodePageImpl extends NodePageImpl implements LeafNodePage {
    private static final int ENTRY_OFFSET = KEYS_OFFSET + KEYS_CAPACITY * Integer.BYTES;

    private LeafEntry[] entries;


    public LeafNodePageImpl(BasePage basePage) {
        super(basePage);
        ((BasePageImpl) basePage).setAfterClose(this::flush);
    }

    @Override
    public void flush() {
        page.getByteBuffer().position(ENTRY_OFFSET);
        for (int i = 0; i < getSize(); i++) {
            LeafEntry entry = getEntries()[i];
            page.getByteBuffer().putInt(entry.getPageId());
            page.getByteBuffer().putInt(entry.getOffset());
        }
        super.flush();
    }

    private LeafEntry[] getEntries() {
        if (entries == null) {
            page.getByteBuffer().position(ENTRY_OFFSET);
            entries = new LeafEntry[ENTRY_CAPACITY];
            for (int i = 0; i < getSize(); i++) {
                int pageId = page.getByteBuffer().getInt();
                int offset = page.getByteBuffer().getInt();
                entries[i] = new LeafEntry(pageId, offset);
            }
        }
        return entries;
    }

    @Override
    public LeafEntry getEntryAt(int index) {
        assert (index < getSize());
        return getEntries()[index];
    }

    @Override
    public void setEntryAt(int index, LeafEntry leafEntry) {
        assert (index < getSize());
        getEntries()[index] = leafEntry;
    }

}
