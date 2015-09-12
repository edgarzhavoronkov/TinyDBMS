package ru.spbau.mit.memory;

import ru.spbau.mit.DatabaseProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for working with cache
 *
 * Created by John on 9/12/2015.
 */
public class BufferManager {
    private List<Page> pages;

    public BufferManager(DatabaseProperties properties) {
        this.pages = new ArrayList<>(properties.getCacheSize());
    }
}
