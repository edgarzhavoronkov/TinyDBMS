package ru.spbau.mit.cursors;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.memory.RecordPage;
import ru.spbau.mit.memory.Record;
import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by edgar on 30.10.15.
 */
public class WhereCursor implements Cursor {
    private final Table table;
    private Integer pageId, offset;
    private RecordPage currentRecordPage = null;
    private final BufferManager bufferManager;
    private final List<Column> fields;
    private Record currentRecord;
    private Expression whereExpression;

    public WhereCursor(BufferManager bufferManager, Table table, Integer pageId, Integer offset, Expression whereExpression) throws IOException {
        this(bufferManager, table);
        this.pageId = pageId;
        this.offset = offset;
        this.whereExpression = whereExpression;
        initiateCursor(pageId, offset);
    }

    public WhereCursor(BufferManager bufferManager, Table table) {
        this.table = table;
        fields = new ArrayList<>(table.getColumns());
        this.bufferManager = bufferManager;
    }

    public WhereCursor(Cursor cursor, Expression whereExpression) throws IOException {
        this(cursor.getBufferManager(), cursor.getTable(), cursor.getPageId(), cursor.getOffset(), whereExpression);
    }

    @Override
    public Record getCurrentRecord() {
        return currentRecord;
    }

    @Override
    public BufferManager getBufferManager() {
        return bufferManager;
    }

    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public Integer getPageId() {
        return pageId;
    }

    @Override
    public Integer getOffset() {
        return offset;
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
        while (!match(currentRecord, whereExpression))
        {
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

    private boolean match(Record record, Expression expression) {
        if (expression instanceof OrExpression) {
            return match(record, ((OrExpression) expression).getLeftExpression())
                    ||
                    match(record, ((OrExpression) expression).getRightExpression());
        } else if (expression instanceof AndExpression) {
            return match(record, ((AndExpression) expression).getLeftExpression())
                    &&
                    match(record, ((AndExpression) expression).getRightExpression());
        } else if (expression instanceof GreaterThan) {
            return directCompare(record, expression, (compareToResult) -> compareToResult > 0);
        } else if (expression instanceof MinorThan) {
            return directCompare(record, expression, (compareToResult) -> compareToResult < 0);
        } else if (expression instanceof GreaterThanEquals) {
            return directCompare(record, expression, (compareToResult) -> compareToResult >= 0);
        } else if (expression instanceof MinorThanEquals) {
            return directCompare(record, expression, (compareToResult) -> compareToResult <= 0);
        } else if (expression instanceof EqualsTo) {
            return directCompare(record, expression, (compareToResult) -> compareToResult == 0);
        }
        return false;
    }

    //TODO: xtract tablenames e.g t1.id -> (t1, id)
    private boolean directCompare(Record record, Expression expression, Predicate<Integer> comparator) {
        Expression lhs = ((GreaterThanEquals) expression).getLeftExpression();
        Expression rhs = ((GreaterThanEquals) expression).getRightExpression();
        if (lhs instanceof Column) {
            Object value = getValueFromColumn(record, (Column) lhs);
            if (rhs instanceof LongValue) {
                Integer lhs_value = (Integer) value;
                Integer rhs_value = new Long(((LongValue) rhs).getValue()).intValue();
                assert lhs_value != null;
                return comparator.test(lhs_value.compareTo(rhs_value));
            } else if (rhs instanceof DoubleValue) {
                Double lhs_value = (Double) value;
                Double rhs_value = ((DoubleValue) rhs).getValue();
                assert lhs_value != null;
                return comparator.test(lhs_value.compareTo(rhs_value));
            } else if (rhs instanceof StringValue) {
                String lhs_value = (String) value;
                String rhs_value = ((StringValue) rhs).getValue();
                assert lhs_value != null;
                return comparator.test(lhs_value.compareTo(rhs_value));
            } else {
                return false;
            }
        }
        return false;
    }

    private Object getValueFromColumn(Record record, Column column) {
        String columnName;
        Object value = null;
        columnName = column.getName();
        for (Map.Entry<Column, Object> entry : record.getValues().entrySet()) {
            if (entry.getKey().getName().equals(columnName)) {
                value = entry.getValue();
                break;
            }
        }
        return value;
    }
}
