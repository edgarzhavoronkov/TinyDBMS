package ru.spbau.mit.memory.page;


public class InnerNodePageImpl extends NodePageImpl implements InnerNodePage {
    private static final int CHILDREN_OFFSET = KEYS_OFFSET + KEYS_CAPACITY * Integer.BYTES;

    private Integer[] children;

    public InnerNodePageImpl(BasePage basePage) {
        super(basePage);
        ((BasePageImpl) basePage).setAfterClose(this::flush);
    }

    public Integer[] getChildren() {
        if (children == null) {
            page.getByteBuffer().position(CHILDREN_OFFSET);
            children = new Integer[CHILDREN_CAPACITY];
            for (int i = 0; i < getSize(); i++) {
                children[i] = page.getByteBuffer().getInt();
            }
        }
        return children;
    }

    @Override
    public Integer getChildrenAt(int index) {
        return getChildren()[index];
    }

    @Override
    public void setChildrenAt(int index, Integer children) {
        getChildren()[index] = children;
    }

    @Override
    public void flush() {
        page.getByteBuffer().position(CHILDREN_OFFSET);
        for (int i = 0; i < getSize(); i++) {
            page.getByteBuffer().putInt(getChildren()[i]);
        }
        super.flush();
    }

}
