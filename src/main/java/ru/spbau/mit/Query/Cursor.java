package ru.spbau.mit.Query;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by wimag - future cursor functionaloty on 09.10.2015.
 */
public interface Cursor extends Iterator{
    public void initiateCursor(Integer pageId, Integer offset) throws IOException;
}
