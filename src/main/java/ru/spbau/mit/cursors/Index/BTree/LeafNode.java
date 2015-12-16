package ru.spbau.mit.cursors.Index.BTree;

import ru.spbau.mit.QueryHandler;
import ru.spbau.mit.memory.page.LeafNodePage;
import ru.spbau.mit.memory.page.LeafNodePageImpl;

import java.io.IOException;

/**
 * Created by gellm_000 on 06.12.2015.
 */
public class LeafNode extends Node {

    public LeafNode() throws IOException {
        super();
        nodePage = new LeafNodePageImpl(QueryHandler.bufferManager.getFirstFreePage());
        nodePage.setSize(0);
        nodePage.setLeftNodePageId(null);
        nodePage.setRightNodePageId(null);
        nodePage.setParentNodePageId(null);
        nodePage.setIsLeaf(true);
        //TODO check if filled with NULL
    }

    public LeafNode(LeafNodePage page) {
        super();
        nodePage = page;
    }

    public LeafEntry getEntryAt(int index) {
        return ((LeafNodePage) nodePage).getEntryAt(index);
    }

    public void setEntryAt(int index, LeafEntry e) {
        ((LeafNodePage) nodePage).setEntryAt(index, e);
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public int find(int key) {
        for (int i = 0; i < getSize(); i++) {
            if (getKeyAt(i) == key) {
                return i;
            }
            if (getKeyAt(i) > key) {
                return -1;
            }
        }
        return -1;
    }

    @Override
    protected Node split() throws IOException {
        int m = getSize() / 2;

        //TODO instantiation ???
        LeafNode newRightNode = new LeafNode();
        for (int i = m; i < getSize(); i++) {
            newRightNode.setKeyAt(i - m, getKeyAt(i));
            newRightNode.setEntryAt(i - m, getEntryAt(i));
            setKeyAt(i, null);
            setEntryAt(i, null);
        }
        newRightNode.setSize(getSize() - m);
        setSize(m);
        return newRightNode;
    }

    @Override
    protected Node pushToParent(int key, Node leftChild, Node rightChild) {
        throw new UnsupportedOperationException("cant use Lead as Parent");
    }

    @Override
    protected void transferChildren(Node receiver, Node donor, int donationIndex) {
        throw new UnsupportedOperationException("cant use Lead as Parent");
    }

    @Override
    protected Node FuseChildren(Node leftChild, Node rightChild) {
        throw new UnsupportedOperationException("cant use Lead as Parent");
    }

    @Override
    protected void FuseWithSibling(int separationKey, Node rightNode) throws IOException {
        LeafNode Leaf = (LeafNode) rightNode;
        int initialSize = getSize();
        for (int i = 0; i < Leaf.getSize(); i++) {
            setKeyAt(initialSize + i, Leaf.getKeyAt(i));
            setEntryAt(initialSize + i, Leaf.getEntryAt(i));
        }
        setSize(getSize() + Leaf.getSize());

        setRightNode(Leaf.getRightNode());
        if (getRightNode() != null) {
            getRightNode().setLeftNode(this);
        }

        //TODO flush everything
        rightNode.close();
    }

    @Override
    protected Integer getKeyFromSibling(int separationKey, Node sibling, int donationIndex) {
        LeafNode Leaf = (LeafNode) sibling;
        insertKey(Leaf.getKeyAt(donationIndex), Leaf.getEntryAt(donationIndex));
        Leaf.deleteAt(donationIndex);
        if (donationIndex == 0) {
            return Leaf.getKeyAt(0);
        }
        return getKeyAt(0);
    }

    public void insertKey(int key, LeafEntry entry) {
        int index = 0;
        for (; index < getSize(); index++) {
            if (getKeyAt(index) >= key) {
                break;
            }
        }
        insertAt(index, key, entry);
    }

    private void insertAt(int index, int key, LeafEntry entry) {
        for (int i = getSize(); i > index; i--) {
            setKeyAt(i, getKeyAt(i - 1));
            setEntryAt(i, getEntryAt(i - 1));
        }

        setKeyAt(index, key);
        setEntryAt(index, entry);
        setSize(getSize() + 1);
    }

    public boolean delete(int key) {
        return deleteAt(find(key));
    }

    public boolean deleteAt(int index) {
        if (index == -1) {
            return false;
        }
        for (int i = index; i < getSize() - 1; i++) {
            setKeyAt(i, getKeyAt(i + 1));
            setEntryAt(i, getEntryAt(i + 1));
        }
        setKeyAt(getSize(), null);
        setEntryAt(getSize(), null);
        setSize(getSize() - 1);
        return true;
    }

    public Integer getRightNodePageId(){
        return nodePage.getRightNodePageId();
    }
}
