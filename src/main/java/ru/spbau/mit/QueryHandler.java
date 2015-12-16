package ru.spbau.mit;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import ru.spbau.mit.controllers.*;
import ru.spbau.mit.cursors.Cursor;
import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.memory.Record;
import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.QueryResponse;

import java.io.IOException;
import java.util.Map;

/**
 * Created by John on 10/21/2015.
 */
public class QueryHandler {

    public static BufferManager bufferManager;

    private static CreateController createController;
    private static SelectController selectController;
    private static InsertController insertController;
    private static UpdateController updateController;
    private static DeleteController deleteController;
    private static JoinController joinController;
    private static CreateIndexController createIndexController;

    public static void queryHandler(String query) throws JSQLParserException, IOException {
        Statement statement = CCJSqlParserUtil.parse(query);
        if (statement instanceof CreateTable) {
            statusHandler(createController.process(statement));
        } else if (statement instanceof CreateIndex) {
            statusHandler(createIndexController.process(statement));
        } else if (statement instanceof Insert) {
            statusHandler(insertController.process(statement));
        } else if (statement instanceof Update) {
            statusHandler(updateController.process(statement));
        } else if (statement instanceof Delete) {
            statusHandler(deleteController.process(statement));
        } else if (statement instanceof Select) {
            if (((PlainSelect)((Select) statement).getSelectBody()).getJoins() != null) {
                selectHandler(joinController.process(statement));
            } else {
                selectHandler(selectController.process(statement));
            }
        } else {
            System.out.println("Unknown command! Please try again");
        }
    }

    public static void queryFastHandler(String query) throws JSQLParserException, IOException {
        Statement statement = CCJSqlParserUtil.parse(query);
        if (statement instanceof CreateTable) {
            createController.process(statement);
        } else if (statement instanceof Insert) {
            insertController.process(statement);
        } else if (statement instanceof Update) {
            updateController.process(statement);
        } else if (statement instanceof Select) {
            selectController.process(statement);
        } else {
            System.out.println("Unknown command! Please try again");
        }
    }

    private static void selectHandler(QueryResponse response) {
        if (response.getStatus() == QueryResponse.Status.OK) {
            Cursor cursor = response.getCursor();
            boolean isFirst = true;
            //TODO write right cursor iterating (cur record can be null)
            while (cursor.hasNext()) {
                Record currentRecord = (Record) cursor.next();
                if (currentRecord == null) continue;
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
        createIndexController = CreateIndexController.getInstance(bufferManager);
        createController = CreateController.getInstance(bufferManager);
        selectController = SelectController.getInstance(bufferManager);
        insertController = InsertController.getInstance(bufferManager);
        updateController = UpdateController.getInstance(bufferManager);
        deleteController = DeleteController.getInstance(bufferManager);
        joinController = JoinController.getInstance(bufferManager);
    }

}
