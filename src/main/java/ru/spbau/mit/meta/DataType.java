package ru.spbau.mit.meta;
import ru.spbau.mit.memory.Page;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by John on 9/12/2015.
 */
public enum DataType {
    INTEGER (Integer.class, Integer.SIZE) {
        @Override
        public void putInPage(Object o, Page page) {
            page.getByteBuffer().putInt((Integer)o);
        }

        @Override
        public Object getFromPage(Page page) {
            return page.getByteBuffer().getInt();
        }
    },
    DOUBLE (Double.class, Double.SIZE) {
        @Override
        public void putInPage(Object o, Page page) {
            page.getByteBuffer().putDouble((Double)o);
        }

        @Override
        public Object getFromPage(Page page) {
            return page.getByteBuffer().getDouble();
        }
    },
    VARCHAR (String.class, Constants.VARCHAR_MAXIMUM_SIZE) {
        @Override
        public void putInPage(Object o, Page page) {
            page.getByteBuffer().put((byte) ((String)o).length());
            page.getByteBuffer().put(((String)o).getBytes());
        }

        @Override
        public Object getFromPage(Page page) {
            byte[] buffer = new byte[Constants.VARCHAR_MAXIMUM_SIZE];
            page.getByteBuffer().get(buffer);
            byte length = buffer[0];
            byte[] stringToReturn = new byte[length];
            System.arraycopy(buffer, 1, stringToReturn, 1, length + 1 - 1);
            ByteArrayInputStream stream = new ByteArrayInputStream(stringToReturn);
            try (ObjectInputStream objectInputStream = new ObjectInputStream(stream)) {
                return objectInputStream.readObject();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
            return null;
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

    public abstract Object getFromPage(Page page);
}
