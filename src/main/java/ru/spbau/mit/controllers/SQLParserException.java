package ru.spbau.mit.controllers;

import net.sf.jsqlparser.statement.Statement;

import java.sql.SQLException;

/**
 * Created by Mark on 13.10.2015.
 */
public class SQLParserException extends SQLException {
    private Statement brokenStatement;
    public SQLParserException(String message, Statement statement){
        super(message);
        brokenStatement = statement;
    }

    public SQLParserException(String message) {
        super(message);
    }


    @Override
    public String toString(){
        return super.toString() + brokenStatement.toString();
    }
}
