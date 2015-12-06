package ru.spbau.mit.meta;
import ru.spbau.mit.memory.page.RecordPage;

/**
 * Created by John on 9/12/2015.
 */
public enum DataType {
    INTEGER (Integer.class, Integer.BYTES) {
        @Override
        public void putInPage(Object o, RecordPage recordPage) {
            recordPage.getByteBuffer().putInt((Integer)o);
        }

        @Override
        public Object getFromPage(RecordPage recordPage) {
            return recordPage.getByteBuffer().getInt();
        }
    },
    DOUBLE (Double.class, Double.BYTES) {
        @Override
        public void putInPage(Object o, RecordPage recordPage) {
            recordPage.getByteBuffer().putDouble((Double)o);
        }

        @Override
        public Object getFromPage(RecordPage recordPage) {
            return recordPage.getByteBuffer().getDouble();
        }
    },
    VARCHAR (String.class, Constants.VARCHAR_MAXIMUM_SIZE) {
        @Override
        public void putInPage(Object o, RecordPage recordPage) {
            recordPage.getByteBuffer().put((byte) (String.valueOf(o)).length());
            recordPage.getByteBuffer().put((String.valueOf(o)).getBytes());
        }

        @Override
        public Object getFromPage(RecordPage recordPage) {
            byte[] buffer = new byte[Constants.VARCHAR_MAXIMUM_SIZE];
            recordPage.getByteBuffer().get(buffer);
            byte length = buffer[0];
            byte[] stringToReturn = new byte[length];
            System.arraycopy(buffer, 1, stringToReturn, 0, length);
            return new String(stringToReturn);
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

    public abstract void putInPage(Object o, RecordPage recordPage);

    public abstract Object getFromPage(RecordPage recordPage);
}
