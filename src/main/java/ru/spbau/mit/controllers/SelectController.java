package ru.spbau.mit.controllers;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import ru.spbau.mit.TableFactory;
import ru.spbau.mit.cursors.Cursor;
import ru.spbau.mit.cursors.FullScanCursor;
import ru.spbau.mit.cursors.WhereCursor;
import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.meta.QueryResponse;
import ru.spbau.mit.meta.Table;

import java.io.IOException;
import java.io.StringReader;


/**
 * Created by edgar on 25.09.15.
 */
public class SelectController implements QueryController {
    private final BufferManager bufferManager;
    private SelectController(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
    }

    public static SelectController getInstance(BufferManager bufferManager) {
        return new SelectController(bufferManager);
    }

    @Override
    public QueryResponse process(Statement statement) throws IOException {
        try {
            if(!(statement instanceof Select)){
                throw new SQLParserException("Not a select statement: ", statement);
            }
            PlainSelect plainSelect = (PlainSelect)(((Select) statement).getSelectBody());
            String tableName = ((net.sf.jsqlparser.schema.Table)plainSelect.getFromItem()).getName();
            Table table = TableFactory.getTable(tableName);
            if(plainSelect.getSelectItems().get(0) instanceof AllColumns) {
                if (plainSelect.getWhere() != null) {
                    String columnName = ((Column)(((EqualsTo)plainSelect.getWhere()).getLeftExpression())).getColumnName();
                    Object value;
                    if (((EqualsTo)plainSelect.getWhere()).getRightExpression() instanceof LongValue) {
                        value = new Long(((LongValue)((EqualsTo)plainSelect.getWhere()).getRightExpression()).getValue()).intValue();
                    } else if (((EqualsTo)plainSelect.getWhere()).getRightExpression() instanceof DoubleValue) {
                        value = ((DoubleValue)((EqualsTo)plainSelect.getWhere()).getRightExpression()).getValue();
                    } else if (((EqualsTo)plainSelect.getWhere()).getRightExpression() instanceof StringValue) {
                        value = ((StringValue)((EqualsTo)plainSelect.getWhere()).getRightExpression()).getValue();
                    } else {
                        throw new SQLParserException("Data type does not match column data type!");
                    }
                    Cursor cursor = new WhereCursor(bufferManager, table, table.getFirstPageId(), 0, columnName, value);
                    return new QueryResponse(QueryResponse.Status.OK, cursor);
                }
                Cursor cursor = new FullScanCursor(bufferManager, table, table.getFirstPageId(), 0);
                return new QueryResponse(QueryResponse.Status.OK, cursor);
            }

            return null;
        } catch (SQLParserException e) {
            return new QueryResponse(QueryResponse.Status.Error, e);
        }
    }

    public static void main(String[] args) throws JSQLParserException {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        String statement = "SELECT * from R as k, S where k.B = S.B";
        PlainSelect plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        System.out.println(plainSelect.getFromItem());
        System.out.println(plainSelect.getSelectItems().get(0) instanceof AllColumns);

    }

}
