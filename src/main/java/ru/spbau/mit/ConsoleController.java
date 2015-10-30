package ru.spbau.mit;

import net.sf.jsqlparser.JSQLParserException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static ru.spbau.mit.QueryHandler.close;
import static ru.spbau.mit.QueryHandler.initialize;
import static ru.spbau.mit.QueryHandler.queryHandler;


/**
 * Input Output Controller
 *
 * Created by John on 9/12/2015.
 */
public class ConsoleController {

    public static void main(String[] args) throws IOException, JSQLParserException {
        System.out.println("Tiny Database command line tool\n");
        initialize();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        System.out.println("Type 2 times ENTER to execute any SQL command.");

        StringBuilder command = new StringBuilder();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("test");
        }));
        while (true) {
            String line = input.readLine();
            if(line.length() == 0 && command.length() > 0){
                queryHandler(command.toString());
                command = new StringBuilder();
            }

            if (line.toLowerCase().trim().equals("quit")) break;
            command.append(line).append('\n');
        }

        close();
    }

}
