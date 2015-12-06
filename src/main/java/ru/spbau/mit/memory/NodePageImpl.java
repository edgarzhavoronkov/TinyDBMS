package ru.spbau.mit.memory;


/**
 * Created by John on 12/6/2015.
 */
public class NodePageImpl extends BasePageImpl implements NodePage {
    private static final int NULL_PAGE_ID = -1;

    private static final int IS_LEAF_OFFSET = 0;
    private static final int SIZE_OFFSET = 1;
    private static final int LEFT_NODE_OFFSET = 5;
    private static final int RIGHT_NODE_OFFSET = 9;
    private static final int PARENT_NODE_OFFSET = 13;
    private static final int KEYS_OFFSET = 17;

    protected Boolean isLeaf; //offset 0
    protected Integer size; // offset 1
    protected Integer leftNodePageId; //offset 5
    protected Integer rightNodePageId; //offset 9
    protected Integer parentNodePageId; //offset 13
    protected int[] keys; // offset 17

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
    public int[] getKeys() {
        if (keys == null) {
            byteBuffer.position(KEYS_OFFSET);
            keys = new int[getSize()];
            byteBuffer.asIntBuffer().get(keys, 0, getSize());
        }
        return keys;
    }

    @Override
    public void setKeys(int[] keys) {
        this.keys = keys;
    }

}
