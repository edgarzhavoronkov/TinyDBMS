//package mit.spbau.mit;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import ru.spbau.mit.DatabaseProperties;
//import ru.spbau.mit.memory.FileDataManager;
//import ru.spbau.mit.memory.Page;
//import ru.spbau.mit.memory.Record;
//import ru.spbau.mit.meta.Column;
//import ru.spbau.mit.meta.DataType;
//import ru.spbau.mit.meta.Table;
//
//import java.io.IOException;
//import java.util.*;
//
///**
// * Created by John on 9/13/2015.
// */
//public class FileDataManagerTest {
//    DatabaseProperties databaseProperties;
//    FileDataManager fileDataManager;
//
//    @Before
//    public void init() throws IOException {
//        databaseProperties = DatabaseProperties.setOptions(new String[]{});
//        fileDataManager = new FileDataManager(databaseProperties);
//    }
//
//    @Test
//    public void insertOneRecord() throws IOException {
//        Page page = fileDataManager.getPageById(0);
//
//        ArrayList<Column> columns = new ArrayList<>();
//        columns.add(new Column("col_int", DataType.INTEGER));
//        columns.add(new Column("col_double", DataType.DOUBLE));
//        columns.add(new Column("col_string", DataType.VARCHAR));
//
//
//        Table table = new Table("test", columns);
//
//        Map<String, Object> values = new HashMap<>();
//        values.put("col_int", 42);
//        values.put("col_double", 31.31);
//        values.put("col_string", "str132");
//
//        Record record = new Record(values);
//
//        page.putRecord(record, table);
//
//
//    }
//
//    @After
//    public void quit() throws IOException {
//        fileDataManager.onQuit();
//    }
//
//
//}
