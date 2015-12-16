package ru.spbau.mit.controllers;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.OldOracleJoinBinaryExpression;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import ru.spbau.mit.TableFactory;
import ru.spbau.mit.cursors.Cursor;
import ru.spbau.mit.cursors.FullScanCursor;
import ru.spbau.mit.cursors.TreeIndexCursor;
import ru.spbau.mit.cursors.WhereCursor;
import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.QueryResponse;
import ru.spbau.mit.meta.Table;

import java.io.IOException;


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
    public QueryResponse process(Statement statement) throws IOException {
        try {
            if(!(statement instanceof Select)){
                throw new SQLParserException("Not a select statement: ", statement);
            }
            PlainSelect plainSelect = (PlainSelect)(((Select) statement).getSelectBody());
            Expression whereExpression = plainSelect.getWhere();
            String tableName = ((net.sf.jsqlparser.schema.Table)plainSelect.getFromItem()).getName();
            Table table = TableFactory.getTable(tableName);
            if(plainSelect.getSelectItems().get(0) instanceof AllColumns) {
                if (whereExpression != null) {
                    String columnName = ((net.sf.jsqlparser.schema.Column) ((OldOracleJoinBinaryExpression) whereExpression).getLeftExpression()).getColumnName();
                    Column column = null;
                    if (table.getIndexedColumns().containsKey(columnName)) {
                        for (Column column1 : table.getColumns()) {
                            if (column1.getName().equals(columnName)) {
                                column = column1;
                                break;
                            }
                        }
                    }

                    Cursor cursorIndex = null;
                    if (column != null) {
                        cursorIndex = new TreeIndexCursor(bufferManager, table, column, 0);
                    }



                    Cursor innerCursor = new FullScanCursor(bufferManager, table, table.getFirstPageId(), 0);
                    Cursor cursor;
                    if (cursorIndex != null) {
                        cursor = new WhereCursor(cursorIndex, whereExpression);
                    } else {
                        cursor = new WhereCursor(innerCursor, whereExpression);
                    }
//                    Cursor cursor = new WhereCursor(innerCursor, whereExpression);
//                    ((Column) ((EqualsTo) whereExpression).getLeftExpression()).getColumnName()
//                    ((Column) ((OldOracleJoinBinaryExpression) whereExpression).getLeftExpression()).getColumnName()
//                    OldOracleJoinBinaryExpression
//                    OldOracleJoinBinaryExpression
                    //select * from t1 where id = 1

//                    Column column = new Column(columnName);




                    return new QueryResponse(QueryResponse.Status.OK, cursor);
                }
                Cursor cursor = new FullScanCursor(bufferManager, table, table.getFirstPageId(), 0);
                return new QueryResponse(QueryResponse.Status.OK, cursor);
            }
            return null;
        } catch (SQLParserException e) {
            return new QueryResponse(QueryResponse.Status.Error, e);
        }
    }

    private Column getIndexColumn() {
        return null;
    }

    ;

}
