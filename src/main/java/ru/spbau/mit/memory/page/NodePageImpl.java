package ru.spbau.mit.memory.page;


import java.nio.ByteBuffer;

/**
 * Created by John on 12/6/2015.
 */
public class NodePageImpl implements NodePage {
    private static final int NULL_PAGE_ID = -1;

    protected static final int IS_LEAF_OFFSET = 0;
    protected static final int SIZE_OFFSET = IS_LEAF_OFFSET + 1;
    protected static final int LEFT_NODE_OFFSET = SIZE_OFFSET + Integer.BYTES;
    protected static final int RIGHT_NODE_OFFSET = LEFT_NODE_OFFSET + Integer.BYTES;
    protected static final int PARENT_NODE_OFFSET = RIGHT_NODE_OFFSET + Integer.BYTES;
    protected static final int KEYS_OFFSET = PARENT_NODE_OFFSET + Integer.BYTES;

    protected Boolean isLeaf; //offset 0
    protected Integer size; // offset 1
    protected Integer leftNodePageId; //offset 5
    protected Integer rightNodePageId; //offset 9
    protected Integer parentNodePageId; //offset 13
    protected Integer[] keys; // offset 17
    protected BasePage page;

    public NodePageImpl(BasePage basePage) {
        this.page = basePage;
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

    @Override
    public void close() {
        page.getByteBuffer().put(IS_LEAF_OFFSET, isLeaf() ? (byte) 0 : (byte) 1);
        page.getByteBuffer().putInt(SIZE_OFFSET, getSize());
        page.getByteBuffer().putInt(LEFT_NODE_OFFSET, getLeftNodePageId() == null ? -1 : getLeftNodePageId());
        page.getByteBuffer().putInt(RIGHT_NODE_OFFSET, getRightNodePageId() == null ? -1 : getRightNodePageId());
        page.getByteBuffer().putInt(PARENT_NODE_OFFSET, getParentNodePageId() == null ? -1 : getParentNodePageId());

        page.getByteBuffer().position(KEYS_OFFSET);
        for (int i = 0; i < getSize(); i++) {
            page.getByteBuffer().putInt(keys[i]);
        }
        page.close();
    }

    @Override
    public boolean isLeaf() {
        if (isLeaf == null) {
            isLeaf = page.getByteBuffer().get(IS_LEAF_OFFSET) != 0;
        }
        return isLeaf;
    }

    @Override
    public void setIsLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    @Override
    public int getSize() {
        if (size == null) {
            size = page.getByteBuffer().getInt(SIZE_OFFSET);
        }
        return size;
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public Integer getLeftNodePageId() {
        if (leftNodePageId == null) {
            leftNodePageId = page.getByteBuffer().getInt(LEFT_NODE_OFFSET);
        }
        return leftNodePageId == NULL_PAGE_ID? null : leftNodePageId;
    }

    @Override
    public void setLeftNodePageId(Integer leftNodePageId) {
        if (leftNodePageId == null) {
            leftNodePageId = NULL_PAGE_ID;
        }
        this.leftNodePageId = leftNodePageId;
    }

    @Override
    public Integer getRightNodePageId() {
        if (rightNodePageId == null) {
            rightNodePageId = page.getByteBuffer().getInt(RIGHT_NODE_OFFSET);
        }
        return rightNodePageId == NULL_PAGE_ID? null : rightNodePageId;
    }

    @Override
    public void setRightNodePageId(Integer rightNodePageId) {
        if (rightNodePageId == null) {
            rightNodePageId = NULL_PAGE_ID;
        }
        this.rightNodePageId = rightNodePageId;
    }

    @Override
    public Integer getParentNodePageId() {
        if (parentNodePageId == null) {
            parentNodePageId = page.getByteBuffer().getInt(PARENT_NODE_OFFSET);
        }
        return parentNodePageId == NULL_PAGE_ID? null : parentNodePageId;
    }

    @Override
    public void setParentNodePageId(Integer parentNodePageId) {
        if (parentNodePageId == null) {
            parentNodePageId = NULL_PAGE_ID;
        }
        this.parentNodePageId = parentNodePageId;
    }

    @Override
    public Integer[] getKeys() {
        if (keys == null) {
            keys = new Integer[KEYS_CAPACITY];
            int[] keysFrom = new int[KEYS_CAPACITY];
            page.getByteBuffer().position(KEYS_OFFSET);
            page.getByteBuffer().asIntBuffer().get(keysFrom, 0, getSize());
            for (int i = 0; i < getSize(); i++) {
                keys[i] = keysFrom[i];
            }
        }
        return keys;
    }

}
