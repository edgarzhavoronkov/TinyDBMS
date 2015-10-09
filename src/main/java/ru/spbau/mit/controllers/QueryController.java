package ru.spbau.mit.controllers;

import net.sf.jsqlparser.statement.Statement;

import java.io.IOException;

/**
 * Created by edgar on 25.09.15.
 */
public interface QueryController {
    void process(Statement statement) throws IOException;
    void getReply();
}
