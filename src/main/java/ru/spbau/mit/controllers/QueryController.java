package ru.spbau.mit.controllers;

import net.sf.jsqlparser.statement.Statement;
import ru.spbau.mit.meta.QueryResponse;

import java.io.IOException;

/**
 * Created by edgar on 25.09.15.
 */
public interface QueryController {
    QueryResponse process(Statement statement) throws IOException;
}
