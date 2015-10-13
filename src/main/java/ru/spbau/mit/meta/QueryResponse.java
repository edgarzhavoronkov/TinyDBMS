package ru.spbau.mit.meta;

import ru.spbau.mit.Cursors.Cursor;

/**
 * Created by John on 10/13/2015.
 */
public class QueryResponse {

    private enum QueryStatus {
        OK,
        Error
    }

    private QueryStatus status;
    /**
     * Count of row changed by query
     * Deleted for delete query, inserted for insert query etc.
     * For select equal 0
     */
    private int rowCount;

    /**
     * Special for select query
     */
    private Cursor cursor;

    /**
     * For all queries besides select
     * @param status query status
     * @param rowCount changed count
     */
    public QueryResponse(QueryStatus status, int rowCount) {
        this.rowCount = rowCount;
        this.status = status;
    }

    /**
     * For select query
     * @param status query status
     * @param cursor cursor of records
     */
    public QueryResponse(QueryStatus status, Cursor cursor) {
        this.status = status;
        this.cursor = cursor;
    }
}
