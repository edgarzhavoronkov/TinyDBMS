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

    private static Random rand = new Random();

    private static String randomString(int len){
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append((char)('a' + rand.nextInt('z' - 'a')));
        }
        return sb.toString();
    }

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

//        String cmd1 = "create table t (id INT, rand_int INT, str VARCHAR(11))";
//        String cmd2 = "create table numbers (num_id INT, num VARCHAR(11))";
//        queryHandler(cmd1);
//        queryHandler(cmd2);
//        for (int i=0; i < 1; ++i) {
//            queryHandler(String.format("insert into t(id, rand_int, str) values(%d, %d, '%s')", i+1, rand.nextInt(10) + 1, randomString(10)));
//        }
//
//
//        String[] nums = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"};
//        for (int i = 0; i < 10; ++i) {
//            queryHandler(String.format("insert into numbers(num_id, num) values(%d, '%s')", i + 1, nums[i]));
//        }
//        queryHandler("insert into numbers(num_id, num) values(1, 'one_plus')");

//        queryHandler("select * from t join numbers on id = num_id");
//        queryHandler("select * from t join numbers on num_id = id");
//        queryHandler("select * from numbers join t on id = num_id");
//        queryHandler("select * from numbers join t on num_id = id");

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
