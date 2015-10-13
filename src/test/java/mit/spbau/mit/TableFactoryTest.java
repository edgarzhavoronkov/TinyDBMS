package mit.spbau.mit;

import org.junit.Test;
import ru.spbau.mit.TableFactory;
import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.DataType;
import ru.spbau.mit.meta.Table;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by John on 10/2/2015.
 */
public class TableFactoryTest {

    @Test
    public void createTable() throws IOException {
        ArrayList<Column> columns = new ArrayList<>();
        columns.add(new Column("col1", DataType.DOUBLE));
        columns.add(new Column("col2", DataType.VARCHAR));
        columns.add(new Column("col3", DataType.INTEGER));
        Table table = new Table("tableName", 0, 0, columns);
        TableFactory.addTable(table);
    }

    @Test
    public void getTable() throws IOException {
        createTable();
        Table table = TableFactory.getTable("tableName");

        assert (table.getName().equals("tableName"));

        assert (table.getColumns().get(0).getName().equals("col1"));
        assert (table.getColumns().get(0).getDataType().equals(DataType.DOUBLE));

        assert (table.getColumns().get(1).getName().equals("col2"));
        assert (table.getColumns().get(1).getDataType().equals(DataType.VARCHAR));

        assert (table.getColumns().get(2).getName().equals("col3"));
        assert (table.getColumns().get(2).getDataType().equals(DataType.INTEGER));

        assert (table.getRecordSize() == 140);
    }

}
