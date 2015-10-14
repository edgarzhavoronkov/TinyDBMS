package ru.spbau.mit.meta;

import ru.spbau.mit.controllers.SQLParserException;
import ru.spbau.mit.cursors.Cursor;

/**
 * Created by John on 10/13/2015.
 */
public class QueryResponse {

    public enum Status {
        OK,
        Error
    }

    private Status status;

    /**
     * Count of row changed by query
     * Deleted for delete query, inserted for insert query etc.
     * For select equal 0
     */
    private int rowCount;

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * Special for select query
     */
    private Cursor cursor;

    /**
     * Special for Error status
     */
    private String errorMessageText;

    public String getErrorMessageText() {
        return errorMessageText;
    }

    private SQLParserException sqlParserException;

    public SQLParserException getSqlParserException() {
        return sqlParserException;
    }

    public void setErrorMessageText(String errorMessageText) {
        this.errorMessageText = errorMessageText;
    }

    /**
     * For all queries besides select (must be OK)
     * @param status query status
     * @param rowCount changed count
     */
    public QueryResponse(Status status, int rowCount) {
        this.rowCount = rowCount;
        this.status = status;
    }

    /**
     * For select query
     * @param status query status (must be OK)
     * @param cursor cursor of records
     */
    public QueryResponse(Status status, Cursor cursor) {
        this.status = status;
        this.cursor = cursor;
    }

    /**
     * Response for error result
     * @param status must be Error
     * @param sqlParserException error
     */
    public QueryResponse(Status status, SQLParserException sqlParserException) {
        this.status = status;
        this.sqlParserException = sqlParserException;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public Status getStatus() {
        return status;
    }
}
