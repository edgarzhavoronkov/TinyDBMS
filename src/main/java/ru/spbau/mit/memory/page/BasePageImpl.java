package ru.spbau.mit.memory.page;

import java.nio.ByteBuffer;

/**
 * Created by John on 12/6/2015.
 */
public class BasePageImpl implements BasePage {
    protected ByteBuffer byteBuffer;
    protected long operationId;
    protected int id;

    protected boolean dirty;
    protected int pinCount;
    public BeforeClose beforeClose;

    public BasePageImpl(byte[] data, Integer id) {
        assert data.length == SIZE;
        byteBuffer = ByteBuffer.wrap(data);
        this.id = id;
    }

    public ByteBuffer getByteBuffer() {
        return this.byteBuffer;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void pin() {
        pinCount++;
    }

    @Override
    public void unpin() {
        assert pinCount > 0;
        pinCount--;
    }

    @Override
    public boolean isPin() {
        return pinCount > 0;
    }

    @Override
    public long getLastOperationId() {
        return operationId;
    }

    @Override
    public void updateOperationId(Long operationId) {
        this.operationId = operationId;
    }

    @Override
    public byte[] getData() {
        return byteBuffer.array();
    }

    @Override
    public void makeDirty() {
        dirty = true;
    }

    @Override
    public void makeClean() {
        dirty = false;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BasePage page = (BasePageImpl) o;

        return id == page.getId();
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public void flush() {
        if (beforeClose != null) {
            beforeClose.beforeClose();
        }
    }

    public void setAfterClose(BeforeClose beforeClose) {
        this.beforeClose = beforeClose;
    }

}
