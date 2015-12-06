package ru.spbau.mit.cursors.Index.BTree;

/**
 * Created by gellm_000 on 06.12.2015.
 */
public class LeafEntry {
    int pageId;
    int offset;

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public LeafEntry(int pageId, int offset) {
        this.pageId = pageId;
        this.offset = offset;
    }
}
