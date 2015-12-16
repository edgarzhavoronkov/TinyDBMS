package ru.spbau.mit.cursors;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.memory.Record;
import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.Table;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by edgar on 13.12.15.
 */
public class JoinCursor implements Cursor {
    private Cursor leftCursor;
    private Cursor rightCursor;
    private Expression onExpression;

    public JoinCursor(Cursor leftCursor, Cursor rightCursor, Expression onExpression) throws IOException {
        this.leftCursor = leftCursor;
        this.rightCursor = rightCursor;
        this.onExpression = onExpression;
    }

    @Override
    public Record getCurrentRecord() {
        return null;
    }

    @Override
    public BufferManager getBufferManager() {
        return leftCursor.getBufferManager();
    }

    @Override
    public Table getTable() {

        return null;
    }



    @Override
    public Cursor clone() {
        try {
            return new JoinCursor(leftCursor.clone(), rightCursor.clone(), onExpression);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Integer getPageId() {
        return null;
    }

    @Override
    public Integer getOffset() {
        return null;
    }

    @Override
    public void initiateCursor(Integer pageId, Integer offset) throws IOException {  }

    @Override
    public boolean hasNext() {
        //TODO: HOW THE FUCK?!
        return false;
    }

    @Override
    public Object next() {
        while (leftCursor.next() != null) {
            Record leftRecord = leftCursor.getCurrentRecord();
            Cursor rightCursorCopy = rightCursor.clone();
            while (rightCursorCopy.next() != null) {
                Record rightRecord = rightCursorCopy.getCurrentRecord();
                if (match(leftRecord, rightRecord, onExpression)) {
                    return concatRecords(leftRecord, rightRecord, onExpression);
                }
            }
        }
        return null;
    }

    private Record concatRecords(Record leftRecord, Record rightRecord, Expression onExpression) {
        //TODO: another infernal wheelchair
        if (onExpression instanceof EqualsTo) {
            Expression lhs = ((EqualsTo) onExpression).getLeftExpression();
            Expression rhs = ((EqualsTo) onExpression).getRightExpression();
            String leftColumnName = ((net.sf.jsqlparser.schema.Column)lhs).getColumnName();
            String rightColumnName = ((net.sf.jsqlparser.schema.Column)rhs).getColumnName();

            Map<Column, Object> resultRecordMap = new HashMap<>();

            for (Map.Entry<Column, Object> entry : leftRecord.getValues().entrySet()) {
                String columnName = entry.getKey().getName();
                Object value = entry.getValue();
                if (!columnName.equals(leftColumnName)) {
                    resultRecordMap.put(entry.getKey(), value);
                }
            }

            for (Map.Entry<Column, Object> entry : rightRecord.getValues().entrySet()) {
                String columnName = entry.getKey().getName();
                Object value = entry.getValue();
                if (!columnName.equals(rightColumnName)) {
                    entry.getKey().setName(entry.getKey().getName() + "1");
                    resultRecordMap.put(entry.getKey(), value);
                }
            }
            return new Record(resultRecordMap);
        }
        return null;
    }

    private boolean match(Record leftRecord, Record rightRecord, Expression onExpression) {
        //TODO: where tables are taken from??
        //TODO: INFERNAL WHEELCHAIR
        if (onExpression instanceof EqualsTo) {
            Expression lhs = ((EqualsTo) onExpression).getLeftExpression();
            Expression rhs = ((EqualsTo) onExpression).getRightExpression();
            String leftColumnName = ((net.sf.jsqlparser.schema.Column)lhs).getColumnName();
            String rightColumnName = ((net.sf.jsqlparser.schema.Column)rhs).getColumnName();
            Object leftValue = getValueFromColumn(leftRecord, leftColumnName);
            Object rightValue = getValueFromColumn(rightRecord, rightColumnName);
            assert leftValue != null;
            assert rightValue != null;
            return leftValue.equals(rightValue);
        } else {
            //TODO: exception maybe?
        }
        return false;
    }

    private Object getValueFromColumn(Record record, String columnName) {
        Object value = null;
        for (Map.Entry<Column, Object> entry : record.getValues().entrySet()) {
            if (entry.getKey().getName().equals(columnName)) {
                value = entry.getValue();
                break;
            }
        }
        return value;
    }
}
