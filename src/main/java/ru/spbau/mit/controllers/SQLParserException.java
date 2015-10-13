package ru.spbau.mit.controllers;

import net.sf.jsqlparser.statement.Statement;
/**
 * Created by Mark on 13.10.2015.
 */
public class SQLParserException extends RuntimeException {
    private Statement brokenStatement;
    public SQLParserException(String message, Statement statement){
        super(message);
        brokenStatement = statement;
    }

    @Override
    public String toString(){
        return super.toString() + brokenStatement.toString();
    }
}
