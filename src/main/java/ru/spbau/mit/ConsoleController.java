package ru.spbau.mit;

import net.sf.jsqlparser.JSQLParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

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

//        String cmd1 = "create table t1 (id INT, a DOUBLE)";
//        queryHandler(cmd1);
//        String cmd2 = "create table t2 (id INT, b DOUBLE)";
//        queryHandler(cmd2);
//        System.out.println(runTestInsert("t1", "a", 100_000));
//        System.out.println(runTestInsert("t2", "b", 100_000));
//        queryHandler("select * from t1 where id = 2");
//        queryHandler("select * from t2 where id < 50");

        queryHandler("select t1.a, t1.b from t1 join t2 on t1.id = t2.id where t1.a > 0.5 and t2.b < 1.0 and t1.id < 2");

//        while (true) {
//            String line = input.readLine();
//            if (line.length() == 0 && command.length() > 0) {
//                queryHandler(command.toString());
//                command = new StringBuilder();
//            }
//
//            if (line.toLowerCase().trim().equals("quit")) break;
//            command.append(line).append('\n');
//        }


    }

    private static long runTestInsert(String table_name, String col_name, int insertTime) throws IOException, JSQLParserException {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < insertTime; i++) {
            String cmd = String.format("INSERT INTO %s (id, %s) VALUES (%d, %f)", table_name, col_name, i, Math.random());
            if (i % 10_000 == 0) {
                System.out.println(String.format("Insert %d rows", i));
            }
            queryFastHandler(cmd);
        }
        return (System.currentTimeMillis() - startTime);
    }

    private static long runRandomIntegerTestInsert(int insertTime) throws IOException, JSQLParserException {
        long startTime = System.currentTimeMillis();
        Random random = new Random();
        for (int i = 0; i < insertTime; i++) {
            String cmd = String.format("INSERT INTO t1 (id, df) VALUES (%d, %f)", random.nextInt()%10000, Math.random());
            if (i % 10_000 == 0) {
                System.out.println(String.format("Insert %d rows", i));
            }
            queryFastHandler(cmd);

        }
        return (System.currentTimeMillis() - startTime);
    }

}
