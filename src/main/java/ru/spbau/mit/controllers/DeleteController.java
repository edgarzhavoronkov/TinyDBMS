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
import java.util.ArrayList;
import java.util.List;

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
            List<Integer> deletedRecords = new ArrayList<>();
            int curPageId = cursor.getPageId();
            while (cursor.hasNext()) {
                Record record = (Record) cursor.next();
                if (record == null) continue;
                if (curPageId != cursor.getPageId() && deletedRecords.size() > 0) {
                    deletedRecords.sort((o1, o2) -> o1 - o2);
                    RecordPage page = new RecordPageImpl(bufferManager.getPage(curPageId), table);
                    for (int i = deletedRecords.size() - 1; i >= 0; i--) {
                        page.removeRecord(deletedRecords.get(i));
                        count++;
                    }
                    deletedRecords = new ArrayList<>();
                }
                curPageId = cursor.getPageId();
                deletedRecords.add(cursor.getOffset());
            }
            if (deletedRecords.size() > 0) {
                RecordPage page = new RecordPageImpl(bufferManager.getPage(curPageId), table);
                for (int i = deletedRecords.size() - 1; i >= 0; i--) {
                    page.removeRecord(deletedRecords.get(i));
                    count++;
                }
            }
            return new QueryResponse(QueryResponse.Status.OK, count);
        } catch (SQLParserException e) {
            return new QueryResponse(QueryResponse.Status.Error, e);
        }
    }
}
