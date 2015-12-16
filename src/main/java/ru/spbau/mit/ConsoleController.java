package ru.spbau.mit;

import net.sf.jsqlparser.JSQLParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static ru.spbau.mit.QueryHandler.*;


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

//        String cmd1 = "create table t1 (id INT, df DOUBLE)";
//        queryHandler(cmd1);
//        System.out.println(runTestInsert(10_000));
//        cmd1 = "create index index1 on t1(id)";
//        queryHandler(cmd1);

        while (true) {
            String line = input.readLine();
            if (line.length() == 0 && command.length() > 0) {
                queryHandler(command.toString());
                command = new StringBuilder();
            }

            if (line.toLowerCase().trim().equals("quit")) break;
            command.append(line).append('\n');
        }

//        create index index1 on t1(id)

//        String cmd1 = "create table t1 (id INT, df DOUBLE)";
//        queryHandler(cmd1);
//        System.out.println(runTestInsert(10_000));
//        cmd1 = "create index index1 on t1(id)";
//        queryHandler(cmd1);
    }

    private static long runTestInsert(int insertTime) throws IOException, JSQLParserException {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < insertTime; i++) {
            String cmd = String.format("INSERT INTO t1 (id, df) VALUES (%d, %f)", i, Math.random());
            if (i % 10_000 == 0) {
                System.out.println(String.format("Insert %d rows", i));
            }
            queryFastHandler(cmd);
        }
        return (System.currentTimeMillis() - startTime);
    }

}
