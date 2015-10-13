package ru.spbau.mit;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import ru.spbau.mit.controllers.*;
import ru.spbau.mit.memory.BufferManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
                Statement statement = CCJSqlParserUtil.parse(command.toString());

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

                System.out.println(statement);
                command = new StringBuilder();
            }

            if (line.toLowerCase().trim().equals("quit")) break;
            command.append(line).append('\n');
        }


        onQuit();
    }

    private static void onQuit() throws IOException {
        bufferManager.onQuit();

        PropertiesManager.onQuit();
    }

    private static void initialize() throws IOException {
        bufferManager = new BufferManager();
        createController = CreateController.getInstance(bufferManager);
        selectController = SelectController.getInstance(bufferManager);
        insertController = InsertController.getInstance(bufferManager);
        updateController = UpdateController.getInstance(bufferManager);
    }

}
