package ru.spbau.mit.meta;
import ru.spbau.mit.memory.Page;

/**
 * Created by John on 9/12/2015.
 */
public enum DataType {
    INTEGER (Integer.class, Integer.SIZE) {
        @Override
        public void putInPage(Object o, Page page) {
            page.getByteBuffer().putInt((Integer)o);
        }
    },
    DOUBLE (Double.class, Double.SIZE) {
        @Override
        public void putInPage(Object o, Page page) {
            page.getByteBuffer().putDouble((Double)o);
        }
    },
    VARCHAR (String.class, Constants.VARCHAR_MAXIMUM_SIZE) {
        @Override
        public void putInPage(Object o, Page page) {
            page.getByteBuffer().put((byte) ((String)o).length());
            page.getByteBuffer().put(((String)o).getBytes());
        }
    };

    static class Constants {
        private static final Integer VARCHAR_MAXIMUM_SIZE = 128;
    }

    private final Class type;
    private final Integer size;

    DataType(Class type, Integer size) {
        this.type = type;
        this.size = size;
    }

    public Class getType() {
        return type;
    }

    public Integer getSize() {
        return size;
    }

    public abstract void putInPage(Object o, Page page);
}
