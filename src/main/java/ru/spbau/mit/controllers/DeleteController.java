package ru.spbau.mit.controllers;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import ru.spbau.mit.TableFactory;
import ru.spbau.mit.cursors.Cursor;
import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.memory.Record;
import ru.spbau.mit.memory.page.RecordPage;
import ru.spbau.mit.memory.page.RecordPageImpl;
import ru.spbau.mit.meta.QueryResponse;
import ru.spbau.mit.meta.Table;

import java.io.IOException;

/**
 * Created by edgar on 06.12.15.
 */
public class DeleteController implements QueryController {
    private final BufferManager bufferManager;

    private DeleteController(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
    }

    public static DeleteController getInstance(BufferManager bufferManager) {
        return new DeleteController(bufferManager);
    }


    @Override
    public QueryResponse process(Statement statement) throws IOException {
        try {
            Delete delete = (Delete) statement;
            Expression whereExpression = delete.getWhere();
            Table table = TableFactory.getTable(delete.getTable().getName());
            QueryResponse response = SelectController.getInstance(bufferManager).process(whereExpression, table);
            Cursor cursor = response.getCursor();
            int count = 0;
            while (cursor.hasNext()) {
                Record record = (Record) cursor.next();
                if (record == null) continue;
                RecordPage page = new RecordPageImpl(bufferManager.getPage(cursor.getPageId()), table);
                page.removeRecord(cursor.getOffset());
                count++;
            }
            return new QueryResponse(QueryResponse.Status.OK, count);
        } catch (SQLParserException e) {
            return new QueryResponse(QueryResponse.Status.Error, e);
        }
    }
}
