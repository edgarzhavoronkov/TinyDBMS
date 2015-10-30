package ru.spbau.mit;

import net.sf.jsqlparser.JSQLParserException;

import java.io.*;

import static ru.spbau.mit.QueryHandler.close;
import static ru.spbau.mit.QueryHandler.initialize;
import static ru.spbau.mit.QueryHandler.queryHandler;

/**
 * Created by John on 10/21/2015.
 */
public class ScriptController {

    public static void main(String[] args) throws IOException, JSQLParserException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("./scripts/testScript.txt"))));
        initialize();
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            System.out.println(line);
            queryHandler(line);
        }
        close();
    }

}
