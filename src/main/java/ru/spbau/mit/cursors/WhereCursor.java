package ru.spbau.mit.cursors;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.memory.Record;
import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.Table;

import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by edgar on 30.10.15.
 */
public class WhereCursor implements Cursor {
    private final Table table;
    private final BufferManager bufferManager;
    private Record currentRecord;
    private Cursor innerCursor;
    private Expression whereExpression;


    public WhereCursor(Cursor cursor, Expression whereExpression) throws IOException {
        this.innerCursor = cursor;
        this.whereExpression = whereExpression;
        this.table = innerCursor.getTable();
        this.bufferManager = innerCursor.getBufferManager();
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
    public Cursor clone() {
        try {
            return new WhereCursor(innerCursor.clone(), whereExpression);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void initiateCursor(Integer pageId, Integer offset) throws IOException {  }

    @Override
    public boolean hasNext() {
        return innerCursor.hasNext();
    }

    @Override
    public Object next() {
        currentRecord = null;
        while (innerCursor.hasNext()) {
            Record record = (Record) innerCursor.next();
            if (match(record, whereExpression)) {
                currentRecord = record;
                return currentRecord;
            }
        }
        return null;
    }

    private boolean match(Record record, Expression expression) {
        if (record == null) {
            return false;
        }
        if (expression == null) {
            return true;
        }
        if (expression instanceof OrExpression) {
            return match(record, ((OrExpression) expression).getLeftExpression())
                    ||
                    match(record, ((OrExpression) expression).getRightExpression());
        } else if (expression instanceof AndExpression) {
            return match(record, ((AndExpression) expression).getLeftExpression())
                    &&
                    match(record, ((AndExpression) expression).getRightExpression());
        } else if (expression instanceof GreaterThan) {
            return directCompare(record,
                    ((GreaterThan)expression).getLeftExpression(),
                    ((GreaterThan)expression).getRightExpression(),
                    (compareToResult) -> compareToResult > 0);
        } else if (expression instanceof MinorThan) {
            return directCompare(record,
                    ((MinorThan) expression).getLeftExpression(),
                    ((MinorThan) expression).getRightExpression(),
                    (compareToResult) -> compareToResult < 0);
        } else if (expression instanceof GreaterThanEquals) {
            return directCompare(record,
                    ((GreaterThanEquals) expression).getLeftExpression(),
                    ((GreaterThanEquals) expression).getRightExpression(),
                    (compareToResult) -> compareToResult >= 0);
        } else if (expression instanceof MinorThanEquals) {
            return directCompare(record,
                    ((MinorThanEquals) expression).getLeftExpression(),
                    ((MinorThanEquals) expression).getRightExpression(),
                    (compareToResult) -> compareToResult <= 0);
        } else if (expression instanceof EqualsTo) {
            return directCompare(record,
                    ((EqualsTo) expression).getLeftExpression(),
                    ((EqualsTo) expression).getRightExpression(),
                    (compareToResult) -> compareToResult == 0);
        }
        return false;
    }

    private boolean directCompare(Record record, Expression lhs, Expression rhs, Predicate<Integer> comparator) {
        if (lhs instanceof net.sf.jsqlparser.schema.Column) {

            Object value = getValueFromColumn(record, ((net.sf.jsqlparser.schema.Column) lhs).getColumnName());
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
