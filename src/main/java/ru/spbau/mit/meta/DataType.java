package ru.spbau.mit.meta;

import ru.spbau.mit.memory.Page;

/**
 * Created by John on 9/12/2015.
 */
public enum DataType {
//    private static final Integer VARCHAR_SIZE = 128;

    INTEGER (Integer.class, Integer.SIZE) {
        @Override
        public void putInPage(Object o, Page page) {
            page.getByteBuffer().putInt((int) o);
        }
    },
    DOUBLE (Double.class, Double.SIZE) {
        @Override
        public void putInPage(Object o, Page page) {
            page.getByteBuffer().putDouble((double) o);
        }
    };
//    VARCHAR (String.class, ) {
//        @Override
//        public void putInPage(Object o, Page page) {
//            page.getByteBuffer().put(o.toString().getBytes());
//        }
//    };

    private final Class<?> type;

    private Integer size;

    public Integer getSize() {
        return size;
    }

    DataType(Class<?> type, Integer size) {
        this.type = type;
        this.size = size;
    }

    public Class<?> getType() {
        return type;
    }

    public abstract void putInPage(Object o, Page page );
}
