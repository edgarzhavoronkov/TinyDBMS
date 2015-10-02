package mit.spbau.mit;

import org.junit.Test;
import ru.spbau.mit.TableFactory;

import java.io.FileNotFoundException;

/**
 * Created by John on 10/2/2015.
 */
public class TableFactoryTest {
    @Test
    public void testTableCreate() throws FileNotFoundException {

        TableFactory.getTable("test_table");

    }
}
