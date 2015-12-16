package ru.spbau.mit.controllers;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.Index;
import ru.spbau.mit.TableFactory;
import ru.spbau.mit.cursors.Cursor;
import ru.spbau.mit.cursors.FullScanCursor;
import ru.spbau.mit.cursors.Index.BTree.BTree;
import ru.spbau.mit.cursors.Index.BTree.LeafEntry;
import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.memory.Record;
import ru.spbau.mit.memory.page.RecordPageImpl;
import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.QueryResponse;
import ru.spbau.mit.meta.Table;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


/**
 * Created by edgar on 06.12.15.
 */
public class CreateIndexController implements QueryController {
    private final BufferManager bufferManager;

    private CreateIndexController(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
    }

    public static CreateIndexController getInstance(BufferManager bufferManager) {
        return new CreateIndexController(bufferManager);
    }


    @Override
    public QueryResponse process(Statement statement) throws IOException {
        try {
            CreateIndex createIndex = (CreateIndex) statement;
            net.sf.jsqlparser.schema.Table table = createIndex.getTable();

            Table indexTable = TableFactory.getTable(table.getName());

            Index index = createIndex.getIndex();
            List<String> columnsNames = index.getColumnsNames();
            if (columnsNames.size() != 1) {
                throw new SQLParserException("Wrong size of indexed columns.", createIndex);
            }
            String columnName = columnsNames.get(0);

            Optional<Column> maybeColumn = indexTable.getColumns().
                    parallelStream().
                    findFirst().
                    filter((column1) -> column1.getName().equals(columnName));

            if (!maybeColumn.isPresent()) {
                throw new SQLParserException("No such column in table.", createIndex);
            }
            Column column = maybeColumn.get();

            BTree bTree = new BTree();
            Integer pageId = bTree.getRoot().getPageId();
            indexTable.getIndexedColumns().put(column.getName(), pageId);

            //Full scan, insert all existing value in
            Cursor cursor = new FullScanCursor(bufferManager, indexTable, indexTable.getFirstPageId(), 0);
            while (cursor.hasNext()) {
                Record currentRecord = (Record) cursor.next();
                if (currentRecord == null) continue;
                int absRecordNum = new RecordPageImpl(
                        bufferManager.getPage(cursor.getPageId()), indexTable)
                        .getAbsRecordNum(cursor.getOffset()
                        );

                bTree.insert(
                        (Integer) currentRecord.getValues().get(column),
                        new LeafEntry(cursor.getPageId(), absRecordNum
                        ));
            }
            return new QueryResponse(QueryResponse.Status.OK, 1);
        } catch (SQLParserException e) {
            QueryResponse response = new QueryResponse(QueryResponse.Status.Error, e);
            response.setErrorMessageText(e.getMessage());
            return response;
        }
    }
}
