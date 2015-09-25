package ru.spbau.mit.controllers;

import net.sf.jsqlparser.statement.Statement;

/**
 * Created by edgar on 25.09.15.
 */
public interface QueryController {
    void process(Statement statement);
    void getReply();
}
