package mit.spbau.mit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.mit.PropertiesManager;
import ru.spbau.mit.QueryHandler;
import ru.spbau.mit.TableFactory;
import ru.spbau.mit.memory.Record;
import ru.spbau.mit.memory.page.BasePage;
import ru.spbau.mit.memory.page.RecordPage;
import ru.spbau.mit.memory.page.RecordPageImpl;
import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.Table;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PageTest {
    private final String FIRST_FREE_PAGE = "first_free_page";
    private final String DATA_FILE_NAME = "data_file_name";

    private Integer firstFreePage;
    private String dataFileName;

    @Before
    public void beforeTest() throws IOException {
        Properties properties = PropertiesManager.getProperties();

        firstFreePage = Integer.valueOf(properties.getProperty(FIRST_FREE_PAGE));
        dataFileName = properties.getProperty(DATA_FILE_NAME);

        properties.setProperty(DATA_FILE_NAME, "data_test.tdb");
        properties.setProperty(FIRST_FREE_PAGE, "1");

        File file = new File(properties.getProperty("dir_path") + "/" + "data_test.tdb");
        file.delete();

        QueryHandler.initialize();
    }

    @Test
    public void getFreeBasePage() throws IOException {
        BasePage firstFreePage = QueryHandler.bufferManager.getFirstFreePage();

        assert (firstFreePage.getId() == 0);
        assert (firstFreePage.getLastOperationId() == 0);

        assert (!firstFreePage.isDirty());
        firstFreePage.makeDirty();
        assert (firstFreePage.isDirty());

        firstFreePage.makeClean();
        assert (!firstFreePage.isDirty());

        byte[] data = firstFreePage.getData();
        for (byte b : data) {
            assert (b == 0);
        }
    }

    public static void main(String[] args) throws IOException {
        PageTest pageTest = new PageTest();
        pageTest.getRecordPage();
    }

    public void getRecordPage() throws IOException {
        TableFactoryTest tableFactoryTest = new TableFactoryTest();
        tableFactoryTest.createTable();

        Table table = TableFactory.getTable("tableName");

        RecordPage recordPage = new RecordPageImpl(QueryHandler.bufferManager.getFirstFreePage(), table);
        Map<Column, Object> values = new HashMap<>();
        for (Column column : table.getColumns()) {
            switch (column.getDataType()) {
                case INTEGER:
                    values.put(column, 1);
                    break;
                case DOUBLE:
                    values.put(column, 3.0);
                    break;
                case VARCHAR:
                    values.put(column, "abc");
                    break;
            }
        }
        Record record = new Record(values);

        while (recordPage.isFreeSpace()) {
            recordPage.putRecord(record);
        }
    }

    @After
    public void afterTest() throws IOException {
        Properties properties = PropertiesManager.getProperties();

        properties.setProperty(DATA_FILE_NAME, dataFileName);
        properties.setProperty(FIRST_FREE_PAGE, String.valueOf(firstFreePage));

        QueryHandler.close();
    }

}
