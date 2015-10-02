package ru.spbau.mit.memory;


import ru.spbau.mit.meta.Column;

import java.util.Map;

/**
 * Class for storage record data
 *
 * Created by John on 9/12/2015.
 */
public class Record {
    private Map<Column, Object> values;

    public Record(Map<Column, Object> values) {
        this.values = values;
    }

    public Map<Column, Object> getValues() {
        return values;
    }

    public void setValues(Map<Column, Object> values) {
        this.values = values;
    }
}
