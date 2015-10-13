package ru.spbau.mit.controllers;

import net.sf.jsqlparser.statement.Statement;
import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.meta.QueryResponse;

/**
 * Created by edgar on 25.09.15.
 */
public class UpdateController implements QueryController {
    private final BufferManager bufferManager;
    private UpdateController(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
    }

    public static UpdateController getInstance(BufferManager bufferManager) {
        return new UpdateController(bufferManager);
    }

    @Override
    public QueryResponse process(Statement statement) {
        return null;
    }

    public void getReply() {

    }
}
