package ru.spbau.mit;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import ru.spbau.mit.PropertiesManager;
import ru.spbau.mit.TableFactory;
import ru.spbau.mit.controllers.CreateController;
import ru.spbau.mit.controllers.InsertController;
import ru.spbau.mit.controllers.SelectController;
import ru.spbau.mit.controllers.UpdateController;
import ru.spbau.mit.cursors.Cursor;
import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.memory.Record;
import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.QueryResponse;
import ru.spbau.mit.meta.Table;

import java.io.IOException;
import java.util.Map;

/**
 * Created by John on 10/21/2015.
 */
public class QueryHandler {

    static BufferManager bufferManager;

    static CreateController createController;
    static SelectController selectController;
    static InsertController insertController;
    static UpdateController updateController;

    public static void queryHandler(String query) throws JSQLParserException, IOException {
        Statement statement = CCJSqlParserUtil.parse(query);
        if (statement instanceof CreateTable) {
            statusHandler(createController.process(statement));
        } else if (statement instanceof Insert) {
            statusHandler(insertController.process(statement));
        } else if (statement instanceof Update) {
            updateController.process(statement);
        } else if (statement instanceof Select) {
            selectHandler(selectController.process(statement));
        } else {
            System.out.println("Unknown command! Please try again");
        }
    }

    private static void selectHandler(QueryResponse response) {
        if (response.getStatus() == QueryResponse.Status.OK) {
            Cursor cursor = response.getCursor();
            boolean isFirst = true;
            while (cursor.next() != null) {
                Record currentRecord = cursor.getCurrentRecord();
                if (isFirst) {
                    for (Column column : currentRecord.getValues().keySet()) {
                        System.out.printf("%" + column.getSize() + "s |", column.getName());
                    }
                    System.out.println();
                    isFirst = false;
                }
                Map<Column, Object> values = currentRecord.getValues();
                for (Column column : values.keySet()) {
                    switch (column.getDataType()) {
                        case INTEGER:
                            System.out.printf("%" + column.getSize() + "d |", values.get(column));
                            break;
                        case DOUBLE:
                            System.out.printf("%8.2f |", ((double) values.get(column)));
                            break;
                        case VARCHAR:
                            System.out.printf("%" + column.getSize() + "s |", values.get(column));
                            break;
                    }
                }
                System.out.println();
            }
            System.out.println("OK");
        } else {
            System.out.println("ERROR" + response.getErrorMessageText());
        }
    }

    private static void statusHandler(QueryResponse response) {
        if (response.getStatus() == QueryResponse.Status.OK) {
            System.out.println("OK");
        } else {
            System.out.println("ERROR" + response.getErrorMessageText());
        }
    }

    public static void close() throws IOException {
        bufferManager.close();
        TableFactory.close();

        PropertiesManager.close();
    }

    public static void initialize() throws IOException {
        bufferManager = new BufferManager();
        createController = CreateController.getInstance(bufferManager);
        selectController = SelectController.getInstance(bufferManager);
        insertController = InsertController.getInstance(bufferManager);
        updateController = UpdateController.getInstance(bufferManager);
    }

}
