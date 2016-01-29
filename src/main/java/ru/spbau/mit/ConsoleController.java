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

//        String cmd1 = "create table t1 (id INT, df DOUBLE)";
//        queryHandler(cmd1);
//        cmd1 = "create index index1 on t1 (id)";
//        queryHandler(cmd1);
//        String cmd2 = "create table t2 (id INT, df DOUBLE)";
//        queryHandler(cmd2);


//        System.out.println(runRandomIntegerTestInsert(1_000_000));
//        queryHandler("select * from t2 where id >= 1000 and id < 1100");

//        System.out.println(runTestInsert("t2", 100_000));
//        queryHandler("select * from t1 where id >= 10 and id < 80");


//        queryHandler("delete from t1 where id >= 0 and id < 4000");
//        queryHandler("select * from t1");

//        queryHandler("select * from t1 where id >= 50000 and id < 51000");
//        queryHandler("delete from t1 where id >= 50000 and id < 51000");
//        queryHandler("select * from t1 where id >= 50000 and id < 50100");
//        System.out.println("+++++++++++++++++++++++++++++");
//        queryHandler("select * from t1 where id >= 49900 and id < 50000");


//        queryHandler("select * from t2 where id < 50");

//        System.out.println(runRandomIntegerTestInsert(800_000));
//
        while (true) {
            String line = input.readLine();
            if (line.length() == 0 && command.length() > 0) {
                queryHandler(command.toString());
                command = new StringBuilder();
            }
            if (line.toLowerCase().trim().equals("quit")) break;
            command.append(line).append('\n');
        }
    }

    private static long runTestStringInsert(int insertTime) throws IOException, JSQLParserException {
        String cmd1 = "create table t3 (id INT, df DOUBLE, str VARCHAR(128))";
        queryHandler(cmd1);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < insertTime; i++) {
            String cmd = String.format("INSERT INTO t3 (id, df, str) VALUES (%d, %f, %s)", i, Math.random(), "'abc'");
            if (i % 10_000 == 0) {
                System.out.println(String.format("Insert %d rows", i));
            }
            queryFastHandler(cmd);
        }
        cmd1 = "create index index1 on t3 (id)";
        queryHandler(cmd1);
        return (System.currentTimeMillis() - startTime);
    }


    private static long runTestInsert(String table_name, int insertTime) throws IOException, JSQLParserException {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < insertTime; i++) {
            if (i == 49) {
                System.out.println(i);
            }
            String cmd = String.format("INSERT INTO %s (id, df) VALUES (%d, %f)", table_name, i, Math.random());
            if (i % 10_000 == 0) {
                System.out.println(String.format("Insert %d rows", i));
            }
            queryFastHandler(cmd);
        }
        return (System.currentTimeMillis() - startTime);
    }

    private static long runRandomIntegerTestInsert(int insertTime) throws IOException, JSQLParserException {
        String cmd1 = "create table t2 (id INT, df DOUBLE)";
        queryHandler(cmd1);
        long startTime = System.currentTimeMillis();
        Random random = new Random();
        for (int i = 0; i < insertTime; i++) {
            String cmd = String.format("INSERT INTO t2 (id, df) VALUES (%d, %f)", Math.abs(random.nextInt() % 10_000), Math.random());
            if ((i + 1) % 10_000 == 0) {
                System.out.println(String.format("Insert %d rows", i + 1));
//                System.out.println("Pages size = " + QueryHandler.bufferManager.getPagesSize());
//                System.out.println("Pinned size = " + QueryHandler.bufferManager.getPinedSize());
            }
            queryFastHandler(cmd);
        }
        cmd1 = "create index index1 on t2(id)";
        queryHandler(cmd1);
        return (System.currentTimeMillis() - startTime);
    }

}
