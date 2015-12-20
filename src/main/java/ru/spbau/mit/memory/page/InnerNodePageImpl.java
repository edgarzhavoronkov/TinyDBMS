package ru.spbau.mit.memory.page;


public class InnerNodePageImpl extends NodePageImpl implements InnerNodePage {
    private static final int CHILDREN_OFFSET = KEYS_OFFSET + KEYS_CAPACITY * Integer.BYTES;

    private Integer[] children;

    public InnerNodePageImpl(BasePage basePage) {
        super(basePage);
        ((BasePageImpl) basePage).setAfterClose(this::flush);
        makeDirty();
    }

    public Integer[] getChildren() {
        if (children == null) {
            page.getByteBuffer().position(CHILDREN_OFFSET);
            children = new Integer[CHILDREN_CAPACITY + 1];
            if(size >= 111){
                size --;
                size ++;
            }
            for (int i = 0;getSize() > 0 &&  i < getSize() + 1; i++) {
                children[i] = page.getByteBuffer().getInt();
            }
        }
        return children;
    }

    @Override
    public Integer getChildrenAt(int index) {
        assert (index < getSize() + 1);
        return getChildren()[index];
    }

    @Override
    public void setChildrenAt(int index, Integer children) {
        assert (index < getSize() + 1);
        makeDirty();
        getChildren()[index] = children;
    }

    @Override
    public void flush() {
        page.getByteBuffer().position(CHILDREN_OFFSET);
        for (int i = 0; getSize() > 0 && i < getSize() + 1; i++) {
            page.getByteBuffer().putInt(getChildren()[i]);
        }
        super.flush();
    }

}
