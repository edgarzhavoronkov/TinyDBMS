package ru.spbau.mit;

import com.google.gson.Gson;
import ru.spbau.mit.meta.Table;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by John on 10/2/2015.
 */
final public class TableFactory {

    static private Map<String, Table> tableMap;

//    public static void main(String[] args) throws FileNotFoundException {
//        Table test_table = getTable("test_table");
//        System.out.println(test_table);
//    }

//    public static void main(String[] args) {
//        ArrayList<Column> columns = new ArrayList<>();
//        columns.add(new Column("col1", DataType.DOUBLE));
//        columns.add(new Column("col2", DataType.VARCHAR));
//        columns.add(new Column("col3", DataType.INTEGER));
//        Table table = new Table("tableName", columns);
//        String json = new Gson().toJson(table);
//        System.out.println(json);
//    }

    static public Table getTable(String tableName) throws FileNotFoundException {
        if (tableMap == null) {
            tableMap = new HashMap<>();
        }
        Table table = tableMap.get(tableName);
        if (table == null) {
            String folder = PropertiesManager.getProperties().getProperty("meta_folder");
            //todo filepath add folder
            String tablePath = tableName + ".json";
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(tablePath)));
            table = new Gson().fromJson(reader, Table.class);
        }
        return table;
    }

    static public void addTable(Table table) {
        //todo filepath add folder


    }
}
