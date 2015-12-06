package ru.spbau.mit.cursors;

import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.memory.RecordPage;
import ru.spbau.mit.memory.Record;
import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by edgar on 30.10.15.
 */
public class WhereCursor implements Cursor {
    private String columnName;
    private Object value;

    private Column columnKey;

    private final Table table;
    private Integer pageId, offset;
    private RecordPage currentRecordPage = null;
    private final BufferManager bufferManager;
    private final List<Column> fields;
    private Record currentRecord;

    public WhereCursor(BufferManager bufferManager, Table table, Integer pageId, Integer offset, String columnName, Object value) throws IOException {
        this(bufferManager, table);
        this.pageId = pageId;
        this.offset = offset;
        this.columnName = columnName;
        this.value = value;
        for (Column column : fields) {
            if (column.getName().equals(columnName)) {
                this.columnKey = column;
                break;
            }
        }
        initiateCursor(pageId, offset);
    }

    public WhereCursor(BufferManager bufferManager, Table table) {
        this.bufferManager = bufferManager;
        this.fields = new ArrayList<>(table.getColumns());

        this.table = table;
    }

    @Override
    public Record getCurrentRecord() {
        return currentRecord;
    }

    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public void initiateCursor(Integer pageId, Integer offset) throws IOException {
        this.pageId = pageId;
        this.offset = offset;
        start();
    }

    private void start() throws IOException {
        this.currentRecordPage = bufferManager.getRecordPage(pageId, table);
        currentRecordPage.pin();
    }

    @Override
    public boolean hasNext() {
        return (offset < currentRecordPage.getRecordCount() || currentRecordPage.hasNext());
    }

    @Override
    public Object next() {
        if (!hasNext()) return null;
        if (offset >= currentRecordPage.getRecordCount()) {
            currentRecordPage.unpin();
            try {
                currentRecordPage = bufferManager.getRecordPage(currentRecordPage.getNextPageId(), table);
            } catch (IOException e) {
                e.printStackTrace();
            }
            currentRecordPage.pin();
            offset = 0;
        }
        currentRecord = currentRecordPage.getRecord(offset);
        offset++;
        while (!currentRecord.getValues().get(columnKey).equals(value)) {
            if (!hasNext()) return null;
            if (offset >= currentRecordPage.getRecordCount()) {
                currentRecordPage.unpin();
                try {
                    currentRecordPage = bufferManager.getRecordPage(currentRecordPage.getNextPageId(), table);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentRecordPage.pin();
                offset = 0;
            }
            currentRecord = currentRecordPage.getRecord(offset);
            offset++;
        }
        return currentRecord;
    }
}
