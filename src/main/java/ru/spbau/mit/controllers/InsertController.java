package ru.spbau.mit.controllers;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import ru.spbau.mit.TableFactory;
import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.memory.Page;
import ru.spbau.mit.memory.Record;
import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.QueryResponse;
import ru.spbau.mit.meta.Table;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by edgar on 25.09.15.
 */
public class InsertController implements QueryController {
    private final BufferManager bufferManager;

    private InsertController(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
    }

    public static InsertController getInstance(BufferManager bufferManager) {
        return new InsertController(bufferManager);
    }

    @Override
    public QueryResponse process(Statement statement) throws IOException {

        try {
            Insert insert = (Insert) statement;

            Table table = TableFactory.getTable(insert.getTable().getName());
            Record record = getRecord(table, insert);

            Page page = bufferManager.getPage(table.getFirstFreePageId(), table);
            page.putRecord(record);

            if (!page.isFreeSpace()) {
                Page firstFreePage = bufferManager.getFirstFreePage();
                firstFreePage.setTable(table);
                page.setNextPageId(firstFreePage.getId());
                table.setFirstFreePageId(firstFreePage.getId());
                firstFreePage.setNextPageId(-1);
            }

            return new QueryResponse(QueryResponse.Status.OK, 1);
        } catch (SQLParserException e) {
            return new QueryResponse(QueryResponse.Status.Error, e);
        }
    }

    private Record getRecord(Table table, Insert insert) throws SQLParserException {
        Map<String, Object> valueMap = getValueMap(insert);

        if (valueMap.size() != table.getColumns().size()) {
            throw new SQLParserException("The discrepancy between the number of columns.", insert);
        }

        String emptyColumns = table.getColumns()
                .parallelStream()
                .filter(column -> !valueMap.containsKey(column.getName()))
                .map(Column::getName)
                .sequential()
                .collect(Collectors.joining(", "));

        if (emptyColumns.length() > 0) {
            throw new SQLParserException("For these columns, there are no values " + emptyColumns, insert);
        }

        Map<Column, Object> recordValue = table.getColumns()
                .parallelStream()
                .collect(Collectors.toMap(
                                column -> column,
                                (java.util.function.Function<Column, Object>) (key) -> valueMap.get(key.getName()))
                );
        return new Record(recordValue);
    }

    private Map<String, Object> getValueMap(Insert statement) throws SQLParserException {
        List<Expression> expressions = ((ExpressionList) statement.getItemsList()).getExpressions();
        List<net.sf.jsqlparser.schema.Column> columns = statement.getColumns();

        Map<String, Object> valueMap = new HashMap<>(columns.size());
        for (int i = 0; i < columns.size(); i++) {
            valueMap.put(
                    columns.get(i).getColumnName(),
                    getValue(expressions.get(i))
            );
        }

        return valueMap;
    }

    private Object getValue(Expression expression) throws SQLParserException {
        if (expression instanceof LongValue) {
            return (int) ((LongValue) expression).getValue();
        }
        if (expression instanceof DoubleValue) {
            return ((DoubleValue) expression).getValue();
        }
        if (expression instanceof StringValue) {
            return ((StringValue) expression).getValue();
        }
        throw new SQLParserException("Unknown value type " + expression.getClass());
    }

}
