package ru.spbau.mit.cursors.Index.BTree;

/**
 * Created by gellm_000 on 06.12.2015.
 */
public class LeafNode extends Node{
    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public int find(int key) {
        for (int i = 0; i < getSize(); i++) {
            if(getKeyAt(i) == key){
                return i;
            }

        }
    }

    @Override
    protected Node split() {
        return null;
    }

    @Override
    protected Node pushToParent(int key, Node leftChild, Node rightChild) {
        return null;
    }

    @Override
    protected void transferChildren(Node receiver, Node donor, int donationIndex) {

    }

    @Override
    protected Node FuseChildren(Node leftChild, Node rightChild) {
        return null;
    }

    @Override
    protected void FuseWithSibling(int separationKey, Node rightNode) {

    }

    @Override
    protected int getKeyFromSibling(int separationKey, Node sibling, int donationIndex) {
        return 0;
    }

    private static class Entry{
        int pageId;
        int offset;
    }

    private final static int LEAFCAPACITY = 100;
    private Entry[] entries;

}
