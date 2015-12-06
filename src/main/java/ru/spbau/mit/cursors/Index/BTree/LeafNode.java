package ru.spbau.mit.Cursors.Index.BTree;

/**
 * Created by gellm_000 on 06.12.2015.
 */
public class LeafNode extends Node{
    private static class Entry{
        int pageId;
        int offset;
    }

    private final static int LEAFCAPACITY = 100;
    private Entry[] entries;

}
