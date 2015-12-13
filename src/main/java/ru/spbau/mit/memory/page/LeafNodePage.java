package ru.spbau.mit.memory.page;

import ru.spbau.mit.cursors.Index.BTree.LeafEntry;

/**
 * Created by John on 12/6/2015.
 */
public interface LeafNodePage extends NodePage {
    int ENTRY_CAPACITY = KEYS_CAPACITY;

    LeafEntry getEntryAt(int index);

    void setEntryAt(int index, LeafEntry leafEntry);

}
