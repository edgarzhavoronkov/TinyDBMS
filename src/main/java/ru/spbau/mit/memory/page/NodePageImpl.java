package ru.spbau.mit.memory.page;


/**
 * Created by John on 12/6/2015.
 */
public class NodePageImpl extends BasePageImpl implements NodePage {
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

    public NodePageImpl(byte[] data, Integer id) {
        super(data, id);
    }

    public NodePageImpl(BasePage basePage) {
        super(basePage.getData(), basePage.getId());
        operationId = ((BasePageImpl) basePage).operationId;
        dirty = ((BasePageImpl) basePage).dirty;
        pinCount = ((BasePageImpl) basePage).pinCount;
    }

    @Override
    public void close() {
        super.close();
        byteBuffer.put(IS_LEAF_OFFSET, isLeaf() ? (byte) 0 : (byte) 1);
        byteBuffer.putInt(SIZE_OFFSET, getSize());
        byteBuffer.putInt(LEFT_NODE_OFFSET, getLeftNodePageId() == null ? -1 : getLeftNodePageId());
        byteBuffer.putInt(RIGHT_NODE_OFFSET, getRightNodePageId() == null ? -1 : getRightNodePageId());
        byteBuffer.putInt(PARENT_NODE_OFFSET, getParentNodePageId() == null ? -1 : getParentNodePageId());

        byteBuffer.position(KEYS_OFFSET);
        for (int key : getKeys()) {
            byteBuffer.putInt(key);
        }
    }

    @Override
    public boolean isLeaf() {
        if (isLeaf == null) {
            isLeaf = byteBuffer.get(IS_LEAF_OFFSET) != 0;
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
            size = byteBuffer.getInt(SIZE_OFFSET);
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
            leftNodePageId = byteBuffer.getInt(LEFT_NODE_OFFSET);
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
            rightNodePageId = byteBuffer.getInt(RIGHT_NODE_OFFSET);
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
            parentNodePageId = byteBuffer.getInt(PARENT_NODE_OFFSET);
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
            byteBuffer.position(KEYS_OFFSET);
            byteBuffer.asIntBuffer().get(keysFrom, 0, getSize());
            for (int i = 0; i < getSize(); i++) {
                keys[i] = keysFrom[i];
            }
        }
        return keys;
    }

    @Override
    public void setKeys(Integer[] keys) {
        this.keys = keys;
    }

}
