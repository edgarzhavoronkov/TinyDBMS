package ru.spbau.mit.memory;


import java.util.Map;

/**
 * Class for storage record data
 *
 * Created by John on 9/12/2015.
 */
public class Record {
    private Map<String, Object> values;

    public Record(Map<String, Object> values) {
        this.values = values;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }
}
