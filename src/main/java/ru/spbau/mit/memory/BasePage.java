package ru.spbau.mit.memory;

import java.nio.ByteBuffer;

/**
 * Created by John on 12/6/2015.
 */
public interface BasePage {
    Integer SIZE = 4 * 1024;

    ByteBuffer getByteBuffer();

    byte[] getData();

    int getId();

    void makeDirty();

    void makeClean();

    boolean isDirty();

    void pin();

    void unpin();

    boolean isPin();

    long getLastOperationId();

    void updateOperationId(Long id);

    void close();
}
