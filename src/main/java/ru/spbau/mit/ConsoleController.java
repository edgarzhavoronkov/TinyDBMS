package ru.spbau.mit;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import ru.spbau.mit.controllers.*;
import ru.spbau.mit.cursors.Cursor;
import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.memory.Record;
import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.QueryResponse;
import ru.spbau.mit.meta.Table;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Input Output Controller
 *
 * Created by John on 9/12/2015.
 */
public class ConsoleController {
    static BufferManager bufferManager;

    static CreateController createController;
    static SelectController selectController;
    static InsertController insertController;
    static UpdateController updateController;

    public static void main(String[] args) throws IOException, JSQLParserException {
        System.out.println("Tiny Database command line tool\n");
        initialize();

        System.out.println("Type 2 times ENTER to execute any SQL command.");

        StringBuilder command = new StringBuilder();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = input.readLine();
            if(line.length() == 0 && command.length() > 0){
                queryHandler(command.toString());
                //System.out.println(statement);
                command = new StringBuilder();
            }

            if (line.toLowerCase().trim().equals("quit")) break;
            command.append(line).append('\n');
        }

        close();
    }

    private static void queryHandler(String query) throws JSQLParserException, IOException {
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
            Table table = cursor.getTable();
            for (Column column : table.getColumns()) {
                System.out.print(column.getName() + " ");
            }
            System.out.println();
            while (cursor.next() != null) {
                Record currentRecord = cursor.getCurrentRecord();
                Map<Column, Object> values = currentRecord.getValues();
                for (Column column : values.keySet()) {
                    System.out.print(values.get(column) + " ");
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

    private static void close() throws IOException {
        bufferManager.close();
        TableFactory.close();

        PropertiesManager.close();
    }

    private static void initialize() throws IOException {
        bufferManager = new BufferManager();
        createController = CreateController.getInstance(bufferManager);
        selectController = SelectController.getInstance(bufferManager);
        insertController = InsertController.getInstance(bufferManager);
        updateController = UpdateController.getInstance(bufferManager);
    }

}
