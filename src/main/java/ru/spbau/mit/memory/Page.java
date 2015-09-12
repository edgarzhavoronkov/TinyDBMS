package ru.spbau.mit.memory;

import ru.spbau.mit.meta.Table;

import java.util.List;

/**
 * Interface for page
 *
 * Created by John on 9/12/2015.
 */
public interface Page {

    List<Record> getAllRecords();

    Record getRecord(Integer num, Table table);

}
