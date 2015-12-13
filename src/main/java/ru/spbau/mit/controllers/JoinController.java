package ru.spbau.mit.controllers;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import ru.spbau.mit.TableFactory;
import ru.spbau.mit.cursors.Cursor;
import ru.spbau.mit.cursors.FullScanCursor;
import ru.spbau.mit.cursors.JoinCursor;
import ru.spbau.mit.cursors.WhereCursor;
import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.memory.Record;
import ru.spbau.mit.meta.Table;
import ru.spbau.mit.meta.QueryResponse;
import ru.spbau.mit.meta.Table;

import java.io.IOException;

/**
 * Created by edgar on 06.12.15.
 */
public class JoinController  implements QueryController {
    private final BufferManager bufferManager;

    private JoinController(BufferManager bufferManager) {
        this.bufferManager = bufferManager;

    }

    public static JoinController getInstance(BufferManager bufferManager) {
        return new JoinController(bufferManager);
    }

    @Override
    public QueryResponse process(Statement statement) throws IOException {
        try {
            if(!(statement instanceof Select)){
                throw new SQLParserException("Not a select statement: ", statement);
            }
            PlainSelect plainSelect = (PlainSelect)(((Select)statement).getSelectBody());
            Expression whereExpression = plainSelect.getWhere();
            String leftTableName = ((net.sf.jsqlparser.schema.Table)plainSelect.getFromItem()).getName();
            Table leftTable = TableFactory.getTable(leftTableName);
            Cursor leftCursor = new FullScanCursor(bufferManager, leftTable, leftTable.getFirstPageId(), 0);

            //TODO: more joins?
            Join join = plainSelect.getJoins().get(0);
            Expression onExpression = join.getOnExpression();
            String tableName = ((net.sf.jsqlparser.schema.Table)join.getRightItem()).getName();
            Table table = TableFactory.getTable(tableName);
            Cursor rightCursor = new FullScanCursor(bufferManager, table, table.getFirstPageId(), 0);
            Cursor joinCursor = new JoinCursor(leftCursor, rightCursor, onExpression);

            Cursor whereCursor = new WhereCursor(joinCursor, whereExpression);
            return new QueryResponse(QueryResponse.Status.OK, whereCursor);
        } catch (SQLParserException e) {
            return new QueryResponse(QueryResponse.Status.Error, e);
        }
    }
}
