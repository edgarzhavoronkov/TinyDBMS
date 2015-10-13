package ru.spbau.mit.controllers;

import net.sf.jsqlparser.statement.Statement;
import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.meta.QueryResponse;


/**
 * Created by edgar on 25.09.15.
 */
public class SelectController implements QueryController {
    private final BufferManager bufferManager;
    private SelectController(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
    }

    public static SelectController getInstance(BufferManager bufferManager) {
        return new SelectController(bufferManager);
    }

    @Override
    public QueryResponse process(Statement statement) {

        return null;
    }

}
