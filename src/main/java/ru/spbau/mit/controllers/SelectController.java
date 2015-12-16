package ru.spbau.mit.controllers;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
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
            if (!(statement instanceof Select)) {
                throw new SQLParserException("Not a select statement: ", statement);
            }
            PlainSelect plainSelect = (PlainSelect) (((Select) statement).getSelectBody());
            Expression whereExpression = plainSelect.getWhere();
            String tableName = ((net.sf.jsqlparser.schema.Table) plainSelect.getFromItem()).getName();
            Table table = TableFactory.getTable(tableName);
            if (plainSelect.getSelectItems().get(0) instanceof AllColumns) {
                if (whereExpression != null) {

                    Cursor indexCursor = getIndexCursor(whereExpression, table);
                    Cursor cursor;
                    if (indexCursor != null) {
                        cursor = new WhereCursor(indexCursor, whereExpression);
                    } else {
                        Cursor innerCursor = new FullScanCursor(bufferManager, table, table.getFirstPageId(), 0);
                        cursor = new WhereCursor(innerCursor, whereExpression);
                    }

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

    private Cursor getIndexCursor(Expression whereExpression, Table table) throws SQLParserException, IOException {
        OldOracleJoinBinaryExpression binaryExpression = (OldOracleJoinBinaryExpression) whereExpression;
        net.sf.jsqlparser.schema.Column leftExpression = (net.sf.jsqlparser.schema.Column) (binaryExpression).getLeftExpression();

        if (!(binaryExpression.getRightExpression() instanceof LongValue)) {
            throw new SQLParserException("Index permit only for INT columns!");
        }

        Integer value = new Long(((LongValue) binaryExpression.getRightExpression()).getValue()).intValue();
        String columnName = leftExpression.getColumnName();

        Column column = null;
        if (table.getIndexedColumns().containsKey(columnName)) {
            for (Column column1 : table.getColumns()) {
                if (column1.getName().equals(columnName)) {
                    column = column1;
                    break;
                }
            }
        }

        if (column != null) {
            if (whereExpression instanceof GreaterThanEquals ||
                    whereExpression instanceof GreaterThan ||
                    whereExpression instanceof EqualsTo) {
                return new TreeIndexCursor(bufferManager, table, column, value - 1);
            } else {
                return new TreeIndexCursor(bufferManager, table, column, Integer.MIN_VALUE);
            }
        }

        return null;
    }

}
