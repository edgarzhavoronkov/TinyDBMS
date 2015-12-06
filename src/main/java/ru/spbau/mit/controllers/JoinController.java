package ru.spbau.mit.controllers;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import ru.spbau.mit.TableFactory;
import ru.spbau.mit.cursors.Cursor;
import ru.spbau.mit.cursors.FullScanCursor;
import ru.spbau.mit.cursors.WhereCursor;
import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.meta.Table;
import ru.spbau.mit.meta.QueryResponse;

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
            Cursor cursor = new FullScanCursor(bufferManager, leftTable, leftTable.getFirstPageId(), 0);
            for (Join join : plainSelect.getJoins()) {
                String tableName = ((net.sf.jsqlparser.schema.Table)join.getRightItem()).getName();
                Table table = TableFactory.getTable(tableName);
                cursor = join(cursor, table);
            }
            Cursor whereCursor = new WhereCursor(cursor, whereExpression);
            return new QueryResponse(QueryResponse.Status.OK, whereCursor);
        } catch (SQLParserException e) {
            return new QueryResponse(QueryResponse.Status.Error, e);
        }
    }

    private Cursor join(Cursor cursor, Table table) {
        return null;
    }


}
