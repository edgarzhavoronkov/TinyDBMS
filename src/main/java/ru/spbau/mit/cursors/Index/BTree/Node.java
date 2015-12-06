package ru.spbau.mit.Cursors.Index.BTree;

import ru.spbau.mit.memory.Page;

import java.util.ArrayList;

/**
 * Created by gellm_000 on 06.12.2015.
 */
abstract class Node {
    protected Integer[] keys;
    protected int size;
    protected Integer pageId;
    protected Integer leftNodePageId;
    protected Integer rightNodePageId;
    protected Integer parentNodePageId;

    protected Node(){
        size = 0;
        // TODO Allocate memmory for self
        pageId = null;
        leftNodePageId = null;
        parentNodePageId = null;
        rightNodePageId = null;
    }


    public int getSize(){
        return size;
    }

    public Integer getKeyAt(int index){
        return keys[index];
    }

    public void setKeyAt(int index, int value){
        keys[index] = value;
    }

    public Integer getParentNodePageID(){
        return parentNodePageId;
    }

    public void setParentNodePageId(int id){
        parentNodePageId = id;
    }

    public Integer getLeftNodePageId(){
        return leftNodePageId;
    }

    public void setLeftNodePageId(int id){
        leftNodePageId = id;
    }

    public Integer getRightNodePageId(){
        return rightNodePageId;
    }

    public void setRightNodePageId(int id){
        rightNodePageId = id;
    }

    public abstract boolean isLeaf();

    public abstract int find(int key);

    protected abstract Node split();

    protected abstract Node pushToParent(int key, Node child, Node)

    public boolean isFull(){
        return getSize() == keys.length;
    }

    public Node resolveOversize(){
        int splitKey = keys[keys.length / 2];
        Node newRightNode = split();

        if(getParentNodePageID() == null){
            setParentNodePageId((new InnerNode()).pageId);
        }

        newRightNode.setParentNodePageId(getParentNodePageID());
        newRightNode.setLeftNodePageId(getRightNodePageId());
        if(getRightNodePageId() != null){
            newRightNode.setRightNodePageId(getRightNodePageId());
            // TODO factory instantiation
            // TODO is instantiation correct?
            Node rightNode;
        }


    }
}
