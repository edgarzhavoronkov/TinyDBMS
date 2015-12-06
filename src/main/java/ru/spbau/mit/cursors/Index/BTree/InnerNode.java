package ru.spbau.mit.cursors.Index.BTree;

import java.util.Objects;

/**
 * Created by gellm_000 on 06.12.2015.
 */
public class InnerNode extends Node{
    private final static int INNERCAPACITY = 100;
    private Integer[] children;

    public InnerNode(){
        // TODO check if filled with null
        keys = new Integer[INNERCAPACITY + 1];
        children = new Integer[INNERCAPACITY + 2];
    }

    public Integer getChildPageAt(int index){
        return children[index];
    }

    public Node getChild(int index){
        if(getChildPageAt(index) == null){
            return null;
        }
        // TODO add construction getChildPageAt(index)
        Node child = null;

        return child;

    }

    public void setChildPageAt(int index, Integer value){
        children[index] = value;
        if(value != null) {
            //TODO get Node by value
            Node child = null;
            child.setParentNode(this);
        }
    }

    private void insertAt(int index, int key, Node left, Node right){
        for(int i = getSize(); i > index; i--){
            setKeyAt(i, getKeyAt(i-1));
        }
        for(int i = getSize() + 1; i > index; i--){
            setKeyAt(i, getChildPageAt(i - 1));
        }
        setKeyAt(index, key);
        setChildPageAt(index, left.getPageId());
        setChildPageAt(index + 1, right.getPageId());
        setSize(getSize() + 1);
    }

    private void deleteAt(int index){
        for(int i = index; i < getSize() - 1; i++){
            setKeyAt(i, getKeyAt(i+1));
            setChildPageAt(i + 1, getChildPageAt(i + 2));
        }

        setKeyAt(getSize() - 1, null);
        setChildPageAt(getSize(), null);
        setSize(getSize() - 1);
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public int find(int key) {
        for(int i = 0; i < getSize(); i++){
            if(getKeyAt(i) == key){
                return i + 1;
            }
            if(getKeyAt(i) > key){
                return i;
            }
        }
        return getSize();
    }

    @Override
    protected Node split() {
        int m = getSize()/2;
        // TODO construction
        // TODO WTF with -1 in all places. Need check
        InnerNode newRightNode = new InnerNode();
        for(int i = m + 1; i < getSize(); i++){
            newRightNode.setKeyAt(i - m - 1, getKeyAt(i));
            setKeyAt(i, null);
        }
        for(int i = m + 1; i < getSize() + 1; i++){
            newRightNode.setChildPageAt(i - m - 1, getChildPageAt(i));
            // TODO mb we can remove next line
            newRightNode.getChild(i - m - 1).setParentNode(newRightNode);
            setChildPageAt(i, null);
        }
        setKeyAt(m, null);
        newRightNode.setSize(getSize() - m - 1);
        setSize(m);

        return newRightNode;
    }

    @Override
    protected Node pushToParent(int key, Node leftChild, Node rightChild) {
        insertAt(find(key), key, leftChild, rightChild);
        if(isFull()){
            return resolveOversize();
        }

        if(getParentNodePageID() == null){
            return this;
        }
        return null;
    }

    @Override
    protected void transferChildren(Node receiver, Node donor, int donationIndex) {
        int recieverIndex = getSize() + 1;
        for(int i = 0; i < getSize() + 1; i++){
            if(Objects.equals(getChildPageAt(i), receiver.getPageId())){
                recieverIndex = i;
                break;
            }
        }
        if(donationIndex == 0){
            Integer key = receiver.getKeyFromSibling(getKeyAt(recieverIndex), donor, donationIndex);
            setKeyAt(recieverIndex, key);
        }else{
            Integer key = receiver.getKeyFromSibling(getKeyAt(recieverIndex - 1), donor, donationIndex);
            setKeyAt(recieverIndex - 1, key);
        }

    }

    @Override
    protected Node FuseChildren(Node leftChild, Node rightChild) {
        int index = getSize();
        for(int i = 0; i < getSize(); i++){
            if(Objects.equals(getChildPageAt(i), leftChild.getPageId())){
                index = i;
                break;
            }
        }
        Integer key = getKeyAt(index);
        leftChild.FuseWithSibling(key, rightChild);
        deleteAt(index);
        if(isTooEmpty()){
            if(getParentNodePageID() == null){
                if(getSize() == 0){
                    leftChild.setParentNodePageId(null);
                    return leftChild;
                }
                return null;
            }
            return resolveUnderflow();
        }
        return null;
    }

    @Override
    protected void FuseWithSibling(int separationKey, Node rightNode) {
        InnerNode Inner = (InnerNode) rightNode;
        int initialSize = getSize();
        setKeyAt(initialSize, separationKey);
        initialSize ++;
        for(int i = 0; i < Inner.getSize(); i++){
            setKeyAt(initialSize + i, Inner.getKeyAt(i));
        }
        for(int i = 0; i < Inner.getSize() + 1; i++){
            setChildPageAt(initialSize + i, Inner.getChildPageAt(i));
        }
        setSize(getSize() + 1 + Inner.getSize());
        setRightNode(Inner.getRightNode());
        if(getRightNode() != null){
            getRightNode().setLeftNode(this);
        }

        //TODO close evcerything
        rightNode.close();
    }

    @Override
    protected Integer getKeyFromSibling(int separationKey, Node sibling, int donationIndex) {
        InnerNode inner = (InnerNode) sibling;
        Integer key = null;
        if(donationIndex == 0){
            int index = getSize();
            setKeyAt(index, separationKey);
            setChildPageAt(index + 1, inner.getChildPageAt(donationIndex));
            setSize(getSize() + 1);
            key = inner.getKeyAt(donationIndex);
            inner.deleteAt(donationIndex);
        }else{
            // TODO check
            insertAt(0, separationKey, inner.getChild(donationIndex + 1), getChild(0));
            key = inner.getKeyAt(donationIndex);
            inner.deleteAt(donationIndex);
        }
        return key;
    }
}
