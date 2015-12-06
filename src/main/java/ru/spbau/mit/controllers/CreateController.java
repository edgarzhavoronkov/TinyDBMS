package ru.spbau.mit.controllers;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import ru.spbau.mit.TableFactory;
import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.memory.RecordPage;
import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.DataType;
import ru.spbau.mit.meta.QueryResponse;
import ru.spbau.mit.meta.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by edgar on 25.09.15.
 */
public class CreateController implements QueryController {
    private final BufferManager bufferManager;

    private CreateController(BufferManager bufferManager){
        this.bufferManager = bufferManager;
    }

    public static CreateController getInstance(BufferManager bufferManager) {
        return new CreateController(bufferManager);
    }

    //TODO: think about exceptions
    @Override
    public QueryResponse process(Statement statement) throws IOException {
        CreateTable createTable = (CreateTable) statement;
        List<ColumnDefinition> columnDefinitions = createTable.getColumnDefinitions();
        List<Column> columns = new ArrayList<>();
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            String columnName = columnDefinition.getColumnName();
            String columnDataType = columnDefinition.getColDataType().getDataType();
            DataType dataType;
            Integer size;
            switch (columnDataType) {
                case "INT" : {
                    dataType = DataType.INTEGER;
                    size = DataType.INTEGER.getSize();
                    break;
                }
                case "DOUBLE" : {
                    dataType = DataType.DOUBLE;
                    size = DataType.DOUBLE.getSize();
                    break;
                }
                case "VARCHAR" : {
                    //TODO: size?
                    dataType = DataType.VARCHAR;
                    size = Integer.parseInt(columnDefinition.getColDataType().getArgumentsStringList().get(0));
                    break;
                }
                default : {
                    throw new IOException("Wrong data type");
                }
            }
            Column column = new Column(columnName, dataType);
            column.setSize(size);
            columns.add(column);
        }
        String tableName = createTable.getTable().getName();
        //TODO: is there a better way to do this?!
        try {
            RecordPage firstFreeRecordPage = bufferManager.getFirstRecordFreePage(null);
            firstFreeRecordPage.setNextPageId(-1);
            Table table = new Table(tableName, firstFreeRecordPage.getId(), firstFreeRecordPage.getId(), columns);
            firstFreeRecordPage.setTable(table);
            TableFactory.addTable(table);
            return new QueryResponse(QueryResponse.Status.OK, 0);
        } catch (IOException e) {
            QueryResponse response = new QueryResponse(QueryResponse.Status.Error, 0);
            response.setErrorMessageText(e.getMessage());
            return response;
        }
    }

}
