package ru.spbau.mit.controllers;

import net.sf.jsqlparser.statement.Statement;
import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.meta.QueryResponse;

import java.io.IOException;

/**
 * Created by edgar on 06.12.15.
 */
public class CreateIndexController implements QueryController {
    private final BufferManager bufferManager;

    private CreateIndexController(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
    }

    public static CreateIndexController getInstance(BufferManager bufferManager) {
        return new CreateIndexController(bufferManager);
    }


    @Override
    public QueryResponse process(Statement statement) throws IOException {
        return null;
    }
}
