package ru.spbau.mit;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Input Output Controller
 *
 * Created by John on 9/12/2015.
 */
public class ConsoleController {

    DatabaseProperties databaseProperties;

    public static void main(String[] args) throws IOException, JSQLParserException {
        System.out.println("Tiny Database command line tool\n");
        DatabaseProperties databaseProperties = DatabaseProperties.setOptions(args);

        System.out.println("Type 2 times ENTER to execute any SQL command.");

        StringBuilder command = new StringBuilder();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = input.readLine();
            if(line.length() == 0 && command.length() > 0){
                Statement statement = CCJSqlParserUtil.parse(command.toString());
                System.out.println(statement);
                command = new StringBuilder();
            }


            if (line.toLowerCase().trim().equals("quit")) break;

            command.append(line).append('\n');
        }
    }

}
