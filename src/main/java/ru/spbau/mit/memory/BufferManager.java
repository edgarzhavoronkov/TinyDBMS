package ru.spbau.mit.memory;

import ru.spbau.mit.DatabaseProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for working with cache
 *
 * Created by John on 9/12/2015.
 */
public class BufferManager {
    private Map<Integer, Page> pages;

    private FileDataManager fileDataManager;

    public BufferManager(DatabaseProperties properties) {
        this.pages = new HashMap<>(properties.getCacheSize());
    }
}
