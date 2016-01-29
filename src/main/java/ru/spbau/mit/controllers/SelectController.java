package ru.spbau.mit.controllers;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


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
                return process(whereExpression, table);
            }
            return null;
        } catch (SQLParserException e) {
            return new QueryResponse(QueryResponse.Status.Error, e);
        }
    }

    public QueryResponse process(Expression whereExpression, Table table) throws IOException, SQLParserException {
        if (whereExpression != null) {
            Cursor indexCursor = getIndexCursor(whereExpression, table);// null;
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

    private Cursor getIndexCursor(Expression whereExpression, Table table) throws SQLParserException, IOException {
        Map<String, Bounds> bounds = getBounds(whereExpression, table.getIndexedColumns().keySet());

        if (bounds.size() == 0) {
            return null;
        } else {
            Map.Entry<String, Bounds> entry = bounds.entrySet().iterator().next();
            String columnName = entry.getKey();
            Column column = null;
            for (Column column1 : table.getColumns()) {
                if (column1.getName().equals(columnName)) {
                    column = column1;
                }
            }
            if (column == null) {
                return null;
            } else {
                return new TreeIndexCursor(bufferManager, table, column, entry.getValue().leftBound, entry.getValue().rightBound);
            }
        }


    }

    private Map<String, Bounds> getBounds(Expression whereExpression, Set<String> indexedColumns) throws SQLParserException {

        if (whereExpression instanceof AndExpression) {
            Expression rightExpression = ((AndExpression) whereExpression).getRightExpression();
            Map<String, Bounds> rBounds = getBounds(rightExpression, indexedColumns);
            Expression leftExpression = ((AndExpression) whereExpression).getLeftExpression();
            Map<String, Bounds> lBounds = getBounds(leftExpression, indexedColumns);

            Map<String, Bounds> result = new HashMap<>(rBounds);
            for (String columnName : lBounds.keySet()) {
                if (result.containsKey(columnName)) {
                    Bounds boundsR = result.get(columnName);
                    Bounds boundsL = lBounds.get(columnName);
                    boundsR.leftBound = Math.max(boundsR.leftBound, boundsL.leftBound);
                    boundsR.rightBound = Math.min(boundsR.rightBound, boundsL.rightBound);
                } else {
                    result.put(columnName, lBounds.get(columnName));
                }
            }
            return result;
        }

        OldOracleJoinBinaryExpression binaryExpression = (OldOracleJoinBinaryExpression) whereExpression;
        net.sf.jsqlparser.schema.Column leftExpression = (net.sf.jsqlparser.schema.Column) (binaryExpression).getLeftExpression();

        if (indexedColumns.contains(leftExpression.getColumnName())) {
            Integer value = new Long(((LongValue) binaryExpression.getRightExpression()).getValue()).intValue();

            if (!(binaryExpression.getRightExpression() instanceof LongValue)) {
                throw new SQLParserException("Index permit only for INT columns!");
            }

            Bounds bounds = new Bounds();
            if (whereExpression instanceof GreaterThanEquals ||
                    whereExpression instanceof GreaterThan
                    ) {
                bounds.leftBound = value;
            } else if (whereExpression instanceof EqualsTo) {
                bounds.leftBound = value;
                bounds.rightBound = value;
            } else {
                bounds.rightBound = value;
            }

            return Collections.singletonMap(leftExpression.getColumnName(), bounds);
        }
        return Collections.emptyMap();
    }

    private static class Bounds {
        Integer leftBound = Integer.MIN_VALUE;
        Integer rightBound = Integer.MAX_VALUE;
    }


}
