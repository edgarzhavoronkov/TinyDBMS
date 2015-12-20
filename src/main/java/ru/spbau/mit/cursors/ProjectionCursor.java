package ru.spbau.mit.cursors;

import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.memory.Record;
import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.Table;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by edgar on 20.12.15.
 */
public class ProjectionCursor implements Cursor {
    private Cursor innerCursor;
    private List<SelectItem> selectItems;
    private Record currentRecord;
    private final BufferManager bufferManager;

    public ProjectionCursor(Cursor innerCursor, List<SelectItem> selectItems) {
        this.innerCursor = innerCursor;
        this.selectItems = selectItems;
        this.bufferManager = innerCursor.getBufferManager();
    }

    @Override
    public Record getCurrentRecord() {
        return currentRecord;
    }

    @Override
    public BufferManager getBufferManager() {
        return innerCursor.getBufferManager();
    }

    @Override
    public Table getTable() {
        return innerCursor.getTable();
    }

    @Override
    public Cursor clone() {
        return null;
    }

    @Override
    public void reset() {
        innerCursor.reset();
    }

    @Override
    public Integer getPageId() {
        return innerCursor.getPageId();
    }

    @Override
    public Integer getOffset() {
        return innerCursor.getOffset();
    }

    @Override
    public void initiateCursor(Integer pageId, Integer offset) throws IOException {

    }

    @Override
    public boolean hasNext() {
        return innerCursor.hasNext();
    }

    @Override
    public Object next() {
        currentRecord = null;
        while (innerCursor.next() != null) {
            Record record = innerCursor.getCurrentRecord();
            currentRecord = project(record, selectItems);
            return currentRecord;
        }
        return null;
    }

    private Record project(Record record, List<SelectItem> selectItems) {
        Map<Column, Object> resultRecordMap = new HashMap<>();
        for (SelectItem selectItem : selectItems) {
            String columnName = ((net.sf.jsqlparser.schema.Column)((SelectExpressionItem)selectItem).getExpression()).getColumnName();
            record.getValues().entrySet().stream().filter(entry -> columnName.equals(entry.getKey().getName())).forEach(entry -> {
                resultRecordMap.put(entry.getKey(), entry.getValue());
            });
        }
        return new Record(resultRecordMap);
    }
}
