package ru.spbau.mit.controllers;

import net.sf.jsqlparser.statement.Statement;
import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.meta.QueryResponse;

import java.io.IOException;

/**
 * Created by edgar on 06.12.15.
 */
public class DeleteController implements QueryController {
    private final BufferManager bufferManager;

    private DeleteController(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
    }

    public static DeleteController getInstance(BufferManager bufferManager) {
        return new DeleteController(bufferManager);
    }


    @Override
    public QueryResponse process(Statement statement) throws IOException {
        return null;
    }
}
