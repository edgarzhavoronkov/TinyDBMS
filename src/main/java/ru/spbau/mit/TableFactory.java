package ru.spbau.mit;

import com.google.gson.Gson;
import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.DataType;
import ru.spbau.mit.meta.Table;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by John on 10/2/2015.
 */
final public class TableFactory {

    static private Map<String, Table> tableMap = new HashMap<>();

    static public Table getTable(String tableName) throws FileNotFoundException {
        Table table = tableMap.get(tableName);
        if (table == null) {
            String folder = PropertiesManager.getProperties().getProperty("meta_folder");
            String tablePath = folder + "\\" + tableName + ".json";
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(tablePath)));
            table = new Gson().fromJson(reader, Table.class);
        }
        return table;
    }

    static public void addTable(Table table) throws IOException {
        tableMap.put(table.getName(), table);

        String folder = PropertiesManager.getProperties().getProperty("meta_folder");
        String tablePath = folder + "\\" + table.getName() + ".json";

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(tablePath), "utf-8"))) {
            writer.write(new Gson().toJson(table));
        }
    }
}
